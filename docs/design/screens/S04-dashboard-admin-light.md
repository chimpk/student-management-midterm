# S04 - Dashboard - Quản trị viên (Admin Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 4  
> **Ảnh tham chiếu:** [S04](reference/S04-dashboard-admin-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S04`
- **Tên màn:** Dashboard - Quản trị viên (Admin Light)
- **Route:** `dashboard_admin`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 4
- **Ảnh tham chiếu:** `reference/S04-dashboard-admin-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` (nền `@color/color_background`)
  - `NestedScrollView` (padding `@dimen/screen_margin`)
    - `LinearLayout` (vertical)
      - Header:
        - `LinearLayout` (horizontal, căn giữa dọc)
          - Slot Logo / Avatar thu nhỏ (`~40dp x 40dp`, bo tròn, `@drawable/ic_app_logo`)
          - `LinearLayout` (vertical, trọng số 1, marginStart `~12dp`)
            - `TextView` Lời chào (18sp/500 `@color/text_primary`, format `@string/greeting_format`)
            - `TextView` Phụ đề (14sp `@color/text_secondary`, copy `@string/greeting_sub`)
          - `Chip` Trạng thái đồng bộ (nền `@color/bg_card`, viền `@color/color_outline_variant`, text 12sp `@color/color_primary`, icon `ic_cloud_done`, copy `@string/status_synced`)
      - `Space` (`~20dp`)
      - Banner thống kê sẵn sàng:
        - `MaterialCardView` (nền `@color/color_primary_container`, bo góc `@dimen/card_corner_radius`, padding `~16dp`)
          - `LinearLayout` (horizontal)
            - `LinearLayout` (vertical, trọng số 1)
              - `TextView` (16sp/500 `@color/color_on_primary_container`, copy `@string/stats_ready_banner`)
              - `TextView` (13sp `@color/color_on_primary_container`, format `@string/stats_summary_format`)
            - `ImageView` (`~40dp`, icon `ic_analytics`, tint `@color/color_primary`)
      - `Space` (`~24dp`)
      - `TextView` Tiêu đề phân khu (16sp/500 `@color/text_primary`, copy `@string/space_mgmt_title`)
      - `Space` (`~12dp`)
      - Lưới thẻ chức năng (`GridLayout` 2 cột, khoảng cách `@dimen/card_gap`):
        - Thẻ 1: Sinh viên (`MaterialCardView`, nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, viền `@dimen/card_stroke_width`)
          - Icon `ic_school` (`~32dp`, tint `@color/color_primary`)
          - `TextView` Tiêu đề (16sp/500 `@color/text_primary`, copy `@string/card_student_title`)
          - `TextView` Mô tả (13sp `@color/text_secondary`, copy `@string/card_student_sub`)
        - Thẻ 2: Người dùng (`MaterialCardView`, nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`)
          - Icon `ic_people` (`~32dp`, tint `@color/color_primary`)
          - `TextView` Tiêu đề (16sp/500 `@color/text_primary`, copy `@string/card_user_title`)
          - `TextView` Mô tả (13sp `@color/text_secondary`, copy `@string/card_user_sub`)
        - Thẻ 3: Import/Export (`MaterialCardView`, nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`)
          - Icon `ic_sync_alt` (`~32dp`, tint `@color/color_primary`)
          - `TextView` Tiêu đề (16sp/500 `@color/text_primary`, copy `@string/card_io_title`)
          - `TextView` Định dạng (13sp `@color/text_secondary`, copy `@string/card_io_format`)
        - Thẻ 4: Hồ sơ (`MaterialCardView`, nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`)
          - Icon `ic_account_circle` (`~32dp`, tint `@color/color_primary`)
          - `TextView` Tiêu đề (16sp/500 `@color/text_primary`, copy `@string/card_profile_title`)
          - `TextView` Chuyên mục (13sp `@color/text_secondary`, copy `@string/card_profile_sub`)
  - `BottomNavigationView` (nền `@color/color_surface`, viền trên `@color/color_outline_variant`, 3 tab: `@string/nav_home`, `@string/nav_students`, `@string/nav_profile`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`: Thẻ thống kê và 4 thẻ lưới chức năng
- `Chip`: Huy hiệu trạng thái đồng bộ dữ liệu
- `BottomNavigationView`: Thanh điều hướng đáy ứng dụng
- Icons: `ic_app_logo`, `ic_cloud_done`, `ic_analytics`, `ic_school`, `ic_people`, `ic_sync_alt`, `ic_account_circle`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/color_primary_container`, `@color/color_on_primary_container`, `@color/text_primary`, `@color/text_secondary`, `@color/color_outline_variant`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/card_gap`, `@dimen/card_stroke_width`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/greeting_format`
- `@string/greeting_sub`
- `@string/status_synced`
- `@string/stats_ready_banner`
- `@string/stats_summary_format`
- `@string/space_mgmt_title`
- `@string/card_student_title`
- `@string/card_student_sub`
- `@string/card_user_title`
- `@string/card_user_sub`
- `@string/card_io_title`
- `@string/card_io_format`
- `@string/card_profile_title`
- `@string/card_profile_sub`
- `@string/nav_home`
- `@string/nav_students`
- `@string/nav_profile`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Thẻ chức năng | Trạng thái đồng bộ |
|:---|:---|:---|
| **Default** | Hiển thị đầy đủ 4 thẻ cho Admin | Chip `@string/status_synced` hiển thị icon đám mây |
| **Offline** | Các thẻ chuyển sang đọc dữ liệu cache cục bộ | Chip chuyển sang trạng thái ngoại tuyến |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Bốn thẻ chức năng căn chỉnh đều theo lưới 2 cột, khoảng cách 12dp.
- [ ] Vùng chạm mỗi thẻ tối thiểu đạt chuẩn tương tác (`@dimen/touch_target_min`).
- [ ] Nhấn vào thẻ mở đúng màn hình điều hướng tương ứng.
