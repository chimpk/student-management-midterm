# S30 - Sinh viên - Chi tiết Chế độ tối (Admin Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 30  
> **Ảnh tham chiếu:** [S30](reference/S30-student-detail-admin-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S30`
- **Tên màn:** Sinh viên - Chi tiết Chế độ tối (Admin Dark)
- **Route:** `student_detail`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** PDF trang 30
- **Ảnh tham chiếu:** `reference/S30-student-detail-admin-dark.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc layout tree tương tự S08 với token Dark:
  - Nền màn hình: `@color/color_background`
  - Thẻ thông tin & thẻ chứng chỉ: `@color/bg_card`
  - Chip trạng thái học tập: `@color/color_secondary_container`

---

## 3. Thành phần giao diện (Components & Material Catalog)
- Tương tự S08 với theme tối

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/text_primary`, `@color/text_secondary`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- Khớp hoàn toàn với copy màn S08

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Chế độ tối |
|:---|:---|
| **Default Dark** | Các khối chi tiết sinh viên và chứng chỉ hiển thị nền tối |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Tương phản chữ trên các hàng thông tin đạt chuẩn WCAG AA.
