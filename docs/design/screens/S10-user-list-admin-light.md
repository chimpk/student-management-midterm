# S10 - Quản lý người dùng - Danh sách (Admin Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 10  
> **Ảnh tham chiếu:** [S10](reference/S10-user-list-admin-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S10`
- **Tên màn:** Quản lý người dùng - Danh sách (Admin Light)
- **Route:** `user_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 10
- **Ảnh tham chiếu:** `reference/S10-user-list-admin-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` (nền `@color/color_background`)
  - `AppBarLayout` (nền `@color/color_surface`)
    - `MaterialToolbar` (nút Back, tiêu đề `@string/title_users`)
    - `LinearLayout` (vertical, paddingHorizontal `@dimen/screen_margin`)
      - `TextView` (13sp `@color/text_secondary`, copy `@string/admin_user_mgmt_desc`)
      - `Space` (`~8dp`)
      - `TextView` (13sp/500 `@color/color_primary`, format `@string/user_count_summary_format`)
  - `RecyclerView` (danh sách người dùng, paddingHorizontal `@dimen/screen_margin`)
    - Item Người dùng (`MaterialCardView`, nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, marginVertical `~6dp`, padding `~16dp`)
      - `LinearLayout` (horizontal, căn giữa dọc)
        - Avatar chữ cái đầu / ảnh (`~44dp x 44dp`, bo tròn)
        - `LinearLayout` (vertical, trọng số 1, marginStart `~12dp`)
          - `LinearLayout` (horizontal, căn giữa dọc)
            - `TextView` Tên người dùng (16sp/500 `@color/text_primary`)
            - `TextView` Nhãn tài khoản chính mình (12sp `@color/color_primary`, copy `@string/user_you_tag`, hiển thị có điều kiện)
          - `TextView` Email • Vai trò (13sp `@color/text_secondary`)
        - Cụm trạng thái & Nút khóa:
          - Nếu đang hoạt động: `Chip` xanh `@string/status_active` + `MaterialButton` Outlined text `@string/btn_lock_account` (hoặc nhãn `@string/note_cannot_self_lock` nếu là bản thân)
          - Nếu đã khóa: `Chip` đỏ `@string/status_locked_tag` + `MaterialButton` Outlined text `@string/btn_unlock_account`

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`: Tiêu đề
- `RecyclerView`: Danh sách tài khoản người dùng
- `MaterialCardView`: Thẻ item người dùng
- `Chip`: Trạng thái hoạt động (`status_active` hoặc `status_locked_tag`)
- `MaterialButton`: Nút Khóa / Mở khóa tài khoản
- Icons: `ic_arrow_back`, `ic_person`, `ic_lock`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/color_error`, `@color/text_primary`, `@color/text_secondary`, `@color/color_outline_variant`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/title_users`
- `@string/admin_user_mgmt_desc`
- `@string/user_count_summary_format`
- `@string/user_you_tag`
- `@string/status_active`
- `@string/status_locked_tag`
- `@string/btn_lock_account`
- `@string/btn_unlock_account`
- `@string/note_cannot_self_lock`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Tài khoản bình thường | Tài khoản bản thân | Tài khoản bị khóa |
|:---|:---|:---|:---|
| **Default** | Hiện nút Khóa tài khoản | Hiện nhãn `@string/note_cannot_self_lock` | Hiện nhãn `@string/status_locked_tag` và nút Mở khóa |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Tuyệt đối không cho phép tài khoản Admin tự khóa chính mình (hiển thị `@string/note_cannot_self_lock`).
- [ ] Chạm vào hàng người dùng điều hướng sang màn hình Lịch sử đăng nhập của tài khoản đó (S11).
- [ ] Nhấn nút Khóa mở Dialog xác nhận (S21).
