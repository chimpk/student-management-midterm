# Phân Tích Khoảng Cách Giữa Thiết Kế (Mockup) Và Hệ Thống Thật (Gap Analysis)

Tài liệu này phân tích các chi tiết trong `docs/Untitled.pdf` và `docs/Untitled (1).pdf` so với mã nguồn, schema Firestore và phạm vi dự án. Việc đã có mockup/spec không đồng nghĩa chức năng đã được triển khai.

> [!NOTE]
> Các chi tiết trong mockup không bắt buộc phải được hỗ trợ hoàn toàn bởi backend nếu đề bài không yêu cầu hoặc đang nằm ngoài ranh giới phân rã của từng thành viên. Fallback/mock phải gắn nhãn demo và không được dùng làm bằng chứng nghiệm thu chức năng backend.

---

## Bảng Phân Tích Chi Tiết

| Hạng mục trong Mockup | Xuất hiện tại | Trạng thái hiện tại | Chủ sở hữu | Ghi chú kỹ thuật & Hướng xử lý |
|---|---|:---:|:---:|---|
| **Đăng nhập bằng Username** | Màn hình Login (P1-3) | `ĐÃ CÓ` | Thành viên 1 | Đã giải quyết qua `DefaultUsernameAuthResolver` ánh xạ an toàn sang Firebase Auth. |
| **Admin tạo tài khoản hoàn chỉnh** | S40–S44 | `ĐÃ CÓ DESIGN, CHƯA CÓ BACKEND` | Thành viên 1 | Không có tự đăng ký. Cần Cloud Function/backend dùng Admin SDK tạo Auth rồi ghi `users/{uid}` mà không đổi phiên Admin. Email tùy chọn, không giới hạn tên miền. |
| **Quên mật khẩu?** | Màn hình Login (P1-3) | `CHƯA CÓ BACKEND` | Thành viên 1 | Mockup có nút "Quên mật khẩu?", nhưng đề bài không yêu cầu luồng reset password. Phản hồi bằng Dialog/Snackbar thông báo liên hệ Quản trị viên, không gọi API giả. |
| **Trạng thái "Đã đồng bộ"** | Dashboard, Danh sách SV | `MỘT PHẦN` | Cả hai | Phần User dùng Firestore listener; Student/Certificate hiện mặc định vẫn dùng repository in-memory nên chưa được gắn nhãn đã đồng bộ cloud. |
| **Trường "Đơn vị" trong User** (vd: "Phòng Đào tạo") | Hồ sơ cá nhân (P17, P35) | `CHƯA CÓ SCHEMA` | Thành viên 1 | Schema Firestore `users` chuẩn đề bài chỉ có: `name`, `age`, `phone`, `status`, `role`. Trường `unit` chỉ là mockup UI trình bày, không bắt buộc thêm vào schema DB để tránh vi phạm Security Rules. |
| **Vị trí đăng nhập "Hà Nội"** | Lịch sử đăng nhập (P11, P17) | `CHƯA CÓ BACKEND` | Thành viên 1 | PDF chỉ yêu cầu xem lịch sử đăng nhập, không quy định các trường `device`, `timestamp` hay vị trí. Device/time là schema do nhóm chọn; vị trí địa lý là mở rộng ngoài đề. |
| **Hệ điều hành "Android 14"** | Lịch sử đăng nhập (P11, P17) | `ĐÃ CÓ` | Thành viên 1 | Ứng dụng đọc trực tiếp từ `Build.VERSION.RELEASE` và ghép vào trường `device` an toàn. |
| **Biểu tượng Chuông thông báo** | Header Dashboard | `CHƯA CÓ BACKEND` | Thành viên 1 | Thông báo push nằm ngoài phạm vi đề tài theo `SRS.md`. Nút chuông chỉ đóng vai trò mockup UI, click hiển thị "Chưa có thông báo mới". |
| **Thẻ Import/Export trên Home** | Dashboard (P4, P28) | `NGOÀI PHẠM VI THÀNH VIÊN 1` | Thành viên 2 | Chức năng nhập/xuất CSV thuộc phân rã của Thành viên 2. Đã scaffold `StudentCsvHelper` và nút mở. |
| **Danh sách & Chi tiết Sinh viên** | Màn hình Sinh viên (P7-8) | `DEMO IN-MEMORY` | Thành viên 2 | `StudentListFragment`/`StudentDetailFragment` đã có, nhưng provider mặc định là `MockStudentRepository`; chưa phải Firestore realtime. |
| **Quản lý Chứng chỉ con** | Chi tiết sinh viên (P8, P15) | `NGOÀI PHẠM VI THÀNH VIÊN 1` | Thành viên 2 | Thuộc phân rã của Thành viên 2. Subcollection `students/{id}/certificates` đã được định nghĩa trong schema. |
| **Sắp xếp danh sách sinh viên** | Yêu cầu trực tiếp trong `Mid-term.pdf` | `CHƯA CÓ CODE` | Thành viên 2 | `StudentListViewModel` hiện chỉ search/filter, chưa có comparator hay sort state. Mockup S13 có BottomSheet nhưng không thay thế implementation. |
| **Sửa thông tin chứng chỉ** | Yêu cầu `Mid-term.pdf`, design S49–S50 | `ĐÃ CÓ DESIGN, CHƯA XÁC NHẬN CODE` | Thành viên 2 | S49 đặc tả form sửa; S50 đặc tả validation ngày tương lai. Cần đối chiếu implementation riêng trước khi đánh dấu hoàn thành chức năng. |
| **Import/Export sinh viên bằng file** | Yêu cầu trực tiếp trong `Mid-term.pdf` | `CHƯA HOÀN CHỈNH` | Thành viên 2 | `StudentCsvHelper` có parse/generate chuỗi, nhưng UI import đang dùng chuỗi sample hardcode và export chỉ hiện Toast; chưa chọn/đọc/ghi file thật. |
| **Import/Export chứng chỉ bằng file** | Yêu cầu `Mid-term.pdf`, design S55–S70 | `ĐÃ CÓ DESIGN, CHƯA XÁC NHẬN CODE` | Thành viên 2 | Đã đủ default, progress, result, sai định dạng, quá 5 MB, MSSV không tồn tại, lỗi máy chủ và export Light/Dark. Mockup/spec không thay thế file I/O thật. |
| **Trường Email trong Sinh viên** | Form thêm/sửa SV (P5, P6) | `CHƯA CÓ SCHEMA` | Thành viên 2 | PDF không quy định danh sách trường của sinh viên. Mã SV, Họ tên, Ngày sinh, Lớp và Khoa là schema do nhóm chọn; Email trong mockup là mở rộng tùy chọn, không phải yêu cầu đề. |
| **Điểm chứng chỉ (vd: "6.5 Overall")** | S46/S47 | `CHƯA CÓ SCHEMA` | Thành viên 2 | Tên, ngày cấp và đơn vị cấp là dữ liệu cốt lõi; `result` chỉ là nội dung demo, không bắt buộc mở rộng schema. |
| **Dashboard Manager riêng** | S37/S38 | `ĐÃ CÓ DESIGN, CHƯA XÁC NHẬN CODE` | Thành viên 1 | Manager có Sinh viên, Import/Export và Hồ sơ; không có thẻ Người dùng. |
| **Dark Dashboard Employee** | S39 | `ĐÃ CÓ DESIGN, CHƯA XÁC NHẬN CODE` | Thành viên 1 | Dùng cùng hierarchy/quyền với S09, chỉ thay token Dark. |
| **Loading/Empty/Error/Offline cho User và History** | S71–S77 | `ĐÃ CÓ DESIGN, CHƯA XÁC NHẬN CODE` | Thành viên 1 | Cần map state thật từ repository; offline chỉ đọc và phải ghi thời điểm cache. |
| **Hết phiên / tài khoản bị khóa** | S80/S81 | `ĐÃ CÓ DESIGN, CHƯA XÁC NHẬN CODE` | Thành viên 1 | Terminal auth state phải chặn Back về route bảo vệ và không cho tự mở khóa. |
| **Nút "Hoàn tác" khi xóa SV** | Danh sách SV (P14) | `NGOÀI PHẠM VI THÀNH VIÊN 1` | Thành viên 2 | Mockup hiển thị Snackbar có nút "Hoàn tác" sau khi xóa sinh viên. Áp dụng trên bộ nhớ đệm hoặc khôi phục dữ liệu demo. |
| **Khóa người dùng qua Dialog** | Danh sách người dùng (P21) | `ĐÃ CÓ` | Thành viên 1 | `UserListFragment` hiển thị AlertDialog xác nhận trước khi đổi `status` sang `Locked`. |
| **Mở khóa inline trong card** | Danh sách người dùng (S10/S33) | `ĐÃ CÓ` | Thành viên 1 | Admin có quyền chuyển đổi trạng thái người dùng trực tiếp. |
| **Tải ảnh đại diện kèm % tiến độ**| Hồ sơ cá nhân (P22) | `ĐÃ CÓ` | Thành viên 1 | `AvatarUploadPolicy` và `ProfileViewModel` quản lý đầy đủ trạng thái tiến độ tải ảnh lên Storage / local. |
| **Banner Offline & Bản lưu gần nhất**| Danh sách sinh viên (P26) | `CHƯA HOÀN CHỈNH` | Cả hai | Có `UiState`, nhưng Student provider còn in-memory và chưa có bằng chứng cache Firestore/cached timestamp trên thiết bị. |
| **Màn Cài đặt / công tắc hiệu ứng** | Đề xuất bổ sung | `CHƯA CÓ TRONG PDF, CHƯA CÓ CODE` | Thành viên 1 | Mockup PDF gốc không có màn Cài đặt. Đã tạo spec [Sxx-settings-effects](file:///d:/Study/mobile/MidTerm/docs/design/screens/Sxx-settings-effects.md) đề xuất bổ sung vào Hồ sơ hoặc màn con. Lưu cục bộ qua SharedPreferences. |
| **Spring Physics Animation Native** | Gói B (FX-07) | `CHƯA CÓ DEPENDENCY` | Cả hai | Dự án chưa có thư viện `androidx.dynamicanimation`. Sử dụng phương án thay thế có sẵn `OvershootInterpolator` trong SDK chuẩn để tránh thêm dependency. |
| **Predictive Back** | Gói A (trước đây) | `KHÔNG ÁP DỤNG` | Cả hai | Đã loại bỏ hoàn toàn theo yêu cầu của USER; ứng dụng sử dụng điều hướng Back tiêu chuẩn của Fragment / AndroidX Activity. |
