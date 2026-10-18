# S08 - Sinh viên - Chi tiết (Admin Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 8  
> **Ảnh tham chiếu:** [S08](reference/S08-student-detail-admin-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S08`
- **Tên màn:** Sinh viên - Chi tiết (Admin Light)
- **Route:** `student_detail`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 8
- **Ảnh tham chiếu:** `reference/S08-student-detail-admin-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` (nền `@color/color_background`)
  - `AppBarLayout` (nền `@color/color_surface`)
    - `MaterialToolbar` (nút Back, tiêu đề `@string/title_student_detail`, menu 3 chấm `ic_more_vert`)
  - `NestedScrollView` (padding `@dimen/screen_margin`)
    - `LinearLayout` (vertical)
      - Header hồ sơ:
        - `LinearLayout` (horizontal, căn giữa dọc)
          - Avatar lớn (`~56dp x 56dp`, bo tròn, nền `@color/color_primary_container`, chữ `@color/color_on_primary_container`)
          - `LinearLayout` (vertical, trọng số 1, marginStart `~16dp`)
            - `TextView` Họ tên (18sp/500 `@color/text_primary`)
            - `TextView` MSSV (14sp `@color/text_secondary`)
            - `Chip` Trạng thái học tập (nền `@color/color_secondary_container`, text `@string/status_studying`)
      - `Space` (`~20dp`)
      - `MaterialCardView` Thông tin chi tiết (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
        - `TextView` Tiêu đề khối (15sp/500 `@color/text_primary`, copy `@string/section_student_info`)
        - `TableLayout` hoặc `LinearLayout` dòng:
          - Dòng Khoa: nhãn `@string/label_faculty` • giá trị khoa
          - Dòng Lớp: nhãn `@string/label_class` • giá trị lớp
          - Dòng Ngày sinh: nhãn `@string/label_dob` • ngày tháng năm
          - Dòng Email: nhãn `@string/label_email` • địa chỉ email
      - `Space` (`~20dp`)
      - `MaterialCardView` Danh sách chứng chỉ (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
        - `LinearLayout` (horizontal, căn giữa dọc)
          - `TextView` (15sp/500 `@color/text_primary`, format `@string/section_certificates_format`)
          - `View` (trọng số 1)
          - `MaterialButton` TextButton (icon `ic_add`, text "+ Thêm chứng chỉ")
        - `RecyclerView` Danh sách chứng chỉ:
          - Item chứng chỉ: Tên chứng chỉ, Ngày cấp, Đơn vị cấp, trạng thái

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`: Tiêu đề kèm nút Back và menu tùy chọn
- `MaterialCardView`: Khối thông tin cá nhân và khối chứng chỉ
- `Chip`: Trạng thái học tập
- `RecyclerView`: Danh sách chứng chỉ con
- Icons: `ic_arrow_back`, `ic_more_vert`, `ic_school`, `ic_verified`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/color_primary_container`, `@color/text_primary`, `@color/text_secondary`, `@color/color_secondary_container`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/title_student_detail`
- `@string/status_studying`
- `@string/section_student_info`
- `@string/label_faculty`
- `@string/label_class`
- `@string/label_dob`
- `@string/label_email`
- `@string/section_certificates_format`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Menu 3 chấm | Danh sách chứng chỉ |
|:---|:---|:---|
| **Default** | Hiển thị menu Sửa và Xóa | Hiển thị các chứng chỉ đã lưu |
| **Menu Open** | Mở popup menu (xem S15) | Giữ nguyên |
| **Delete Prompt**| Mở dialog xác nhận xóa phân tầng (xem S16) | Giữ nguyên |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Hiển thị chính xác số lượng chứng chỉ trong tiêu đề format `@string/section_certificates_format`.
- [ ] Chạm nút menu 3 chấm mở menu tùy chọn Sửa và Xóa.
