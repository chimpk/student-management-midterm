# S28 - Dashboard - Quản trị viên Chế độ tối (Admin Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 28  
> **Ảnh tham chiếu:** [S28](reference/S28-dashboard-admin-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S28`
- **Tên màn:** Dashboard - Quản trị viên Chế độ tối (Admin Dark)
- **Route:** `dashboard_admin`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** PDF trang 28
- **Ảnh tham chiếu:** `reference/S28-dashboard-admin-dark.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc layout tree tương tự S04 với token Dark:
  - Nền màn hình: `@color/color_background` (``)
  - Nền 4 thẻ chức năng: `@color/bg_card` (``)
  - Nền thẻ thống kê: `@color/color_primary_container` (``)
  - Thanh BottomNav: `@color/color_surface` (``)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- Tương tự S04 với theme tối

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/color_primary_container`, `@color/color_on_primary_container`, `@color/text_primary`, `@color/text_secondary`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- Khớp hoàn toàn với copy màn S04

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Chế độ tối |
|:---|:---|
| **Default Dark** | Các thẻ chức năng hiển thị rõ nét trên nền tối |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Áp dụng đúng token Dark mode từ `colors-night.xml`.
- [ ] Các icon hiển thị rõ ràng với tint `@color/color_primary`.
