# S41 - Người dùng - Sửa đổi (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 21  
> **Ảnh tham chiếu:** [S41](../reference/S41-user-edit-default-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S41`
- **Tên màn:** Người dùng - Sửa đổi (Light)
- **Route:** `user_edit`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 21
- **Ảnh tham chiếu:** `reference/S41-user-edit-default-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc giống S40, dữ liệu hiện tại được điền sẵn.
  - Username là định danh chỉ đọc.
  - Mật khẩu mới để trống nghĩa là giữ mật khẩu hiện tại.
  - Vai trò, trạng thái và email tùy chọn dùng Material dropdown/input.
  - Nút Lưu ghim đáy.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `TextInputLayout`, `TextInputEditText`, dropdown Material, `MaterialButton`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/title_edit_user, @string/password_new_label, @string/password_keep_hint, @string/label_email_optional, @string/btn_save

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Default** | Theo đúng ảnh S41 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Không thay username; email vẫn tùy chọn và không giới hạn tên miền.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S41-user-edit-default-light.png`.
