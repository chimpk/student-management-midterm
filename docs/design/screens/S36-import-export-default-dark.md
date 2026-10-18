# S36 - Import / Export CSV - Chế độ tối (Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 36  
> **Ảnh tham chiếu:** [S36](reference/S36-import-export-default-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S36`
- **Tên màn:** Import / Export CSV - Chế độ tối (Dark)
- **Route:** `import_export`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** PDF trang 36
- **Ảnh tham chiếu:** `reference/S36-import-export-default-dark.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc layout tree tương tự S18 với token Dark:
  - Thẻ Import & Thẻ Export: Nền `@color/bg_card`
  - Chip quy cách: Nền `@color/color_surface_variant`
  - Nút bấm: Nền `@color/color_primary`, chữ `@color/color_on_primary`

---

## 3. Thành phần giao diện (Components & Material Catalog)
- Tương tự S18 với theme tối

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/bg_card`, `@color/color_primary`, `@color/color_on_primary`, `@color/text_primary`, `@color/text_secondary`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- Khớp hoàn toàn với copy màn S18

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Chế độ tối |
|:---|:---|
| **Default Dark** | Các khối nhập xuất dữ liệu hiển thị trên nền tối |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Áp dụng triệt để token Dark mode trong `colors-night.xml`.
