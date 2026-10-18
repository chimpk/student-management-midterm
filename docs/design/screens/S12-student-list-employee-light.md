# S12 - Sinh viên - Danh sách chỉ đọc (Employee Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 12  
> **Ảnh tham chiếu:** [S12](reference/S12-student-list-employee-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S12`
- **Tên màn:** Sinh viên - Danh sách chỉ đọc (Employee Light)
- **Route:** `student_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 12
- **Ảnh tham chiếu:** `reference/S12-student-list-employee-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` (nền `@color/color_background`)
  - `AppBarLayout` (nền `@color/color_surface`)
    - `MaterialToolbar` (tiêu đề `@string/title_students`)
    - Layout tìm kiếm và bộ lọc (tương tự S07)
  - `RecyclerView` (danh sách sinh viên)
    - Item sinh viên (nhấn vào xem chi tiết chỉ đọc)
  - `BottomNavigationView` (điều hướng)
  - **LƯU Ý:** FloatingActionButton (+ Thêm sinh viên) bị ẩn hoàn toàn (`View.GONE`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`: Tiêu đề
- `TextInputLayout` OutlinedBox: Ô tìm kiếm
- `ChipGroup`: Lọc theo Khoa, Lớp
- `RecyclerView`: Danh sách sinh viên
- Icons: `ic_search`, `ic_clear`, `ic_sort`, `ic_chevron_right`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/text_primary`, `@color/text_secondary`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/title_students`
- `@string/search_hint`
- `@string/filter_all`
- `@string/student_count_sort_synced_format`
- `@string/sort_sheet_title`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Quyền hạn tương tác |
|:---|:---|
| **Default (Employee)** | Chỉ đọc dữ liệu, ẩn hoàn toàn nút FAB thêm mới và các thao tác xóa |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Tuyệt đối không hiển thị FAB Thêm sinh viên trên giao diện Employee.
- [ ] Không có thao tác vuốt để xóa sinh viên.
