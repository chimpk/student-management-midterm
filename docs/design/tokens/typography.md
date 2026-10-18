# Đặc Tả Kiểu Chữ (Typography Tokens)

Nguồn: PDF thiết kế trang 37.

## 1. Phông chữ hệ thống
- **Font Family:** `Roboto` (phông chữ mặc định của Android).
- **Hỗ trợ trợ năng:** Tương thích hoàn toàn với `fontScale` hệ thống, nội dung tự động tăng chiều cao container hoặc cuộn khi người dùng phóng to cỡ chữ, không bị cắt cụt.

## 2. Thang kích thước và độ đậm (Hierarchy)

| Cấp bậc (Role) | Kích thước (sp) | Độ đậm (Font Weight) | Token tương ứng | Áp dụng trên giao diện |
|---|---|---|---|---|
| **Tiêu đề màn hình (Screen Title)** | `22sp` | 500 (Medium) | `@dimen/text_size_title` | Tiêu đề chính đầu màn hình, tên ứng dụng trên Header |
| **Tiêu đề mục / Tên (Item / Section Name)** | `16sp` | 500 (Medium) | `@dimen/text_size_name` | Tên sinh viên, tên tài khoản người dùng, tên thẻ chức năng |
| **Phụ đề / Nội dung phụ (Subtitle / Body)** | `14sp` | 400 (Regular) | `@dimen/text_size_subtitle` | Nội dung mô tả, MSSV kèm lớp, nhãn thông tin chi tiết, input form |
| **Nhãn phụ / Chip / Badge (Caption / Chip)** | `12sp` | 500 (Medium) | `@dimen/text_size_chip` | Chip vai trò (Admin/Manager/Employee), Chip trạng thái (Hoạt động/Đã khóa), số lượng sinh viên |
| **Ghi chú nhỏ (Micro Caption)** | `11sp` | 400 (Regular) | `@dimen/text_size_caption` | Thời gian đăng nhập, nhãn footnote bảo mật |

## 3. Độ tương phản màu chữ (Contrast Accessibility)
- Văn bản thông thường (body text, subtitle) đạt độ tương phản tối thiểu **4.5:1** trên nền tương ứng.
- Văn bản kích thước lớn hoặc đậm (title) đạt độ tương phản tối thiểu **3.0:1**.
- Chế độ Dark Mode: Nút bấm nền `@color/color_primary` (`#B6BFFF`) sử dụng màu chữ tối (`#18204F`) để đảm bảo độ tương phản cao, không dùng chữ trắng trên nền sáng.
