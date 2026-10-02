# Realtime Student Information Management App

Ứng dụng Android quản lý thông tin sinh viên thời gian thực, dùng Firebase Firestore. Bài giữa kỳ môn Phát triển ứng dụng di động (503074). Đề bài: [`docs/Mid-term.pdf`](docs/Mid-term.pdf).

> **Trạng thái:** đang phát triển. Hiện mới có khung dự án và bộ tài liệu thiết kế. Tính năng sẽ được đánh dấu hoàn thành trong [`project-progress.xlsx`](project-progress.xlsx) khi đã lập trình và kiểm thử xong.

## Tính năng theo vai trò

- **Admin:** tài khoản tích hợp sẵn, toàn quyền. Là người duy nhất tạo được tài khoản mới.
- **Manager:** mọi chức năng liên quan đến sinh viên và chứng chỉ (xem, thêm, sửa, xóa, tìm kiếm, sắp xếp, import/export CSV).
- **Employee:** chỉ xem, và đổi ảnh đại diện của chính mình.

Chi tiết: [`docs/SRS.md`](docs/SRS.md), [`docs/rbac-matrix.md`](docs/rbac-matrix.md).

## Công nghệ

- **Nền tảng:** Android (Java 17), `minSdk` 26, `targetSdk` 34
- **Kiến trúc:** MVVM (ViewModel, LiveData, ViewBinding, Navigation Component)
- **Firebase:** Authentication (email/mật khẩu), Firestore, Storage (ảnh đại diện)
- **Thư viện ảnh:** Glide

## Cài đặt và chạy

Các bước đầy đủ, kèm khắc phục sự cố: [`docs/test-and-setup.md`](docs/test-and-setup.md). Tóm tắt:

1. Android Studio Iguana (2023.2.1) trở lên, JDK 17.
2. Tạo Firebase project, bật Authentication (Email/Password), Firestore, Storage.
3. Tải `google-services.json`, đặt vào `app/` (mẫu: `app/google-services.json.example`).
4. Publish `firestore.rules` trong Firebase Console.
5. Chạy ứng dụng trên máy ảo hoặc thiết bị Android 8.0+. Lần chạy đầu app tự tạo tài khoản Admin.

## Tài khoản demo

| Vai trò | Email | Mật khẩu |
|---|---|---|
| Admin | `admin@student.app` | `Admin@123456` |
| Manager | Do Admin tạo | Đặt lúc tạo |
| Employee | Do Admin tạo | Đặt lúc tạo |

Đây là tài khoản demo cho project Firebase của bài tập. Email chỉ là định danh đăng nhập, không cần là hộp thư thật.

## Tài liệu

| File | Nội dung |
|---|---|
| [`docs/SRS.md`](docs/SRS.md) | Yêu cầu và đối chiếu với đề bài |
| [`docs/firestore-schema.md`](docs/firestore-schema.md) | Cấu trúc dữ liệu Firestore |
| [`docs/design.md`](docs/design.md), [`docs/uml.md`](docs/uml.md) | Thiết kế và biểu đồ |
| [`docs/test-and-setup.md`](docs/test-and-setup.md) | Cài đặt và test case |
| [`docs/folder-structure.md`](docs/folder-structure.md) | Cấu trúc thư mục và danh sách đầy đủ các file trong `docs/` |
| [`CONTRIBUTING.md`](CONTRIBUTING.md) | Quy ước commit và nhánh |
| [`sample-data/`](sample-data/) | CSV mẫu để thử import |

## Nhóm thực hiện

- [Họ tên – MSSV] (Thành viên A)
- [Họ tên – MSSV] (Thành viên B)

Giảng viên phụ trách: [điền theo mẫu của Khoa]
