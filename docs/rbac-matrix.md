# Ma trận phân quyền theo vai trò

> **Trạng thái:** tài liệu thiết kế cho khung ứng dụng đang phát triển. Quyền phía server được mô tả theo [`../firestore.rules`](../firestore.rules) và [`../storage.rules`](../storage.rules); quyền trên giao diện phải triển khai và kiểm thử riêng.

## 1. Vai trò

| Vai trò | Phạm vi |
|---|---|
| **Admin** | Quản lý tài khoản, lịch sử đăng nhập, sinh viên, chứng chỉ và nhập/xuất dữ liệu. |
| **Manager** | Quản lý sinh viên, chứng chỉ và nhập/xuất dữ liệu; không quản lý tài khoản. |
| **Employee** | Chỉ xem sinh viên và chứng chỉ; được đổi avatar và xem lịch sử đăng nhập của chính mình. |

Admin đầu tiên phải được cấp bằng Firebase Console hoặc Admin SDK/Cloud Functions. Ứng dụng client không được tự tạo hoặc tự nâng quyền thành Admin.

## 2. Ma trận chức năng ứng dụng

| Chức năng | Admin | Manager | Employee |
|---|:---:|:---:|:---:|
| Đăng nhập và đăng xuất | Có | Có | Có |
| Xem, tìm kiếm, sắp xếp sinh viên | Có | Có | Có |
| Xem chứng chỉ | Có | Có | Có |
| Thêm, sửa, xóa sinh viên | Có | Có | Không |
| Thêm, sửa, xóa chứng chỉ | Có | Có | Không |
| Nhập/xuất CSV | Có | Có | Không |
| Xem danh sách người dùng | Có | Không | Không |
| Tạo, sửa, khóa hoặc xóa người dùng | Có | Không | Không |
| Xem lịch sử đăng nhập của người khác | Có | Không | Không |
| Xem lịch sử đăng nhập của chính mình | Có | Có | Có |
| Đổi avatar của chính mình | Có | Có | Có |

## 3. Ma trận Firestore

| Đường dẫn | Đọc | Tạo | Cập nhật | Xóa |
|---|---|---|---|---|
| `users/{uid}` | Chính chủ hoặc Admin | Admin | Admin; chính chủ chỉ được đổi `avatarUrl` | Admin |
| `users/{uid}/loginHistory/{logId}` | Chính chủ hoặc Admin | Chính chủ | Admin | Admin |
| `students/{studentId}` | Mọi người đã đăng nhập | Admin, Manager | Admin, Manager | Admin, Manager |
| `students/{studentId}/certificates/{certId}` | Mọi người đã đăng nhập | Admin, Manager | Admin, Manager | Admin, Manager |

Quyền Admin/Manager được đọc từ document `users/{request.auth.uid}` đã được bảo vệ, không lấy từ dữ liệu do client gửi trong yêu cầu ghi. Các thao tác tạo và cập nhật đều phải vượt qua cùng bộ kiểm tra kiểu, trường cho phép và giới hạn độ dài; `createdAt` không được thay đổi khi cập nhật.

## 4. Ma trận Storage

| Đường dẫn | Đọc | Tạo/Cập nhật | Xóa |
|---|---|---|---|
| `avatars/{uid}.jpg`, `.jpeg`, `.png` | Mọi người đã đăng nhập | Chỉ UID trùng tên tệp; JPEG/PNG; nhỏ hơn 5 MiB | Chỉ UID trùng tên tệp |

## 5. Phòng vệ nhiều lớp

- Giao diện ẩn hoặc vô hiệu hóa thao tác không thuộc vai trò để tránh nhầm lẫn.
- ViewModel/Repository kiểm tra quyền trước khi gửi yêu cầu và hiển thị lỗi tiếng Việt.
- Firestore/Storage Rules là lớp thực thi quyền cuối cùng; việc ẩn nút không phải biện pháp bảo mật.
- Tài khoản có `status: "Locked"` phải bị đăng xuất sau khi đọc hồ sơ người dùng, dù dịch vụ xác thực đã xác nhận danh tính.
- Mọi quyền trong bảng phải được kiểm thử bằng Firebase Emulator trước khi báo cáo là hoàn thành.
