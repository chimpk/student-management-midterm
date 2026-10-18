# S15 - Sinh viên - Menu tùy chọn chi tiết (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 15  
> **Ảnh tham chiếu:** [S15](reference/S15-student-detail-action-menu-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S15`
- **Tên màn:** Sinh viên - Menu tùy chọn chi tiết (Light)
- **Route:** `student_detail`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 15
- **Ảnh tham chiếu:** `reference/S15-student-detail-action-menu-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giao diện chi tiết sinh viên S08
- `PopupMenu` hoặc `ListPopupWindow` (nổi góc trên phải AppBar, nền `@color/color_surface`, bo góc `@dimen/input_corner_radius`, elevation `~8dp`)
  - Item 1: `ic_edit` + `TextView` (copy `@string/menu_action_edit`, chữ `@color/text_primary`)
  - Item 2: `ic_delete` + `TextView` (copy `@string/menu_action_delete`, chữ `@color/color_error`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `PopupMenu`: Trình đơn nổi tùy chọn thao tác
- Icons: `ic_edit` (Material Symbols `edit`), `ic_delete` (Material Symbols `delete`)

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_surface`, `@color/text_primary`, `@color/color_error`, `@color/color_outline_variant`
- Kích thước: `@dimen/input_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/menu_action_edit`
- `@string/menu_action_delete`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Menu hiển thị |
|:---|:---|
| **Open** | Nổi trên thanh AppBar sau khi nhấn icon 3 chấm |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Mục Xóa hiển thị màu sắc cảnh báo đỏ `@color/color_error`.
- [ ] Nhấn Sửa mở màn hình S06; nhấn Xóa mở Dialog xác nhận S16.
