# S01 - Đăng nhập - Mặc định (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 1  
> **Ảnh tham chiếu:** [S01](reference/S01-login-default-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S01`
- **Tên màn:** Đăng nhập - Mặc định (Light)
- **Route:** `login`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 1
- **Ảnh tham chiếu:** `reference/S01-login-default-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `ScrollView` (toàn màn hình, nền `@color/color_background`)
  - `LinearLayout` (vertical, padding `@dimen/screen_margin`, căn giữa)
    - Slot Logo thương hiệu (`~64dp x 64dp`, bo góc `@dimen/card_corner_radius`, nền `@color/color_primary`, biểu tượng `@drawable/ic_app_logo`)
    - `TextView` App Name (kiểu chữ 22sp/500 `@color/color_primary`, copy `@string/app_name`)
    - `TextView` Tagline (kiểu chữ 14sp `@color/text_secondary`, copy `@string/app_tagline`)
    - `Space` (`~32dp`)
    - `MaterialCardView` (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, viền `@dimen/card_stroke_width` `@color/color_outline_variant`, elevation `@dimen/card_elevation`)
      - `LinearLayout` (vertical, padding `@dimen/screen_margin`)
        - `TextView` Tiêu đề form (18sp/500 `@color/text_primary`, copy `@string/login_title`)
        - `TextView` Hướng dẫn đăng nhập (14sp `@color/text_secondary`, copy `@string/login_instruction`)
        - `Space` (`~16dp`)
        - `TextInputLayout` OutlinedBox (hint `@string/username_hint`, bo góc `@dimen/input_corner_radius`, icon đầu `ic_person`)
          - `TextInputEditText` (copy `@string/username_label`, text `@color/text_primary`)
        - `Space` (`~12dp`)
        - `TextInputLayout` OutlinedBox (hint `@string/password_hint`, bo góc `@dimen/input_corner_radius`, icon đầu `ic_lock`, icon cuối password_toggle)
          - `TextInputEditText` (copy `@string/password_label`, text `@color/text_primary`, inputType `textPassword`)
        - `Space` (`~12dp`)
        - `TextView` Quên mật khẩu (14sp/500 `@color/color_primary`, copy `@string/forgot_password`, touch target `~48dp`, căn phải)
        - `Space` (`~16dp`)
        - `MaterialButton` Filled (chiều cao `@dimen/touch_target_min`, bo góc `@dimen/input_corner_radius`, nền `@color/color_primary`, chữ `@color/color_on_primary`, copy `@string/btn_login`)
    - `Space` (`~24dp`)
    - `TextView` Thông báo tài khoản (12sp `@color/text_secondary`, copy `@string/no_account_hint`, căn giữa)
    - `LinearLayout` (horizontal, căn giữa)
      - `ImageView` (`~16dp`, icon `ic_shield`, tint `@color/text_secondary`)
      - `TextView` (12sp `@color/text_secondary`, copy `@string/security_notice`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`: Khung bao quanh form đăng nhập
- `TextInputLayout` OutlinedBox: Hộp nhập tên đăng nhập và mật khẩu
- `TextInputEditText`: Ô nhập văn bản
- `MaterialButton`: Nút đăng nhập chính
- Icons: `ic_app_logo` (slot logo), `ic_person` (Material Symbols `person`), `ic_lock` (Material Symbols `lock`), `ic_shield` (Material Symbols `security`), `ic_visibility` (Material Symbols `visibility`)

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/bg_card`, `@color/color_primary`, `@color/color_on_primary`, `@color/text_primary`, `@color/text_secondary`, `@color/color_outline_variant`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/input_corner_radius`, `@dimen/card_stroke_width`, `@dimen/card_elevation`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/app_name`
- `@string/app_tagline`
- `@string/login_title`
- `@string/login_instruction`
- `@string/username_label`
- `@string/username_hint`
- `@string/password_label`
- `@string/password_hint`
- `@string/btn_login`
- `@string/forgot_password`
- `@string/no_account_hint`
- `@string/security_notice`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Nút đăng nhập | Ô nhập liệu | Thông báo |
|:---|:---|:---|:---|
| **Default** | Kích hoạt, chữ `@string/btn_login` | Cho phép nhập bình thường | Không có lỗi |
| **Loading** | Vô hiệu hóa, chữ `@string/btn_logging_in` (xem S02) | Vô hiệu hóa chỉnh sửa | Đang tiến hành xác thực |
| **Error** | Kích hoạt lại | Viền đỏ `@color/color_error` (xem S03) | `@string/login_error_invalid_credentials` |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Tỷ lệ tương phản chữ `@color/text_primary` và `@color/text_secondary` trên nền `@color/bg_card` đạt tối thiểu 4.5:1.
- [ ] Vùng chạm của nút đăng nhập và link `@string/forgot_password` đạt tối thiểu 48dp (`@dimen/touch_target_min`).
- [ ] Không tiết lộ sự tồn tại của tài khoản: Thông báo lỗi xác thực dùng chung `@string/login_error_invalid_credentials`, không báo riêng lỗi tên đăng nhập hay mật khẩu.
- [ ] Phím Enter trên bàn phím chuyển focus từ ô tên đăng nhập sang ô mật khẩu và submit form.
