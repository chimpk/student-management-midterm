# Ma trận trạng thái giao diện (State Matrix)

Tài liệu này tổng hợp toàn bộ các trạng thái giao diện (UI states) được xác định trực tiếp từ tài liệu PDF thiết kế bàn giao (Nguồn: PDF trang 1–37).

---

## 1. Phân loại trạng thái hệ thống

Các trạng thái được biểu diễn trong thiết kế gồm:
- **Default (Idle/Normal):** Trạng thái hoạt động bình thường, dữ liệu sẵn sàng và hiển thị đầy đủ.
- **Loading / Shimmer:** Đang tải dữ liệu từ máy chủ hoặc xử lý tiến trình ngầm (như đăng nhập, tải danh sách).
- **Empty:** Màn hình không có dữ liệu để hiển thị (ví dụ danh sách chưa có bản ghi nào).
- **Error (Inline / Form / Banner):** Lỗi xác thực tài khoản, lỗi nhập liệu form hoặc lỗi xử lý dòng dữ liệu.
- **Offline:** Mất kết nối mạng Internet, hiển thị thanh cảnh báo ngoại tuyến kèm thời gian lưu gần nhất (`bg_offline`).
- **Interactive Overlay / Dialog / Sheet:** Hộp thoại xác nhận (khóa tài khoản, xóa sinh viên), bộ chọn ngày (DatePicker), bảng chọn sắp xếp (BottomSheet).
- **Progress / Uploading:** Thanh tiến độ hiển thị phần trăm tác vụ đang xử lý (ví dụ tải ảnh đại diện).
- **Feedback Notification (Snackbar):** Thông báo kết quả thao tác kèm nút tương tác (ví dụ hoàn tác xóa).
- **Dark Mode:** Toàn bộ biến thể giao diện tối áp dụng bảng token màu nền tối tương ứng.

---

## 2. Bảng ma trận trạng thái theo từng màn hình

| ID | Nhóm màn hình / Tính năng | Default (Light) | Loading / Progress | Empty | Error / Validation | Offline | Dialog / BottomSheet / Feedback | Dark Mode | Ghi chú & Nguồn PDF |
|:---|:---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---|
| **S01** | Đăng nhập (Auth) | S01 | S02 (`btn_logging_in`) | - | S03 (Sai tài khoản) | - | - | S27 | PDF trang 1, 2, 3, 27 |
| **S02** | Dashboard - Admin | S04 | - | - | - | S04 (Hỗ trợ indicator) | - | S28 | PDF trang 4, 28 |
| **S03** | Dashboard - Employee | S09 | - | - | - | - | - | - | PDF trang 9 (`TODO(design)` biến thể Dark) |
| **S04** | Sinh viên - Danh sách (Admin) | S07 | S24 (Skeleton/Shimmer) | S25 | - | S26 (Banner ngoại tuyến) | S13 (BottomSheet), S14 (Snackbar) | S29 | PDF trang 7, 13, 14, 24, 25, 26, 29 |
| **S05** | Sinh viên - Danh sách (Employee) | S12 | - | - | - | - | - | - | PDF trang 12 (Ẩn FAB, đọc dữ liệu) |
| **S06** | Sinh viên - Chi tiết (Admin) | S08 | - | - | - | - | S15 (Action menu), S16 (Dialog xóa) | S30 | PDF trang 8, 15, 16, 30 |
| **S07** | Sinh viên - Thêm mới | S05 | - | - | S20 (Lỗi form trùng/định dạng) | - | S19 (DatePicker) | S31 | PDF trang 5, 19, 20, 31 |
| **S08** | Sinh viên - Cập nhật | S06 | - | - | S20 (Lỗi form tương tự) | - | S19 (DatePicker) | S32 | PDF trang 6, 32 |
| **S09** | Quản lý người dùng | S10 | - | - | - | - | S21 (Dialog khóa tài khoản) | S33 | PDF trang 10, 21, 33 |
| **S10** | Lịch sử đăng nhập | S11 | - | - | - | - | - | S34 | PDF trang 11, 34 |
| **S11** | Hồ sơ cá nhân | S17 | S22 (Upload avatar 65%) | - | - | - | - | S35 | PDF trang 17, 22, 35 |
| **S12** | Import / Export CSV | S18 | - | - | S23 (Báo cáo lỗi dòng CSV) | - | S23 (Card tổng hợp kết quả) | S36 | PDF trang 18, 23, 36 |
| **Sxx** | Cài đặt hiệu ứng | Sxx (Bật) | - | - | - | - | Sxx (Vô hiệu do hệ thống) | Sxx (Dark) | Đề xuất bổ sung (quản trị Gói B) |

---

## 3. Quy tắc hiển thị chi tiết theo từng trạng thái

