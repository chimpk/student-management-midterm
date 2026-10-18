# S34 - Lịch sử đăng nhập - Chế độ tối (Admin Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 34  
> **Ảnh tham chiếu:** [S34](reference/S34-login-history-user-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S34`
- **Tên màn:** Lịch sử đăng nhập - Chế độ tối (Admin Dark)
- **Route:** `login_history_user`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** PDF trang 34
- **Ảnh tham chiếu:** `reference/S34-login-history-user-dark.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc layout tree tương tự S11 với token Dark:
  - Thẻ phiên đăng nhập: Nền `@color/bg_card`
  - Tiêu đề nhóm ngày: Chữ `@color/color_primary`

---

## 3. Thành phần giao diện (Components & Material Catalog)
- Tương tự S11 với theme tối

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/bg_card`, `@color/color_primary`, `@color/text_primary`, `@color/text_secondary`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- Khớp hoàn toàn với copy màn S11

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Chế độ tối |
|:---|:---|
| **Default Dark** | Các phiên đăng nhập hiển thị trên nền tối |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Huy hiệu phiên hiện tại nổi bật trên nền thẻ tối.
