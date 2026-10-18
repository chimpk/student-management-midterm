# S17 - Hồ sơ cá nhân - Quản trị viên (Admin Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 17  
> **Ảnh tham chiếu:** [S17](reference/S17-profile-admin-default-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S17`
- **Tên màn:** Hồ sơ cá nhân - Quản trị viên (Admin Light)
- **Route:** `profile`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 17
- **Ảnh tham chiếu:** `reference/S17-profile-admin-default-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` (nền `@color/color_background`)
  - `AppBarLayout` (nền `@color/color_surface`)
    - `MaterialToolbar` (tiêu đề `@string/title_profile`)
  - `NestedScrollView` (padding `@dimen/screen_margin`)
    - `LinearLayout` (vertical)
      - Khối Header cá nhân:
        - `FrameLayout` (căn giữa)
          - Avatar lớn (`~80dp x 80dp`, bo tròn, ảnh đại diện)
          - `FloatingActionButton` nhỏ (`~32dp x 32dp`, icon `ic_camera_alt`, góc dưới phải avatar)
        - `Space` (`~12dp`)
        - `TextView` Tên người dùng (18sp/500 `@color/text_primary`, căn giữa)
        - `TextView` Email (14sp `@color/text_secondary`, căn giữa)
        - `Chip` (nền `@color/color_primary_container`, text `@string/role_admin_full`, căn giữa)
      - `Space` (`~24dp`)
      - `MaterialCardView` Thông tin tài khoản (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
        - `TextView` (15sp/500 `@color/text_primary`, copy `@string/section_account_info`)
        - `LinearLayout` các dòng thông tin:
          - Đơn vị: `@string/label_unit` • `@string/unit_value_demo`
          - Vai trò: `@string/label_role` • `@string/role_admin_full`
          - Trạng thái: `@string/label_status` • `@string/status_active`
      - `Space` (`~20dp`)
      - `MaterialCardView` Lịch sử đăng nhập của tôi (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
        - `TextView` (15sp/500 `@color/text_primary`, copy `@string/section_my_login_history`)
        - Danh sách 2-3 phiên đăng nhập gần nhất của chính tài khoản này
  - `BottomNavigationView` (tab Hồ sơ được chọn)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`: Tiêu đề
- `MaterialCardView`: Thẻ thông tin tài khoản và thẻ lịch sử cá nhân
- `FloatingActionButton`: Nút đổi ảnh đại diện
- `BottomNavigationView`: Điều hướng đáy
- Icons: `ic_camera_alt`, `ic_person`, `ic_history`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/color_primary_container`, `@color/text_primary`, `@color/text_secondary`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/title_profile`
- `@string/section_account_info`
- `@string/label_unit`
- `@string/label_role`
- `@string/label_status`
- `@string/unit_value_demo`
- `@string/role_admin_full`
- `@string/status_active`
- `@string/section_my_login_history`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Avatar |
|:---|:---|
| **Default** | Hiển thị ảnh hiện tại hoặc chữ cái viết tắt |
| **Uploading** | Hiển thị thanh tiến độ tải ảnh 65% (xem S22) |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Bấm nút camera kích hoạt bộ chọn ảnh hệ thống.
- [ ] Hiển thị đúng vai trò và quyền hạn quản trị.
