# S21 - Quản lý người dùng - Dialog khóa tài khoản (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 21  
> **Ảnh tham chiếu:** [S21](reference/S21-user-lock-dialog-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S21`
- **Tên màn:** Quản lý người dùng - Dialog khóa tài khoản (Light)
- **Route:** `user_lock_dialog`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 21
- **Ảnh tham chiếu:** `reference/S21-user-lock-dialog-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `MaterialAlertDialog` (nền `@color/color_surface`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
  - `LinearLayout` (vertical)
    - `ImageView` (`~32dp`, icon `ic_lock_person`, tint `@color/color_error`)
    - `TextView` Tiêu đề (18sp/500 `@color/text_primary`, copy `@string/dialog_lock_user_title`)
    - `Space` (`~12dp`)
    - `TextView` Mô tả tác động (14sp `@color/text_secondary`, format `@string/dialog_lock_user_desc_format`)
    - `Space` (`~24dp`)
    - `LinearLayout` (horizontal, căn phải)
      - `MaterialButton` TextButton (copy `@string/btn_cancel`, chữ `@color/text_secondary`)
      - `Space` (`~8dp`)
      - `MaterialButton` Filled (copy `@string/btn_lock_account`, nền `@color/color_error`, chữ `@color/color_on_primary`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialAlertDialog`: Hộp thoại xác nhận khóa
- `MaterialButton`: Nút xác nhận khóa và Hủy
- Icons: `ic_lock_person` (Material Symbols `lock_person`)

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_surface`, `@color/color_error`, `@color/color_on_primary`, `@color/text_primary`, `@color/text_secondary`
- Kích thước: `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/dialog_lock_user_title`
- `@string/dialog_lock_user_desc_format`
- `@string/btn_cancel`
- `@string/btn_lock_account`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Mô tả |
|:---|:---|
| **Active** | Cảnh báo người dùng bị khóa sẽ không thể đăng nhập vào hệ thống |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Điền chính xác tên và email người dùng vào chuỗi định dạng cảnh báo.
- [ ] Khóa thành công cập nhật trạng thái sang `status_label_locked`.
