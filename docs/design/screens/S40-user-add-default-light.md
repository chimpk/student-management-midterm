# S40 - Người dùng - Thêm mới (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 20  
> **Ảnh tham chiếu:** [S40](../reference/S40-user-add-default-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S40`
- **Tên màn:** Người dùng - Thêm mới (Light)
- **Route:** `user_add`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 20
- **Ảnh tham chiếu:** `reference/S40-user-add-default-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` với `MaterialToolbar` tiêu đề thêm người dùng.
  - `NestedScrollView` chứa card thông tin người dùng.
    - Trường bắt buộc: họ tên, tuổi, số điện thoại, username, mật khẩu, vai trò, trạng thái.
    - Email tùy chọn; chấp nhận mọi tên miền hợp lệ.
  - Footer ghim đáy chứa nút `@string/btn_save`; nội dung có inset để không bị che.

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
- @string/title_add_user, @string/user_required_fields_note, @string/label_full_name, @string/label_age, @string/label_phone, @string/username_label, @string/password_label, @string/label_email_optional, @string/btn_save

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Default** | Theo đúng ảnh S40 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Validate khi rời ô; Admin tạo tài khoản, không có luồng người dùng tự đăng ký.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S40-user-add-default-light.png`.
