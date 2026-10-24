# S42 - Người dùng - Lỗi nhập liệu (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 22  
> **Ảnh tham chiếu:** [S42](../reference/S42-user-add-validation-errors-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S42`
- **Tên màn:** Người dùng - Lỗi nhập liệu (Light)
- **Route:** `user_add`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 22
- **Ảnh tham chiếu:** `reference/S42-user-add-validation-errors-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc form S40.
  - Tuổi, số điện thoại, username và email lỗi có viền `@color/color_error` cùng helper text ngay dưới trường.
  - Giá trị hợp lệ đã nhập được giữ nguyên.
  - Nút Lưu ghim đáy ở trạng thái disabled.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `TextInputLayout` error state, `TextInputEditText`, `MaterialButton` disabled
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/error_age_range, @string/error_phone_required, @string/error_username_spaces, @string/error_email_invalid_generic, @string/form_error_review, @string/btn_save

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Validation error** | Theo đúng ảnh S42 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Lỗi xuất hiện sau blur; không gửi form khi còn bất kỳ lỗi nào.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S42-user-add-validation-errors-light.png`.
