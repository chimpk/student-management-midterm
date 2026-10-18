# S32 - Sinh viên - Sửa đổi Chế độ tối (Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 32  
> **Ảnh tham chiếu:** [S32](reference/S32-student-edit-default-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S32`
- **Tên màn:** Sinh viên - Sửa đổi Chế độ tối (Dark)
- **Route:** `student_edit`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** PDF trang 32
- **Ảnh tham chiếu:** `reference/S32-student-edit-default-dark.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc layout tree tương tự S06 với token Dark:
  - Ô MSSV bị khóa: Nền xám tối, chữ `@color/text_disabled` (``)
  - Các ô khác: Nền `@color/bg_card`, chữ `@color/text_primary`

---

## 3. Thành phần giao diện (Components & Material Catalog)
- Tương tự S06 với theme tối

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/bg_card`, `@color/color_primary`, `@color/text_primary`, `@color/text_disabled`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- Khớp hoàn toàn với copy màn S06

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Chế độ tối |
|:---|:---|
| **Default Dark** | Ô MSSV hiển thị rõ trạng thái vô hiệu hóa trên nền tối |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Phân biệt rõ ràng giữa ô MSSV bị khóa và các ô cho phép chỉnh sửa.
