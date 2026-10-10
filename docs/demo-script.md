# Kịch bản video demo dự kiến

> **Trạng thái:** BẢN NHÁP. Chỉ quay các chức năng đã triển khai và kiểm thử được.
>
> **Học phần:** Phát triển ứng dụng di động (503074)
>
> **Thời lượng tối đa:** 20 phút; độ phân giải tối thiểu 720p.

## 1. Nguyên tắc sử dụng

- Không nói “đã hoàn thành”, “đạt 100%” hoặc “27/27 test đạt” khi chưa có bằng chứng.
- Nếu chức năng chưa tồn tại, chuyển phần đó thành trình bày thiết kế hoặc bỏ khỏi video.
- Không hiển thị khóa dịch vụ, thông tin thanh toán hoặc dữ liệu cá nhân thật.
- Ghi rõ phiên bản, ngày quay, thiết bị và Firebase Project ID dùng cho demo.

## 2. Chuẩn bị

- Thiết bị hoặc máy ảo Android API 26–34 có Google Play Services.
- Firebase Console mở sẵn Authentication, Firestore và Rules; chỉ mở Storage nếu đã cấu hình.
- File CSV mẫu trong thư mục `Download`.
- Ba tài khoản demo dự kiến được cấp từ môi trường đáng tin cậy, không seed hoặc nâng quyền từ ứng dụng:

```text
Admin: admin / admin
Manager: manager / manager
Employee: employee / employee
```

- Email không bắt buộc. Nếu tài khoản có email, có thể dùng email thay username khi đăng nhập.
- Mật khẩu demo (`admin@123456`, `manager@123456`, `employee@123456`) đã đáp ứng giới hạn tối thiểu 6 ký tự của Firebase Email/Password.

## 3. Kịch bản theo thời lượng

### 00:00–01:30 — Giới thiệu

- Giới thiệu học phần, đề tài, giảng viên, lớp/nhóm và thành viên.
- Nói rõ đây là dự án môn học và nêu trạng thái thực tế của sản phẩm.

### 01:30–04:30 — Nghiên cứu Firestore

- Trình bày collection, document và subcollection.
- Giải thích `users/{uid}`, `students/{studentId}` và `students/{studentId}/certificates/{certId}`.
- Giải thích snapshot listener, cache ngoại tuyến và giới hạn thao tác ghi.
- Nêu quy ước dự án: import chia nhóm tối đa **400 thao tác ghi**.

### 04:30–06:00 — Kiến trúc và dữ liệu

- Mở cây mã nguồn thật và chỉ giới thiệu lớp đang tồn tại.
- Nếu MVVM chưa triển khai, trình bày [UML](uml.md) dưới dạng thiết kế dự kiến.
- Schema sinh viên gồm `studentId`, `name`, `dateOfBirth`, `className`, `faculty`, `createdAt`; không demo GPA.
- Giải thích MSSV `KYYTSSSS`, ví dụ `524H0123`, và mã lớp đúng 8 ký tự chữ hoa/số.

### 06:00–08:30 — Xác thực và phân quyền

Chỉ thực hiện nếu chức năng đã triển khai:

1. Đăng nhập sai và quan sát thông báo.
2. Đăng nhập tài khoản `Locked` và xác minh bị từ chối.
3. Đăng nhập Admin bằng username `admin`; nếu Admin có email thì thử thêm trường hợp dùng email làm bí danh.
4. So sánh menu của ba vai trò.
5. Mở Console để chứng minh Admin được cấp ngoài client.

### 08:30–11:00 — Quản lý người dùng

Chỉ thực hiện nếu chức năng đã triển khai: xem danh sách, kiểm tra dữ liệu sai, tạo tài khoản Manager/Employee, khóa/mở khóa và xem lịch sử đăng nhập.

### 11:00–14:30 — Sinh viên và chứng chỉ

Chỉ thực hiện nếu chức năng đã triển khai:

1. Thêm sinh viên với MSSV `524H0123` và mã lớp hợp lệ.
2. Thử MSSV sai cấu trúc và xác minh ứng dụng từ chối.
3. Sửa thông tin không bao gồm GPA.
4. Thêm chứng chỉ, tìm kiếm tên tiếng Việt và sắp xếp theo tên.
5. Dùng Employee để xác minh không có quyền ghi.

### 14:30–16:30 — Nhập và xuất CSV

1. Import file sinh viên mẫu.
2. Import file lỗi và chỉ ra lỗi theo dòng.
3. Giải thích cách chia tối đa 400 thao tác ghi mỗi batch.
4. Export CSV và kiểm tra UTF-8, số cột, số dòng.

Không đọc sẵn số lượng bản ghi từ kịch bản; nêu số lượng quan sát được từ file và kết quả thực tế.

### 16:30–18:30 — Đồng bộ, ngoại tuyến và xóa dữ liệu

Chỉ thực hiện nếu đã kiểm thử:

1. Sửa tên hoặc lớp trên thiết bị 1.
2. Đo thời gian thiết bị 2 nhận thay đổi; không tuyên bố thời gian nếu chưa đo.
3. Tắt mạng và mô tả chính xác dữ liệu đọc được từ cache.
4. Xóa sinh viên có chứng chỉ và kiểm tra subcollection trên Console.

### 18:30–19:30 — Rules và bằng chứng test

- Giải thích quyền và cho xem rules đã triển khai.
- Trình bày lệnh, ngày, thiết bị và số test đạt/thất bại.
- Nếu test vẫn “Chờ test”, nói rõ chưa có kết quả nghiệm thu.

### 19:30–20:00 — Kết luận

- Tóm tắt những chức năng thật sự đã demo thành công.
- Nêu hạn chế và hướng hoàn thiện; không dùng số liệu chưa được chứng minh.

## 4. Checklist trước khi quay

- [ ] Đã điền giảng viên, lớp/nhóm và thành viên B.
- [ ] Đã điền Firebase Project ID và Storage Bucket.
- [ ] Admin đã được cấp từ môi trường đáng tin cậy.
- [ ] Từng chức năng đã chạy thử trên đúng APK sẽ quay.
- [ ] Kết quả test có ngày, người test, thiết bị và bằng chứng.
- [ ] Không còn nội dung GPA.
- [ ] Không để lộ khóa, dữ liệu cá nhân hoặc thông tin thanh toán.
- [ ] Video MP4, H.264, 720p trở lên và không quá 20 phút.
- [ ] Tên file: `[MaLop]_[Nhom]_[TenDeTai]_Demo.mp4`.
