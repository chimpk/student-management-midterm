# S06 - Sinh viên - Sửa đổi (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 6  
> **Ảnh tham chiếu:** [S06](reference/S06-student-edit-default-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S06`
- **Tên màn:** Sinh viên - Sửa đổi (Light)
- **Route:** `student_edit`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 6
- **Ảnh tham chiếu:** `reference/S06-student-edit-default-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` (nền `@color/color_background`)
  - `AppBarLayout` (nền `@color/color_surface`)
    - `MaterialToolbar` (nút Back, tiêu đề `@string/title_edit_student`, format `@string/subtitle_edit_student_format`)
  - `NestedScrollView` (padding `@dimen/screen_margin`)
    - `LinearLayout` (vertical)
      - `MaterialCardView` (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
        - `LinearLayout` (vertical)
          - `TextInputLayout` OutlinedBox (label `@string/field_mssv_required`, disabled / read-only)
            - `TextInputEditText` (hiển thị MSSV hiện tại, text `@color/text_disabled`)
          - `TextView` (12sp `@color/text_secondary`, copy `@string/note_mssv_immutable`)
          - `Space` (`~12dp`)
          - `TextInputLayout` OutlinedBox (label `@string/field_name_required`)
            - `TextInputEditText` (điền sẵn tên sinh viên)
          - `Space` (`~12dp`)
          - `TextInputLayout` OutlinedBox (label `@string/field_dob_required`, icon lịch)
            - `TextInputEditText` (điền sẵn ngày sinh)
          - `Space` (`~12dp`)
          - `TextInputLayout` OutlinedBox (label `@string/field_faculty_required`, dropdown)
            - `AutoCompleteTextView` (chọn sẵn khoa hiện tại)
          - `Space` (`~12dp`)
          - `TextInputLayout` OutlinedBox (label `@string/field_class_required`, dropdown)
            - `AutoCompleteTextView` (chọn sẵn lớp hiện tại)
          - `Space` (`~12dp`)
          - `TextInputLayout` OutlinedBox (label `@string/field_email_required`)
            - `TextInputEditText` (điền sẵn email)
          - `Space` (`~16dp`)
          - `TextView` (12sp `@color/text_secondary`, format `@string/note_keep_certificates_format`)
      - `Space` (`~24dp`)
      - `LinearLayout` (horizontal, căn phải)
        - `MaterialButton` TextButton (copy `@string/btn_cancel`)
        - `MaterialButton` Filled (copy `@string/btn_save`, nền `@color/color_primary`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`: Thanh công cụ điều hướng
- `TextInputLayout` OutlinedBox: Ô MSSV bị vô hiệu hóa chỉnh sửa, các ô khác cho phép sửa
- `MaterialButton`: Nút Lưu và Hủy
- Icons: `ic_arrow_back`, `ic_calendar_today`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/text_primary`, `@color/text_disabled`, `@color/text_secondary`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/input_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/title_edit_student`
- `@string/subtitle_edit_student_format`
- `@string/note_mssv_immutable`
- `@string/field_mssv_required`
- `@string/field_name_required`
- `@string/field_dob_required`
- `@string/field_faculty_required`
- `@string/field_class_required`
- `@string/field_email_required`
- `@string/note_keep_certificates_format`
- `@string/btn_save`
- `@string/btn_cancel`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Ô MSSV | Các trường khác |
|:---|:---|:---|
| **Default** | Bị khóa (read-only), không cho phép sửa | Điền sẵn dữ liệu cũ của sinh viên |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] MSSV tuyệt đối không cho phép chỉnh sửa để bảo toàn tính toàn vẹn khóa chính.
- [ ] Lưu thành công bảo toàn toàn bộ chứng chỉ con hiện có.
