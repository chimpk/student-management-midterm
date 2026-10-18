# S02 - Đăng nhập - Đang xử lý (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 2  
> **Ảnh tham chiếu:** [S02](reference/S02-login-loading-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S02`
- **Tên màn:** Đăng nhập - Đang xử lý (Light)
- **Route:** `login`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 2
- **Ảnh tham chiếu:** `reference/S02-login-loading-light.png`
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
        - `TextInputLayout` OutlinedBox (disabled, text người dùng đã nhập)
        - `TextInputLayout` OutlinedBox (disabled, text password đã nhập ẩn dấu chấm)
        - `MaterialButton` Filled (disabled, nền `@color/color_primary_container`, chữ `@color/color_on_primary_container`, icon xoay hoặc copy `@string/btn_logging_in`)
    - `TextView` `@string/no_account_hint`

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`: Khung bao form
- `TextInputLayout` OutlinedBox: Vô hiệu hóa tương tác (enabled=false)
- `MaterialButton`: Trạng thái Loading, copy `@string/btn_logging_in`
- `CircularProgressIndicator` (indeterminate, kích thước `~20dp`, màu `@color/color_on_primary`) bên trong nút bấm
- Icons: `ic_app_logo`, `ic_person`, `ic_lock`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/bg_card`, `@color/color_primary`, `@color/color_primary_container`, `@color/color_on_primary_container`, `@color/text_disabled`, `@color/color_outline_variant`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/input_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/app_name`
- `@string/app_tagline`
- `@string/login_title`
- `@string/login_instruction`
- `@string/username_label`
- `@string/password_label`
- `@string/btn_logging_in`
- `@string/no_account_hint`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Nút đăng nhập | Ô nhập liệu |
|:---|:---|:---|
| **Loading** | Disabled, hiển thị `@string/btn_logging_in` kèm progress indicator | Disabled, ngăn người dùng chỉnh sửa trong lúc gửi request |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Nút đăng nhập bị vô hiệu hóa click lặp lại để ngăn chặn gửi nhiều request xác thực đồng thời.
- [ ] Không xóa trắng trường tên đăng nhập và mật khẩu trong quá trình tải.
- [ ] Trình đọc màn hình thông báo trạng thái đang đăng nhập qua live region.
