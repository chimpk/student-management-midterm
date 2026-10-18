# S29 - Sinh viên - Danh sách Chế độ tối (Admin Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 29  
> **Ảnh tham chiếu:** [S29](reference/S29-student-list-admin-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S29`
- **Tên màn:** Sinh viên - Danh sách Chế độ tối (Admin Dark)
- **Route:** `student_list`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** PDF trang 29
- **Ảnh tham chiếu:** `reference/S29-student-list-admin-dark.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc layout tree tương tự S07 với token Dark:
  - Nền: `@color/color_background` (``)
  - Thẻ sinh viên: `@color/bg_card` (``)
  - FAB: Nền `@color/color_primary` (``), tint `@color/color_on_primary` (``)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- Tương tự S07 với theme tối

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/color_on_primary`, `@color/text_primary`, `@color/text_secondary`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- Khớp hoàn toàn với copy màn S07

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Chế độ tối |
|:---|:---|
| **Default Dark** | Danh sách sinh viên hiển thị trên nền tối |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Dải Filter Chips và ô tìm kiếm hiển thị tương phản chuẩn xác trên nền tối.
