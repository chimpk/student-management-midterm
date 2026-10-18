# S07 - Sinh viên - Danh sách (Admin Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 7  
> **Ảnh tham chiếu:** [S07](reference/S07-student-list-admin-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S07`
- **Tên màn:** Sinh viên - Danh sách (Admin Light)
- **Route:** `student_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 7
- **Ảnh tham chiếu:** `reference/S07-student-list-admin-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` (nền `@color/color_background`)
  - `AppBarLayout` (nền `@color/color_surface`)
    - `MaterialToolbar` (tiêu đề `@string/title_students`)
    - `LinearLayout` (vertical, paddingHorizontal `@dimen/screen_margin`)
      - `TextInputLayout` OutlinedBox (hint `@string/search_hint`, icon đầu `ic_search`, icon cuối `ic_clear`)
        - `TextInputEditText`
      - `Space` (`~8dp`)
      - `HorizontalScrollView` (dải Filter Chips)
        - `ChipGroup` (singleSelection)
          - `Chip` (copy `@string/filter_all`, checked)
          - `Chip` (copy `@string/filter_faculty_all`, icon dropdown)
          - `Chip` (copy `@string/filter_class_all`, icon dropdown)
      - `Space` (`~8dp`)
      - `LinearLayout` (horizontal, căn giữa dọc)
        - `TextView` (13sp `@color/text_secondary`, format `@string/student_count_sort_synced_format`)
        - `View` (trọng số 1)
        - `MaterialButton` Outlined (icon `ic_sort`, copy `@string/sort_sheet_title`, touch target `~48dp`)
  - `RecyclerView` (danh sách sinh viên, paddingHorizontal `@dimen/screen_margin`)
    - Item Sinh viên (`MaterialCardView`, nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, marginVertical `~6dp`)
      - `LinearLayout` (horizontal, padding `~16dp`, căn giữa dọc)
        - Avatar chữ cái đầu (`~44dp x 44dp`, nền `@color/color_primary_container`, chữ `@color/color_on_primary_container`)
        - `LinearLayout` (vertical, trọng số 1, marginStart `~12dp`)
          - `TextView` Họ và tên (16sp/500 `@color/text_primary`)
          - `TextView` MSSV • Khoa • Lớp (13sp `@color/text_secondary`)
        - `ImageView` (`~24dp`, icon `ic_chevron_right`, tint `@color/text_secondary`)
  - `FloatingActionButton` (icon `ic_add`, nền `@color/color_primary`, tint `@color/color_on_primary`, margin `16dp`)
  - `BottomNavigationView` (tab Sinh viên được chọn)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`: Thanh tiêu đề
- `TextInputLayout` OutlinedBox: Ô tìm kiếm
- `ChipGroup` & `Chip`: Dải chip lọc nhanh
- `RecyclerView`: Danh sách cuộn sinh viên
- `FloatingActionButton`: Nút thêm mới nổi
- `BottomNavigationView`: Điều hướng đáy
- Icons: `ic_search`, `ic_clear`, `ic_sort`, `ic_chevron_right`, `ic_add`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/color_primary_container`, `@color/text_primary`, `@color/text_secondary`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/card_gap`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/title_students`
- `@string/search_hint`
- `@string/filter_all`
- `@string/filter_faculty_all`
- `@string/filter_class_all`
- `@string/student_count_sort_synced_format`
- `@string/sort_sheet_title`
- `@string/btn_add_fab`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Danh sách sinh viên | FAB |
|:---|:---|:---|
| **Default** | Hiển thị danh sách thẻ sinh viên | Hiển thị FAB thêm mới |
| **Loading** | Hiển thị 3 khung xương Shimmer (xem S24) | Vô hiệu hóa tạm thời |
| **Empty** | Hiển thị màn hình trống kèm CTA (xem S25) | Vẫn hiển thị |
| **Offline** | Xuất hiện thanh banner ngoại tuyến (xem S26) | Hiển thị |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Tìm kiếm lọc dữ liệu tức thì theo Tên hoặc MSSV.
- [ ] Chạm vào bất kỳ sinh viên nào mở màn hình Chi tiết sinh viên tương ứng.
- [ ] FAB có vùng chạm tối thiểu 56dp x 56dp.
