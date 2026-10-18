# S09 - Dashboard - Nhân viên tra cứu (Employee Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 9  
> **Ảnh tham chiếu:** [S09](reference/S09-dashboard-employee-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S09`
- **Tên màn:** Dashboard - Nhân viên tra cứu (Employee Light)
- **Route:** `dashboard_employee`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 9
- **Ảnh tham chiếu:** `reference/S09-dashboard-employee-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` (nền `@color/color_background`)
  - `NestedScrollView` (padding `@dimen/screen_margin`)
    - `LinearLayout` (vertical)
      - Header tương tự S04 (Lời chào format `@string/greeting_format`, chip `@string/status_synced`)
      - `Space` (`~20dp`)
      - `MaterialCardView` Tra cứu sinh viên (nền `@color/color_primary_container`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
        - `LinearLayout` (vertical)
          - `ImageView` (`~48dp`, icon `ic_search_check`, tint `@color/color_primary`)
          - `TextView` (18sp/500 `@color/color_on_primary_container`, copy `@string/employee_dashboard_title`)
          - `Space` (`~8dp`)
          - `TextView` (14sp `@color/color_on_primary_container`, copy `@string/employee_dashboard_desc`)
          - `Space` (`~16dp`)
          - `MaterialButton` Filled (copy `@string/nav_students`, nền `@color/color_primary`, chữ `@color/color_on_primary`)
  - `BottomNavigationView` (nền `@color/color_surface`, tab Trang chủ được chọn)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`: Thẻ tra cứu dành riêng cho Employee
- `MaterialButton`: Nút chuyển hướng sang danh sách sinh viên
- `BottomNavigationView`: Điều hướng đáy
- Icons: `ic_app_logo`, `ic_cloud_done`, `ic_search`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/color_primary_container`, `@color/color_on_primary_container`, `@color/color_primary`, `@color/color_on_primary`, `@color/text_primary`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/greeting_format`
- `@string/greeting_sub`
- `@string/status_synced`
- `@string/employee_dashboard_title`
- `@string/employee_dashboard_desc`
- `@string/nav_students`
- `@string/nav_home`
- `@string/nav_profile`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Quyền hạn |
|:---|:---|
| **Default** | Ẩn hoàn toàn thẻ Người dùng và Import/Export so với Admin Dashboard |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Tuyệt đối không hiển thị thẻ Người dùng và Thẻ Import/Export với vai trò Employee.
- [ ] Chạm nút "Sinh viên" chuyển sang màn hình S12 (danh sách chỉ đọc).
