# Phân Tích Khoảng Cách Giữa Thiết Kế (Mockup) Và Hệ Thống Thật (Gap Analysis)

Tài liệu này phân tích và phân loại toàn diện các chi tiết xuất hiện trong tài liệu thiết kế PDF (`docs/Untitled.pdf`) so sánh với mã nguồn, schema Firestore và phạm vi trách nhiệm hiện tại của dự án.

> [!NOTE]
> Các chi tiết trong mockup không bắt buộc phải được hỗ trợ hoàn toàn bởi backend nếu đề bài không yêu cầu hoặc đang nằm ngoài ranh giới phân rã của từng thành viên. Các mục chưa có backend sẽ được xử lý an toàn bằng fallback giao diện hoặc mock data mà không làm thay đổi `firestore.rules`.

---

## Bảng Phân Tích Chi Tiết

| Hạng mục trong Mockup | Xuất hiện tại | Trạng thái hiện tại | Chủ sở hữu | Ghi chú kỹ thuật & Hướng xử lý |
|---|---|:---:|:---:|---|
| **Đăng nhập bằng Username** | Màn hình Login (P1-3) | `ĐÃ CÓ` | Thành viên 1 | Đã giải quyết qua `DefaultUsernameAuthResolver` ánh xạ an toàn sang Firebase Auth. |
| **Quên mật khẩu?** | Màn hình Login (P1-3) | `CHƯA CÓ BACKEND` | Thành viên 1 | Mockup có nút "Quên mật khẩu?", nhưng đề bài không yêu cầu luồng reset password. Phản hồi bằng Dialog/Snackbar thông báo liên hệ Quản trị viên, không gọi API giả. |
| **Trạng thái "Đã đồng bộ"** | Dashboard, Danh sách SV | `ĐÃ CÓ` | Cả hai | Firestore `addSnapshotListener` cung cấp cơ chế realtime listener thật. |
| **Trường "Đơn vị" trong User** (vd: "Phòng Đào tạo") | Hồ sơ cá nhân (P17, P35) | `CHƯA CÓ SCHEMA` | Thành viên 1 | Schema Firestore `users` chuẩn đề bài chỉ có: `name`, `age`, `phone`, `status`, `role`. Trường `unit` chỉ là mockup UI trình bày, không bắt buộc thêm vào schema DB để tránh vi phạm Security Rules. |
| **Vị trí đăng nhập "Hà Nội"** | Lịch sử đăng nhập (P11, P17) | `CHƯA CÓ BACKEND` | Thành viên 1 | Đề bài chỉ yêu cầu ghi nhận thiết bị (`device`) và thời gian (`timestamp`). Vị trí địa lý đòi hỏi quyền Location nguy hiểm (`ACCESS_FINE_LOCATION`) ngoài phạm vi đề tài, hiển thị placeholder nếu cần. |
| **Hệ điều hành "Android 14"** | Lịch sử đăng nhập (P11, P17) | `ĐÃ CÓ` | Thành viên 1 | Ứng dụng đọc trực tiếp từ `Build.VERSION.RELEASE` và ghép vào trường `device` an toàn. |
| **Biểu tượng Chuông thông báo** | Header Dashboard | `CHƯA CÓ BACKEND` | Thành viên 1 | Thông báo push nằm ngoài phạm vi đề tài theo `SRS.md`. Nút chuông chỉ đóng vai trò mockup UI, click hiển thị "Chưa có thông báo mới". |
| **Thẻ Import/Export trên Home** | Dashboard (P4, P28) | `NGOÀI PHẠM VI THÀNH VIÊN 1` | Thành viên 2 | Chức năng nhập/xuất CSV thuộc phân rã của Thành viên 2. Đã scaffold `StudentCsvHelper` và nút mở. |
| **Danh sách & Chi tiết Sinh viên** | Màn hình Sinh viên (P7-8) | `NGOÀI PHẠM VI THÀNH VIÊN 1` | Thành viên 2 | Thuộc phân rã của Thành viên 2. Đã scaffold đầy đủ `StudentListFragment`, `StudentDetailFragment` và repository mock. |
| **Quản lý Chứng chỉ con** | Chi tiết sinh viên (P8, P15) | `NGOÀI PHẠM VI THÀNH VIÊN 1` | Thành viên 2 | Thuộc phân rã của Thành viên 2. Subcollection `students/{id}/certificates` đã được định nghĩa trong schema. |
| **Trường Email trong Sinh viên** | Form thêm/sửa SV (P5, P6) | `CHƯA CÓ SCHEMA` | Thành viên 2 | Đề bài gốc chỉ yêu cầu: Mã SV, Họ tên, Ngày sinh, Lớp, Khoa. Email sinh viên trong mockup là thuộc tính mở rộng cho giao diện, không bắt buộc ghi vào Firestore. |
| **Điểm chứng chỉ (vd: "6.5 Overall")** | Danh sách chứng chỉ (P8, P30)| `CHƯA CÓ SCHEMA` | Thành viên 2 | Đề bài chỉ yêu cầu: Tên chứng chỉ, Ngày cấp, Đơn vị cấp. Trường `result` là mở rộng demo, không làm đổi schema bắt buộc. |
| **Nút "Hoàn tác" khi xóa SV** | Danh sách SV (P14) | `NGOÀI PHẠM VI THÀNH VIÊN 1` | Thành viên 2 | Mockup hiển thị Snackbar có nút "Hoàn tác" sau khi xóa sinh viên. Áp dụng trên bộ nhớ đệm hoặc khôi phục dữ liệu demo. |
| **Khóa người dùng qua Dialog** | Danh sách người dùng (P21) | `ĐÃ CÓ` | Thành viên 1 | Đã triển khai đầy đủ `dialog_user_edit` và đổi trạng thái `status: "Locked"` tức thì. |
| **Mở khóa inline trong card** | Danh sách người dùng (P37) | `ĐÃ CÓ` | Thành viên 1 | Admin có quyền chuyển đổi trạng thái người dùng trực tiếp. |
| **Tải ảnh đại diện kèm % tiến độ**| Hồ sơ cá nhân (P22) | `ĐÃ CÓ` | Thành viên 1 | `AvatarUploadPolicy` và `ProfileViewModel` quản lý đầy đủ trạng thái tiến độ tải ảnh lên Storage / local. |
| **Banner Offline & Bản lưu gần nhất**| Danh sách sinh viên (P26) | `ĐÃ CÓ` | Cả hai | `UiState` và Firestore offline cache đã được tích hợp sẵn, hiển thị trạng thái ngoại tuyến chuẩn. |
| **Màn Cài đặt / công tắc hiệu ứng** | Đề xuất bổ sung | `CHƯA CÓ TRONG PDF, CHƯA CÓ CODE` | Thành viên 1 | Mockup PDF gốc không có màn Cài đặt. Đã tạo spec [Sxx-settings-effects](file:///d:/Study/mobile/MidTerm/docs/design/screens/Sxx-settings-effects.md) đề xuất bổ sung vào Hồ sơ hoặc màn con. Lưu cục bộ qua SharedPreferences. |
| **Spring Physics Animation Native** | Gói B (FX-07) | `CHƯA CÓ DEPENDENCY` | Cả hai | Dự án chưa có thư viện `androidx.dynamicanimation`. Sử dụng phương án thay thế có sẵn `OvershootInterpolator` trong SDK chuẩn để tránh thêm dependency. |
| **Predictive Back** | Gói A (trước đây) | `KHÔNG ÁP DỤNG` | Cả hai | Đã loại bỏ hoàn toàn theo yêu cầu của USER; ứng dụng sử dụng điều hướng Back tiêu chuẩn của Fragment / AndroidX Activity. |
