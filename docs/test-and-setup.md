# Hướng Dẫn Cài Đặt và Kiểm Thử

## Hướng dẫn cài đặt và chạy dự án

### Yêu cầu
- Android Studio **Iguana (2023.2.1)** trở lên (project dùng Android Gradle Plugin 8.3.2)
- JDK 17
- Android SDK API 34 (`compileSdk`/`targetSdk` = 34)
- Máy ảo hoặc thiết bị thật chạy **Android 8.0 (API 26)** trở lên
- Tài khoản Google / Firebase

### Các bước cài đặt

1. **Clone dự án**
   ```bash
   git clone https://github.com/chimpk/student-management-midterm.git
   ```
   Mở thư mục vừa clone bằng Android Studio.

2. **Tạo Firebase project**
   - Vào [console.firebase.google.com](https://console.firebase.google.com), tạo project mới, thêm ứng dụng Android.
   - Package name: `com.example.studentmgmt`

3. **Bật các dịch vụ Firebase**
   - Authentication → Sign-in method → **Email/Password** → Bật
   - Firestore Database → Tạo (chế độ production)
   - Storage → Bắt đầu (dùng cho ảnh đại diện)

4. **Thêm `google-services.json`**
   - Tải file từ Firebase Console, đặt vào thư mục `app/` (xem mẫu `app/google-services.json.example`).
   - File này bị `.gitignore`, **không** đưa lên repo công khai.

5. **Triển khai Security Rules**
   - Firebase Console → Firestore Database → Rules → dán nội dung `firestore.rules` → **Publish**.
   - `firestore.indexes.json` hiện rỗng và không cần triển khai (xem `firestore-schema.md` mục 5).
   - Muốn dùng Firebase CLI (`firebase deploy --only firestore`) thì cần tạo `firebase.json` trước bằng `firebase init firestore`.

6. **Tài khoản Admin lần đầu**
   - Chạy ứng dụng lần đầu: app tự kiểm tra và tạo tài khoản Admin nếu chưa có (FR-ACC-04).
   - Email: `admin@student.app` | Mật khẩu: `Admin@123456`
   - Đây là tài khoản demo cho project Firebase của bài tập, cũng là tài khoản gửi cho giám khảo. Email chỉ là định danh đăng nhập, không cần là hộp thư thật.

7. **Chạy ứng dụng**
   - Gradle Sync, chọn máy ảo hoặc thiết bị (API 26+), nhấn Run ▶.

### Khắc phục sự cố

| Lỗi | Nguyên nhân | Cách sửa |
|---|---|---|
| Build báo thiếu `google-services.json` hoặc Firebase không khởi tạo | Chưa có file cấu hình | Tải từ Firebase Console, đặt vào `app/` rồi Sync lại |
| `PERMISSION_DENIED` (Firestore) | Rules chưa publish hoặc chưa có luật cho collection đó | Publish `firestore.rules`, kiểm tra luật của collection đang dùng |
| Không thấy dữ liệu thời gian thực | Listener chưa đăng ký | Kiểm tra `addSnapshotListener` trong Repository và việc observe `LiveData` ở Fragment |
| Import CSV lỗi encoding | File không phải UTF-8 | Excel → Lưu dưới dạng **CSV UTF-8** |
| Lỗi khi đổi avatar | Chưa bật Storage hoặc Storage Rules từ chối ghi | Bật Storage, kiểm tra Storage Rules |
| Tài khoản bị khóa vẫn đăng nhập được | Thiếu bước kiểm tra | Đọc `status` từ `users/{uid}` sau khi Auth thành công |
| Logcat báo cần index | Truy vấn `where` + `orderBy` khác trường | Bấm link trong logcat để tạo, rồi thêm vào `firestore.indexes.json` |
| Build lỗi `minSdk` | Thiết bị chạy Android dưới 8.0 | Dùng máy ảo API 26 trở lên |

---

## Kiểm thử

### Tài khoản kiểm thử

| Vai trò | Email | Mật khẩu |
|---|---|---|
| Admin | `admin@student.app` | `Admin@123456` |
| Manager | Do Admin tạo khi test (FR-USR-02) | Đặt lúc tạo |
| Employee | Do Admin tạo khi test (FR-USR-02) | Đặt lúc tạo |

Ghi tài khoản Manager/Employee dùng để demo vào phụ lục báo cáo.

### Dữ liệu mẫu

Các file trong [`../sample-data/`](../sample-data/): `students.csv` (20 sinh viên hợp lệ), `students-invalid.csv` (có dòng lỗi), `certificates.csv`.

### Bảng test cases

| Mã | FR | Vai trò | Bước thực hiện | Kết quả mong đợi | Kết quả | Ưu tiên |
|---|---|---|---|---|---|---|
| TC-01 | FR-ACC-01 | Tất cả | Nhập email/mật khẩu đúng → Đăng nhập | Vào màn hình chính, hiện đúng mục theo vai trò | – | Cao |
| TC-02 | FR-ACC-01 | Tất cả | Nhập mật khẩu sai | Hiện lỗi "Sai thông tin đăng nhập" | – | Cao |
| TC-03 | FR-ACC-01 | Employee | Tài khoản bị khóa → Đăng nhập | Từ chối, hiện "Tài khoản bị khóa" | – | Cao |
| TC-04 | FR-ACC-02 | Employee | Chọn ảnh từ thư viện → Lưu | Avatar cập nhật ngay | – | Trung |
| TC-05 | FR-USR-02 | Admin | Thêm user với tuổi 17 | Hiện lỗi "Tuổi phải từ 18–60" | – | Cao |
| TC-06 | FR-USR-02 | Admin | Thêm user với SĐT 9 số | Hiện lỗi "SĐT không hợp lệ" | – | Cao |
| TC-07 | FR-USR-02 | Manager | Truy cập chức năng thêm user | Bị từ chối (nút ẩn hoặc Firestore trả `PERMISSION_DENIED`) | – | Cao |
| TC-08 | FR-USR-05 | Admin | Khóa tài khoản Manager | Manager không đăng nhập được | – | Cao |
| TC-09 | FR-STU-02 | Manager | Thêm sinh viên đầy đủ thông tin | Sinh viên xuất hiện trong danh sách ngay | – | Cao |
| TC-10 | FR-STU-02 | Employee | Nhấn nút Thêm sinh viên | Nút bị ẩn hoặc hiện lỗi quyền | – | Cao |
| TC-11 | FR-STU-05 | Tất cả | Tìm kiếm "Nguyễn" | Hiện tất cả sinh viên có tên chứa "Nguyễn" | – | Cao |
| TC-12 | FR-STU-05 | Tất cả | Tìm kiếm tên có dấu "Thị" | Kết quả đúng, không mất dấu tiếng Việt | – | Cao |
| TC-13 | FR-STU-06 | Tất cả | Sắp xếp theo tên A–Z | Danh sách đúng thứ tự | – | Trung |
| TC-14 | FR-CER-02 | Manager | Thêm chứng chỉ cho sinh viên | Chứng chỉ xuất hiện trong subcollection `certificates` | – | Cao |
| TC-15 | FR-CER-04 | Employee | Xóa chứng chỉ | Bị từ chối | – | Cao |
| TC-16 | FR-IO-01 | Manager | Import `sample-data/students.csv` (20 sinh viên hợp lệ) | 20 sinh viên được thêm vào Firestore | – | Cao |
| TC-17 | FR-IO-01 | Manager | Import `sample-data/students-invalid.csv` (1 dòng thiếu Mã SV) | Dòng lỗi bị bỏ qua, báo cáo rõ | – | Cao |
| TC-18 | FR-IO-01 | Manager | Import file không phải CSV | Hiện lỗi "Định dạng file không hỗ trợ" | – | Trung |
| TC-19 | FR-IO-02 | Admin | Export danh sách 50 sinh viên | File CSV tải về đúng 50 dòng, đủ cột | – | Cao |
| TC-20 | FR-IO-02 | Employee | Truy cập Export | Bị từ chối | – | Cao |
| TC-21 | FR-STU-01 | Manager (TB1) | Thêm SV → Manager khác (TB2) xem | TB2 thấy sinh viên mới trong < 2 giây | – | Cao |
| TC-22 | FR-ACC-01 | Admin | Đăng nhập đúng | Ghi vào `loginHistory`, thấy trong lịch sử | – | Trung |
| TC-23 | FR-USR-06 | Admin | Xem lịch sử đăng nhập của Manager | Hiện danh sách thời gian đúng | – | Trung |
| TC-24 | FR-IO-01 | Manager | Import `sample-data/students.csv` (tên có dấu tiếng Việt) | Dấu được giữ nguyên sau import | – | Cao |
| TC-25 | FR-STU-04 | Manager | Xóa sinh viên có chứng chỉ | Sinh viên + chứng chỉ đều bị xóa | – | Cao |
| TC-26 | FR-ACC-04 | – | Cài mới, chạy app lần đầu rồi đăng nhập bằng tài khoản Admin mặc định | Đăng nhập thành công, vào màn hình Admin | – | Cao |
| TC-27 | FR-IO-03 | Manager | Import `sample-data/certificates.csv` sau khi đã import sinh viên | Chứng chỉ xuất hiện đúng sinh viên theo Mã SV | – | Cao |
