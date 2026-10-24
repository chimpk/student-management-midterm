# S44 - Người dùng - Bảo vệ tài khoản hiện tại (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 28  
> **Ảnh tham chiếu:** [S44](../reference/S44-user-edit-self-protection-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S44`
- **Tên màn:** Người dùng - Bảo vệ tài khoản hiện tại (Light)
- **Route:** `user_edit`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 28
- **Ảnh tham chiếu:** `reference/S44-user-edit-self-protection-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Form sửa tài khoản Admin hiện tại có banner `@string/current_account_label`.
  - Username chỉ đọc; vai trò cố định Admin.
  - Tùy chọn trạng thái Đã khóa bị vô hiệu hóa.
  - Card cảnh báo nêu rõ không thể tự khóa, tự xóa hoặc tự hạ quyền.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialBanner`, `TextInputLayout`, dropdown disabled, `MaterialCardView`, `MaterialButton`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/current_account_label, @string/self_role_protection, @string/self_lock_protection, @string/admin_account_protection

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Protected** | Theo đúng ảnh S44 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Cả UI và backend phải chặn tự khóa, tự xóa và tự hạ quyền.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S44-user-edit-self-protection-light.png`.
