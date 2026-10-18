# Ma trận phân quyền hiển thị (RBAC Visibility Matrix)

Tài liệu này xác định quyền truy cập màn hình và các nút hành động (Button/FAB/Menu) theo từng vai trò người dùng trong hệ thống: **Admin**, **Manager**, **Employee** dựa trên tài liệu PDF thiết kế bàn giao (Nguồn: PDF trang 4, 8, 9, 10, 12, 17, 37).

> [!NOTE]
> **Giả định Demo theo PDF:**
> - Mockup PDF minh họa trực quan 2 vai trò chính: **Admin** (đầy đủ quyền hệ thống, quản lý người dùng, import/export) và **Employee** (nhân viên tra cứu, chỉ đọc dữ liệu sinh viên).
> - Vai trò **Manager** được liệt kê trong token/mô tả chức năng (PDF trang 37) với vai trò "Quản lý sinh viên", có toàn quyền CRUD sinh viên nhưng không quản trị người dùng.
> - Mapping nhãn UI ↔ Dữ liệu Firestore:
>   - Vai trò: `Admin` ↔ `admin`, `Manager` ↔ `manager`, `Employee` ↔ `employee` (chữ thường).
>   - Trạng thái tài khoản: `Hoạt động` ↔ `Normal`, `Đã khóa` ↔ `Locked`.

---

## 1. Bảng phân quyền màn hình theo Role

| Màn hình / Phân hệ | Route / Tên màn | Admin | Manager | Employee | Ghi chú & Nguồn thiết kế |
|:---|:---|:---:|:---:|:---:|:---|
| **Đăng nhập** | `login` | Cho phép | Cho phép | Cho phép | PDF trang 1–3 |
| **Dashboard - Admin** | `dashboard_admin` | Cho phép | Ẩn (dùng chung hoặc chuyển hướng) | Ẩn | PDF trang 4 (Hiển thị 4 thẻ chức năng) |
| **Dashboard - Employee** | `dashboard_employee` | - | - | Cho phép | PDF trang 9 (Chỉ có banner tra cứu + nút đi tới DS sinh viên) |
| **Sinh viên - Danh sách** | `student_list` | Cho phép | Cho phép | Cho phép | PDF trang 7, 12 (Employee xem ở chế độ chỉ đọc) |
| **Sinh viên - Chi tiết** | `student_detail` | Cho phép | Cho phép | Cho phép | PDF trang 8 (Employee không thấy nút Sửa/Xóa) |
| **Sinh viên - Thêm mới** | `student_add` | Cho phép | Cho phép | Bị chặn | PDF trang 5 |
| **Sinh viên - Sửa đổi** | `student_edit` | Cho phép | Cho phép | Bị chặn | PDF trang 6 |
| **Quản lý người dùng** | `user_list` | Cho phép | Bị chặn | Bị chặn | PDF trang 10 (Chỉ Admin thấy thẻ trên Dashboard) |
| **Lịch sử đăng nhập (Người khác)** | `login_history_user` | Cho phép | Bị chặn | Bị chặn | PDF trang 11 (Truy cập từ màn Quản lý người dùng) |
| **Hồ sơ cá nhân** | `profile` | Cho phép | Cho phép | Cho phép | PDF trang 17 (Xem thông tin bản thân & lịch sử của mình) |
| **Import / Export CSV** | `import_export` | Cho phép | `TODO(design)` | Bị chặn | PDF trang 18 (Employee không thấy thẻ Import/Export) |

---

## 2. Bảng phân quyền các nút hành động (Action Buttons & Controls)

