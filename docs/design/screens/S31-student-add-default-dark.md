# S31 - Sinh viên - Thêm mới Chế độ tối (Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 31  
> **Ảnh tham chiếu:** [S31](reference/S31-student-add-default-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S31`
- **Tên màn:** Sinh viên - Thêm mới Chế độ tối (Dark)
- **Route:** `student_add`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** PDF trang 31
- **Ảnh tham chiếu:** `reference/S31-student-add-default-dark.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc layout tree tương tự S05 với token Dark:
  - Nền: `@color/color_background`
  - Thẻ form: `@color/bg_card`
  - Ô input OutlinedBox: Viền `@color/color_outline_variant`, text `@color/text_primary`
  - Nút Lưu: Nền `@color/color_primary`, chữ `@color/color_on_primary`

---

## 3. Thành phần giao diện (Components & Material Catalog)
- Tương tự S05 với theme tối

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/color_on_primary`, `@color/text_primary`, `@color/color_outline_variant`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- Khớp hoàn toàn với copy màn S05

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Chế độ tối |
|:---|:---|
| **Default Dark** | Giao diện form tối ưu không gây chói mắt |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Dropdown chọn khoa, lớp hiển thị popup nền tối đồng bộ.
