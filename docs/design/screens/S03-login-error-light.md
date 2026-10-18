# S03 - Đăng nhập - Lỗi xác thực (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 3  
> **Ảnh tham chiếu:** [S03](reference/S03-login-error-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S03`
- **Tên màn:** Đăng nhập - Lỗi xác thực (Light)
- **Route:** `login`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 3
- **Ảnh tham chiếu:** `reference/S03-login-error-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `ScrollView` (toàn màn hình, nền `@color/color_background`)
  - `LinearLayout` (vertical, padding `@dimen/screen_margin`, căn giữa)
    - Slot Logo thương hiệu (`~64dp x 64dp`, nền `@color/color_primary`, icon `@drawable/ic_app_logo`)
    - `TextView` App Name (copy `@string/app_name`)
    - `TextView` Tagline (copy `@string/app_tagline`)
    - `MaterialCardView` (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`)
      - `LinearLayout` (vertical, padding `@dimen/screen_margin`)
        - `TextView` Tiêu đề form (copy `@string/login_title`)
        - `TextView` Hướng dẫn (copy `@string/login_instruction`)
        - `MaterialCardView` Banner lỗi inline (nền `@color/color_error_container`, viền `@color/color_error`, padding `~12dp`, bo góc `@dimen/input_corner_radius`)
          - `LinearLayout` (horizontal)
            - `ImageView` (`~20dp`, icon `ic_error`, tint `@color/color_error`)
            - `TextView` (14sp `@color/color_on_error_container`, copy `@string/login_error_invalid_credentials`)
        - `Space` (`~12dp`)
        - `TextInputLayout` OutlinedBox (viền `@color/color_error`)
          - `TextInputEditText` (giữ nguyên username đã nhập)
        - `Space` (`~12dp`)
        - `TextInputLayout` OutlinedBox (viền `@color/color_error`)
          - `TextInputEditText` (xóa mật khẩu hoặc giữ nguyên theo cấu hình bảo mật)
        - `MaterialButton` Filled (nền `@color/color_primary`, copy `@string/btn_login`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`: Banner lỗi inline cảnh báo xác thực
- `TextInputLayout` OutlinedBox: Trạng thái lỗi viền đỏ
- `MaterialButton`: Nút đăng nhập phục hồi trạng thái sẵn sàng
- Icons: `ic_app_logo`, `ic_person`, `ic_lock`, `ic_error` (Material Symbols `error`)

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/bg_card`, `@color/color_error`, `@color/color_error_container`, `@color/color_on_error_container`, `@color/color_primary`, `@color/text_primary`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/input_corner_radius`, `@dimen/card_stroke_width`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/app_name`
- `@string/app_tagline`
- `@string/login_title`
- `@string/login_error_invalid_credentials`
- `@string/username_label`
- `@string/password_label`
- `@string/btn_login`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Banner lỗi | Ô nhập liệu |
|:---|:---|:---|
| **Error** | Hiển thị thông báo `@string/login_error_invalid_credentials` | Đổi màu viền sang `@color/color_error`, giữ nguyên dữ liệu username |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Tuyệt đối không tiết lộ tài khoản tồn tại (dùng đúng `@string/login_error_invalid_credentials`).
- [ ] Giữ nguyên trường tên đăng nhập đã nhập, không bắt người dùng gõ lại từ đầu.
- [ ] Màu chữ lỗi `@color/color_on_error_container` trên nền `@color/color_error_container` đạt tỷ lệ tương phản tối thiểu 4.5:1.
