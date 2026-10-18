# S33 - Quản lý người dùng - Danh sách Chế độ tối (Admin Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 33  
> **Ảnh tham chiếu:** [S33](reference/S33-user-list-admin-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S33`
- **Tên màn:** Quản lý người dùng - Danh sách Chế độ tối (Admin Dark)
- **Route:** `user_list`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** PDF trang 33
- **Ảnh tham chiếu:** `reference/S33-user-list-admin-dark.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc layout tree tương tự S10 với token Dark:
  - Thẻ người dùng: Nền `@color/bg_card`
  - Nhãn Hoạt động: `@color/color_primary_container`
  - Nhãn Đã khóa: `@color/color_error_container`, viền `@color/color_error`

---

## 3. Thành phần giao diện (Components & Material Catalog)
- Tương tự S10 với theme tối

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/bg_card`, `@color/color_primary`, `@color/color_error`, `@color/text_primary`, `@color/text_secondary`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- Khớp hoàn toàn với copy màn S10

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Chế độ tối |
|:---|:---|
| **Default Dark** | Danh sách tài khoản hiển thị rõ ràng trên nền tối |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Nút Khóa tài khoản và Mở khóa tài khoản tương phản tốt trên nền tối.
