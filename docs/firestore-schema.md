# Thiết Kế Dữ Liệu Firestore

> Đây là **nguồn duy nhất** cho cấu trúc dữ liệu. Quy tắc kiểm tra dữ liệu: [`SRS.md`](SRS.md) mục 4. Quy tắc bảo mật: [`../firestore.rules`](../firestore.rules).

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
  avatars/{uid}.jpg             ← Ảnh đại diện
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
| `email` | string | ✔ | `"manager1@student.app"` |
| `name` | string | ✔ | `"Nguyễn Văn A"` |
| `age` | number | ✔ | `28` |
| `phone` | string | ✔ | `"0912345678"` |
| `status` | string | ✔ | `"Normal"` hoặc `"Locked"` |
| `role` | string | ✔ | `"admin"`, `"manager"`, `"employee"` |
| `avatarUrl` | string | ✖ | `"https://firebasestorage.googleapis.com/..."` |
| `createdAt` | timestamp | ✔ | `2026-10-11T08:00:00Z` |

`name`, `age`, `phone`, `status` là các trường mà đề bài yêu cầu. `email` chỉ phục vụ đăng nhập Firebase Auth. `role` phục vụ phân quyền.

```json
{
  "uid": "wR83kL9mPxZ1",
  "email": "manager1@student.app",
  "name": "Nguyễn Văn A",
  "age": 28,
  "phone": "0912345678",
  "status": "Normal",
  "role": "manager",
  "avatarUrl": "https://firebasestorage.googleapis.com/...",
  "createdAt": "2026-10-11T08:00:00Z"
}
```

### Subcollection `users/{uid}/loginHistory`

| Trường | Kiểu | Bắt buộc | Ví dụ |
|---|---|:---:|---|
| `timestamp` | timestamp | ✔ | `2026-10-12T09:15:00Z` (server timestamp) |
| `device` | string | ✖ | `"Samsung Galaxy S21"` |

---

## 3. Collection `students`

Document ID = `studentId` (Mã SV) để đảm bảo duy nhất.

| Trường | Kiểu | Bắt buộc | Ví dụ |
|---|---|:---:|---|
| `studentId` | string | ✔ | `"SV2024001"` |
| `name` | string | ✔ | `"Trần Thị B"` |
| `dateOfBirth` | string `YYYY-MM-DD` | ✔ | `"2002-05-15"` |
| `className` | string | ✔ | `"20DTHD1"` |
| `faculty` | string | ✔ | `"Công nghệ thông tin"` |
| `createdAt` | timestamp | ✔ | `2026-10-11T08:00:00Z` |

### Subcollection `students/{studentId}/certificates`

| Trường | Kiểu | Bắt buộc | Ví dụ |
|---|---|:---:|---|
| `certName` | string | ✔ | `"TOEIC 750"` |
| `issueDate` | string `YYYY-MM-DD` | ✔ | `"2025-06-01"` |
| `issuedBy` | string | ✔ | `"IIG Vietnam"` |

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
| `users/{uid}` | Đã đăng nhập | Admin. Mọi người được cập nhật riêng `avatarUrl` của chính mình |
| `users/{uid}/loginHistory/{id}` | Admin hoặc chính chủ | Người dùng đã đăng nhập (ghi lịch sử của chính mình) |
| `students/{id}` | Đã đăng nhập | Admin, Manager |
| `students/{id}/certificates/{id}` | Đã đăng nhập | Admin, Manager |

**Việc còn phải làm ở `firestore.rules`:** file hiện tại mới bao phủ `users` và `students` (quyền ghi chỉ Admin hoặc Manager). Chưa có luật cho `loginHistory`, `certificates`, việc Employee tự đổi `avatarUrl`, và việc tạo document Admin lần đầu khi seed. Cho đến khi bổ sung, Firestore sẽ từ chối các thao tác này.

---

## 7. Ghi chú kỹ thuật

- **Realtime listener:** đăng ký `addSnapshotListener()` trong Repository, gọi `ListenerRegistration.remove()` khi ViewModel bị hủy (`onCleared()`).
- **Batch write:** import CSV ghi tối đa **500 document/batch** (`WriteBatch`).
- **Offline:** bật persistence qua `setPersistenceEnabled(true)` (hoặc cài đặt tương đương của SDK đang dùng).
- **Seed Admin:** `AdminSeedService` chạy lúc khởi động. Nếu chưa có tài khoản Admin thì tạo tài khoản Firebase Auth với email/mật khẩu mặc định ([`test-and-setup.md`](test-and-setup.md)) và document `users/{uid}` có `role: "admin"`, `status: "Normal"`.
- **Xóa người dùng:** chỉ xóa document `users/{uid}`. Tài khoản Firebase Auth vẫn tồn tại (xóa Auth cần Admin SDK hoặc Cloud Functions), nhưng app từ chối đăng nhập khi không tìm thấy document (FR-ACC-01).
- **Xóa sinh viên:** phải xóa cả subcollection `certificates` (Firestore không tự xóa subcollection).
