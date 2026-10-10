# Thiết Kế Dữ Liệu Firestore

> Đây là **nguồn duy nhất** cho cấu trúc dữ liệu đích của khung ứng dụng đang phát triển. Quy tắc kiểm tra dữ liệu: [`SRS.md`](SRS.md) mục 4. Quy tắc bảo mật: [`../firestore.rules`](../firestore.rules).

## 1. Tổng quan

```
users/                          ← Collection
  {uid}/                        ← Document (uid = Firebase Auth UID)
    loginHistory/               ← Subcollection
      {logId}/                  ← Document (mỗi lần đăng nhập, ID tự sinh)

students/                       ← Collection
  {studentId}/                  ← Document (ID = Mã sinh viên)
    certificates/               ← Subcollection
      {certId}/                 ← Document (ID tự sinh)

Firebase Storage
  avatars/{uid}.{jpg|jpeg|png}  ← Ảnh đại diện, dưới 5 MiB
```

Quy ước giá trị lưu trữ:

| Trường | Giá trị lưu | Hiển thị trên giao diện |
|---|---|---|
| `role` | `admin`, `manager`, `employee` | Admin, Manager, Employee |
| `status` | `Normal`, `Locked` (theo đề bài) | Bình thường, Khóa |

---

## 2. Collection `users`

| Trường | Kiểu | Bắt buộc | Ví dụ |
|---|---|:---:|---|
| `uid` | string | ✔ | `"wR83kL9mPxZ1"` |
| `username` | string | ✔ | `"manager"` |
| `email` | string | ✖ | `"manager@student.app"` |
| `name` | string | ✔ | `"Nguyễn Văn A"` |
| `age` | number | ✔ | `28` |
| `phone` | string | ✔ | `"0912345678"` |
| `status` | string | ✔ | `"Normal"` hoặc `"Locked"` |
| `role` | string | ✔ | `"admin"`, `"manager"`, `"employee"` |
| `avatarUrl` | string | ✖ | `"https://firebasestorage.googleapis.com/..."` |
| `createdAt` | timestamp | ✔ | `2026-10-08T08:00:00Z` |

`name`, `age`, `phone`, `status` là các trường mà đề bài yêu cầu. `username` là định danh đăng nhập chính. `email` chỉ là thông tin bổ sung; khi có, ứng dụng có thể dùng nó làm bí danh đăng nhập. `role` phục vụ phân quyền. Tính duy nhất của username/email phải được bảo đảm bởi luồng cấp tài khoản trong môi trường tin cậy; Security Rules chỉ kiểm tra hình dạng của từng document.

```json
{
  "uid": "wR83kL9mPxZ1",
  "username": "manager",
  "email": "manager@student.app",
  "name": "Nguyễn Văn A",
  "age": 28,
  "phone": "0912345678",
  "status": "Normal",
  "role": "manager",
  "avatarUrl": "https://firebasestorage.googleapis.com/...",
  "createdAt": "2026-10-08T08:00:00Z"
}
```

### Subcollection `users/{uid}/loginHistory`

| Trường | Kiểu | Bắt buộc | Ví dụ |
|---|---|:---:|---|
| `timestamp` | timestamp | ✔ | `2026-10-08T09:15:00Z` (server timestamp) |
| `device` | string | ✖ | `"Samsung Galaxy S21"` |

---

## 3. Collection `students`

Document ID = `studentId` (Mã SV) để đảm bảo duy nhất.

| Trường | Kiểu | Bắt buộc | Ví dụ |
|---|---|:---:|---|
| `studentId` | string | ✔ | `"524H0123"` |
| `name` | string | ✔ | `"Trần Thị B"` |
| `dateOfBirth` | string `YYYY-MM-DD` | ✔ | `"2002-05-15"` |
| `className` | string | ✔ | `"24H50301"` |
| `faculty` | string | ✔ | `"Công nghệ thông tin"` |
| `createdAt` | timestamp | ✔ | `2026-10-08T08:00:00Z` |

### Subcollection `students/{studentId}/certificates`

| Trường | Kiểu | Bắt buộc | Ví dụ |
|---|---|:---:|---|
| `certName` | string | ✔ | `"TOEIC 750"` |
| `issueDate` | string `YYYY-MM-DD` | ✔ | `"2025-06-01"` |
| `issuedBy` | string | ✔ | `"IIG Vietnam"` |

### Quy tắc MSSV và mã lớp