| Màn hình | Nút / Thao tác tương tác | Admin | Manager | Employee | Hành vi giao diện khi không có quyền |
|:---|:---|:---:|:---:|:---:|:---|
| **Dashboard** | Thẻ "Sinh viên" | Hiển thị | Hiển thị | Hiển thị | Mở danh sách sinh viên |
| | Thẻ "Người dùng" | Hiển thị | Ẩn | Ẩn | Thẻ không được render trên giao diện (Gone) |
| | Thẻ "Import/Export" | Hiển thị | `TODO(design)` | Ẩn | Thẻ không được render trên giao diện (Gone) |
| | Thẻ "Hồ sơ" | Hiển thị | Hiển thị | Hiển thị | Mở hồ sơ cá nhân |
| **Danh sách sinh viên** | Ô tìm kiếm & Bộ lọc (Khoa, Lớp) | Hiển thị | Hiển thị | Hiển thị | Cung cấp cho mọi vai trò |
| | Nút "Sắp xếp" (Sort) | Hiển thị | Hiển thị | Hiển thị | Mở BottomSheet sắp xếp |
| | FAB "+ Thêm sinh viên" | Hiển thị | Hiển thị | **Ẩn** | Nút FAB bị ẩn hoàn toàn (Gone, xem PDF trang 12) |
| | Nút "+ Thêm sinh viên" (Empty state) | Hiển thị | Hiển thị | **Ẩn** | Empty state chỉ hiện thông điệp tra cứu cho Employee |
| | Nút "Nhập từ CSV" (Empty state) | Hiển thị | `TODO(design)` | **Ẩn** | Ẩn nút với Employee |
| | Thao tác xóa sinh viên (Swipe / Action) | Cho phép | Cho phép | **Bị chặn** | Không hiển thị action xóa |
| **Chi tiết sinh viên** | Menu hành động (3 chấm) | Hiển thị | Hiển thị | **Ẩn** | Ẩn icon menu trên AppBar (PDF trang 8 vs trang 15) |
| | Mục menu "Sửa" | Hiển thị | Hiển thị | Ẩn | Chuyển tới màn Sửa |
| | Mục menu "Xóa" | Hiển thị | Hiển thị | Ẩn | Mở Dialog xác nhận xóa phân tầng |
| | Nút "+ Thêm chứng chỉ" | Hiển thị | Hiển thị | **Ẩn** | Chỉ xem danh sách chứng chỉ hiện có |
| **Quản lý người dùng** | Nút "Khóa tài khoản" | Cho phép | Ẩn | Ẩn | Ẩn trên tài khoản chính mình (`note_cannot_self_lock`) |
| | Nút "Mở khóa tài khoản" | Cho phép | Ẩn | Ẩn | Chỉ thao tác trên tài khoản đang bị `status_label_locked` |
| | Chạm vào tài khoản xem Lịch sử | Cho phép | Ẩn | Ẩn | Mở màn Lịch sử đăng nhập tương ứng |
| **Hồ sơ cá nhân** | Nút đổi ảnh đại diện (Avatar Edit) | Cho phép | Cho phép | Cho phép | Mọi vai trò được quyền đổi ảnh cá nhân |
| | Xem lịch sử đăng nhập của tôi | Cho phép | Cho phép | Cho phép | Hiển thị danh sách các phiên đăng nhập gần đây |
| | Nút "Đăng xuất" | Cho phép | Cho phép | Cho phép | Thoát phiên làm việc về màn Đăng nhập |
| **Import / Export** | Nút "Chọn tệp CSV" & "Tải mẫu" | Cho phép | `TODO(design)` | Ẩn | Toàn bộ màn hình bị chặn với Employee |
| | Nút "Xuất CSV" | Cho phép | `TODO(design)` | Ẩn | Toàn bộ màn hình bị chặn với Employee |

---

## 3. Quy tắc xử lý giao diện bảo vệ (UI Protection Rules)

1. **Nguyên tắc ẩn giao diện (Hide instead of Disable):**
   - Với các hành động người dùng không có quyền (ví dụ FAB thêm sinh viên của Employee, Thẻ người dùng của Employee): Component phải được đặt trạng thái `View.GONE` hoàn toàn, không hiển thị dưới dạng mờ/vô hiệu hóa nhằm giữ giao diện gọn gàng, tránh gây hiểu lầm.
2. **Khóa bảo vệ đa tầng (Defense in Depth):**
   - Phân quyền trên UI chỉ mang tính định hướng trải nghiệm người dùng.
   - Toàn bộ thao tác ghi/đọc dữ liệu đều bắt buộc phải được kiểm soát bởi quy tắc bảo mật backend (Firestore Security Rules & Storage Security Rules).
3. **Phòng chống tự khóa tài khoản Admin:**
   - Trên màn hình Quản lý người dùng, tài khoản của chính người đăng nhập có gắn nhãn `@string/user_you_tag`.
   - Nút hành động khóa tài khoản được thay bằng nhãn `@string/note_cannot_self_lock` để ngăn ngừa lỗi thao tác tự cô lập hệ thống.