### 3.1. Trạng thái Loading / Shimmer (S02, S22, S24)
- **Login Loading (S02):** Nút đăng nhập đổi văn bản thành `@string/btn_logging_in`, vô hiệu hóa tương tác (disabled), xuất hiện vòng xoay CircularProgressIndicator nhỏ gọn hoặc thanh mờ.
- **Student List Shimmer (S24):** 3 khối card xám bo góc 16dp tạo hiệu ứng khung xương, kèm thanh hiển thị nhãn `@string/student_loading_text`. Vô hiệu hóa thanh cuộn và nút chức năng trong lúc tải.
- **Avatar Upload Progress (S22):** Vùng tải ảnh hiển thị thanh LinearProgressIndicator với giá trị tiến độ phần trăm (`65%`), nhãn trạng thái `@string/uploading_avatar_format`.

### 3.2. Trạng thái Offline (S26)
- **Banner ngoại tuyến:** Xuất hiện dải ruy băng đầu danh sách với nền `@color/bg_offline`, viền `@color/border_offline`, chữ `@color/text_offline`.
- **Thông điệp:** `@string/student_offline_banner` kèm nhãn `@string/student_cached_time_format`.
- **Dữ liệu hiển thị:** Danh sách sinh viên vẫn đọc được từ bộ nhớ đệm cục bộ (Cache/Room/Firestore Offline Cache), chip đếm hiển thị nhãn `@string/student_count_sort_offline_format`.

### 3.3. Trạng thái Empty (S25)
- **Vị trí:** Giữa màn hình danh sách khi không có kết quả lọc hoặc dữ liệu hệ thống trống.
- **Thành phần:** Icon minh họa trạng thái rỗng `ic_folder_empty` (vòng tròn 56dp nền phụ), tiêu đề `@string/student_empty_title` (18sp/500), mô tả `@string/student_empty_desc` (14sp).
- **Hành động phục hồi (CTA):** 2 nút hành động trực tiếp: `@string/btn_add_student_inline` (Filled Button) và `@string/btn_import_csv_inline` (Outlined Button).

### 3.4. Trạng thái Form Validation Error (S20)
- **Input lỗi:** `TextInputLayout` chuyển viền và nhãn sang màu `@color/color_error`.
- **Thông báo lỗi bên dưới ô:**
  - MSSV: `@string/error_mssv_exists_format`.
  - Email: `@string/error_email_invalid_example`.
- **Giữ dữ liệu người dùng:** Tuyệt đối không xóa trắng các trường thông tin đã điền hợp lệ khi phát sinh lỗi xác thực.

### 3.5. Trạng thái Dialog xác nhận & Cascade Warning (S16, S21)
- **Khóa tài khoản (S21):** Dialog nền `@color/color_surface`, cảnh báo người dùng `@string/dialog_lock_user_desc_format`. Nút hủy `@string/btn_cancel` và nút xác nhận `@color/color_error`.
- **Xóa sinh viên dạng phân tầng (S16):** Tiêu đề `@string/dialog_delete_student_title`, nội dung cảnh báo xóa kèm theo toàn bộ chứng chỉ `@string/dialog_delete_student_warning_format`. Nút xác nhận xóa mang sắc đỏ cảnh báo `@string/btn_confirm_delete`.

### 3.6. Trạng thái Feedback Snackbar (S14)
- **Vị trí:** Nổi phía dưới màn hình (trên Navigation Bar hoặc FAB).
- **Nội dung:** Thông báo bản ghi vừa bị xóa `@string/snackbar_deleted_student_format`.
- **Hành động hoàn tác:** Nút `@string/btn_undo` có màu nổi bật để người dùng khôi phục bản ghi trước khi hết thời gian chờ (timeout ~4000ms).

### 3.7. Trạng thái công tắc Hiệu ứng nâng cao (Sxx)
- **Bật (Enabled - ON):** Công tắc ở vị trí Checked (nền `@color/color_primary`), toàn bộ hiệu ứng Gói B (FX-05 → FX-11) được kích hoạt chạy song song với Gói A.
- **Tắt (Enabled - OFF):** Công tắc ở vị trí Unchecked (nền `@color/color_surface_variant`), toàn bộ hiệu ứng Gói B chuyển sang phương án tĩnh tức thì.
- **Vô hiệu hóa do hệ thống (Disabled by System):** Khi hệ thống bật chế độ "Giảm hiệu ứng động" (`ANIMATOR_DURATION_SCALE == 0`), công tắc tự động mờ đi (disabled, màu `@color/text_disabled`), không cho phép gạt và hiển thị dòng chú thích `@string/settings_effects_disabled_system_note`. Toàn bộ Gói A và Gói B lùi về phương án tĩnh.