- `studentId` có cấu trúc TDTU `KYYTSSSS` và khớp `^[0-9A-H][0-9]{2}[0H][0-9]{4}$`.
- Ứng dụng chỉ kiểm tra MSSV, không tự sinh MSSV từ họ tên hoặc thứ tự danh sách.
- `className` có đúng 8 chữ hoa hoặc chữ số và khớp `^[0-9A-Z]{8}$`.
- Các mẫu ngành CNTT `02`, `03`, `04` và nhóm `H5` mới chỉ là dữ liệu quan sát, chưa được xem là quy tắc chính thức của TDTU.

---

## 4. Định dạng file CSV

Dùng chung cho import và export. Dòng đầu là tiêu đề. Xem mẫu ở [`../sample-data/`](../sample-data/).

| File | Các cột (theo thứ tự) |
|---|---|
| Sinh viên | `studentId,name,dateOfBirth,className,faculty` |
| Chứng chỉ | `studentId,certName,issueDate,issuedBy` |

- Export ghi **UTF-8 có BOM** (để Excel hiển thị đúng tiếng Việt). Import đọc UTF-8 có hoặc không có BOM.
- Dòng sai (thiếu trường bắt buộc, sai định dạng ngày, Mã SV trùng hoặc không tồn tại) bị bỏ qua và được báo cáo.

---

## 5. Truy vấn và Index

| Chức năng | Cách thực hiện | Index |
|---|---|---|
| Danh sách sinh viên | `addSnapshotListener` trên `students` | Tự động |
| Tìm kiếm đa tiêu chí (tên, mã SV, lớp, khoa) | Lọc **phía client** trên danh sách đã tải. Firestore không tìm theo chuỗi con và không kết hợp nhiều điều kiện khoảng. | Không cần |
| Sắp xếp (tên, ngày sinh, lớp) | Sắp xếp phía client trên danh sách đã tải | Không cần |
| Danh sách chứng chỉ | Listener trên `students/{id}/certificates`, `orderBy("issueDate", DESCENDING)` | Tự động |
| Lịch sử đăng nhập | `orderBy("timestamp", DESCENDING)` | Tự động |

Vì vậy [`firestore.indexes.json`](../firestore.indexes.json) để rỗng là đúng. Nếu sau này dùng truy vấn kết hợp `where` và `orderBy` trên các trường khác nhau thì mới cần bổ sung composite index.

---

## 6. Bảo mật

Quyền theo collection (khớp [`rbac-matrix.md`](rbac-matrix.md)):

| Đường dẫn | Đọc | Ghi |
|---|---|---|
| `users/{uid}` | Chính chủ hoặc Admin | Admin. Chính chủ chỉ được cập nhật `avatarUrl` của mình |
| `users/{uid}/loginHistory/{id}` | Admin hoặc chính chủ | Người dùng đã đăng nhập (ghi lịch sử của chính mình) |
| `students/{id}` | Đã đăng nhập | Admin, Manager |
| `students/{id}/certificates/{id}` | Đã đăng nhập | Admin, Manager |

Rules hiện tại đã bao phủ `users`, `loginHistory`, `students`, `certificates` và quyền chủ sở hữu đối với avatar. Không có đường bootstrap Admin từ ứng dụng client; Admin đầu tiên phải được cấp trong môi trường tin cậy.

---

## 7. Ghi chú kỹ thuật

- **Realtime listener:** đăng ký `addSnapshotListener()` trong Repository, gọi `ListenerRegistration.remove()` khi ViewModel bị hủy (`onCleared()`).
- **Ghi theo lô:** import CSV ghi tối đa **400 document mỗi batch** (`WriteBatch`) để chừa dư địa cho các thao tác phụ.
- **Offline:** bật persistence qua `setPersistenceEnabled(true)` (hoặc cài đặt tương đương của SDK đang dùng).
- **Cấp tài khoản demo:** chuẩn bị ba tài khoản `admin/admin@123456`, `manager/manager@123456`, `employee/employee@123456` trong môi trường tin cậy, rồi tạo `users/{uid}` với username và role tương ứng. Email có thể bỏ trống; nếu có thì được dùng như bí danh đăng nhập. Không đưa mật khẩu môi trường thật vào mã nguồn.
- **Giới hạn Firebase Auth:** provider Email/Password yêu cầu mật khẩu tối thiểu 6 ký tự; mật khẩu demo dạng `<username>@123456` đã đáp ứng yêu cầu này.
- **Xóa người dùng:** chỉ xóa document `users/{uid}`. Tài khoản Firebase Auth vẫn tồn tại (xóa Auth cần Admin SDK hoặc Cloud Functions), nhưng app từ chối đăng nhập khi không tìm thấy document (FR-ACC-01).
- **Xóa sinh viên:** phải xóa cả subcollection `certificates` (Firestore không tự xóa subcollection).
