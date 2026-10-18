# S27 - Đăng nhập - Chế độ tối (Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 27  
> **Ảnh tham chiếu:** [S27](reference/S27-login-default-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S27`
- **Tên màn:** Đăng nhập - Chế độ tối (Dark)
- **Route:** `login`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** PDF trang 27
- **Ảnh tham chiếu:** `reference/S27-login-default-dark.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc layout tree tương tự S01 nhưng toàn bộ container áp dụng token Dark:
  - Nền toàn màn hình: `@color/color_background` (``)
  - Nền form đăng nhập: `@color/bg_card` (``)
  - Chữ tiêu đề & nhập liệu: `@color/text_primary` (``)
  - Chữ phụ đề: `@color/text_secondary` (``)
  - Nút đăng nhập: Nền `@color/color_primary` (``), Chữ `@color/color_on_primary` (``)
  - Viền thẻ & input: `@color/color_outline_variant` (``)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- Tương tự S01, đổi toàn bộ style màu sang Dark theme

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/bg_card`, `@color/color_primary`, `@color/color_on_primary`, `@color/text_primary`, `@color/text_secondary`, `@color/color_outline_variant` (theo `colors-night.xml`)
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/input_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- Khớp hoàn toàn với copy màn S01

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Chế độ tối |
|:---|:---|
| **Default Dark** | Nền tối, chữ sáng tương phản cao, nút nhấn màu sáng nổi bật |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Áp dụng triệt để token Dark mode trong `colors-night.xml`, không có bất kỳ màu hex nào bị hard-code.
- [ ] Tương phản chữ `@color/color_on_primary` trên nền nút `@color/color_primary` đạt trên 4.5:1.
