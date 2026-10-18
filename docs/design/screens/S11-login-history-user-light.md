# S11 - Lịch sử đăng nhập - Xem tài khoản (Admin Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 11  
> **Ảnh tham chiếu:** [S11](reference/S11-login-history-user-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S11`
- **Tên màn:** Lịch sử đăng nhập - Xem tài khoản (Admin Light)
- **Route:** `login_history_user`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 11
- **Ảnh tham chiếu:** `reference/S11-login-history-user-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` (nền `@color/color_background`)
  - `AppBarLayout` (nền `@color/color_surface`)
    - `MaterialToolbar` (nút Back, tiêu đề `@string/title_login_history`)
    - `LinearLayout` (vertical, paddingHorizontal `@dimen/screen_margin`)
      - `TextView` (13sp `@color/text_secondary`, copy `@string/login_history_desc_format`)
  - `RecyclerView` (danh sách phiên đăng nhập theo nhóm ngày)
    - Tiêu đề nhóm ngày: `TextView` (13sp/500 `@color/color_primary`, copy `@string/date_group_today` hoặc `@string/date_group_yesterday`)
    - Item Lịch sử (`MaterialCardView`, nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, marginVertical `~4dp`, padding `~12dp`)
      - `LinearLayout` (horizontal, căn giữa dọc)
        - `ImageView` (`~24dp`, icon `ic_check_circle`, tint `@color/color_primary`)
        - `LinearLayout` (vertical, trọng số 1, marginStart `~12dp`)
          - `LinearLayout` (horizontal)
            - `TextView` Giờ đăng nhập (15sp/500 `@color/text_primary`, ví dụ `09:30 AM`)
            - `Chip` (copy `@string/login_current_session_badge`, hiển thị nếu là phiên hiện tại)
          - `TextView` Thiết bị • Vị trí / Địa chỉ IP (13sp `@color/text_secondary`)
        - `TextView` Trạng thái (13sp `@color/color_primary`, copy `@string/login_status_success`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`: Tiêu đề
- `RecyclerView`: Danh sách nhóm theo ngày
- `MaterialCardView`: Khung hiển thị từng bản ghi phiên
- `Chip`: Huy hiệu "Phiên hiện tại"
- Icons: `ic_arrow_back`, `ic_check_circle`, `ic_devices`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/text_primary`, `@color/text_secondary`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/title_login_history`
- `@string/login_history_desc_format`
- `@string/date_group_today`
- `@string/date_group_yesterday`
- `@string/login_status_success`
- `@string/login_current_session_badge`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Bản ghi thường | Bản ghi phiên đang dùng |
|:---|:---|:---|
| **Default** | Hiện giờ, thiết bị, địa chỉ IP, trạng thái Thành công | Có thêm badge `@string/login_current_session_badge` |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Bản ghi được gom nhóm chính xác theo ngày gần nhất.
- [ ] Phiên đăng nhập đang diễn ra có đánh dấu huy hiệu nổi bật.
