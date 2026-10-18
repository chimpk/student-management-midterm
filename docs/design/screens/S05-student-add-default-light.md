# S05 - Sinh viên - Thêm mới (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 5  
> **Ảnh tham chiếu:** [S05](reference/S05-student-add-default-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S05`
- **Tên màn:** Sinh viên - Thêm mới (Light)
- **Route:** `student_add`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 5
- **Ảnh tham chiếu:** `reference/S05-student-add-default-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` (nền `@color/color_background`)
  - `AppBarLayout` (nền `@color/color_surface`, elevation 0)
    - `MaterialToolbar` (nút Back `ic_arrow_back`, tiêu đề `@string/title_add_student`, phụ đề `@string/subtitle_add_student`)
  - `NestedScrollView` (padding `@dimen/screen_margin`)
    - `LinearLayout` (vertical)
      - `TextView` Chú thích bắt buộc (13sp `@color/text_secondary`, copy `@string/form_required_note`)
      - `Space` (`~16dp`)
      - `MaterialCardView` (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
        - `LinearLayout` (vertical)
          - `TextInputLayout` OutlinedBox (label `@string/field_mssv_required`, bo góc `@dimen/input_corner_radius`)
            - `TextInputEditText` (inputType `textCapCharacters`)
          - `Space` (`~12dp`)
          - `TextInputLayout` OutlinedBox (label `@string/field_name_required`)
            - `TextInputEditText` (inputType `textPersonName`)
          - `Space` (`~12dp`)
          - `TextInputLayout` OutlinedBox (label `@string/field_dob_required`, endIconMode `custom`, endIconDrawable `ic_calendar_today`)
            - `TextInputEditText` (focusable=false, mở DatePicker khi click)
          - `Space` (`~12dp`)
          - `TextInputLayout` OutlinedBox (label `@string/field_faculty_required`, style ExposedDropdownMenu)
            - `AutoCompleteTextView` (danh sách khoa)
          - `Space` (`~12dp`)
          - `TextInputLayout` OutlinedBox (label `@string/field_class_required`, style ExposedDropdownMenu)
            - `AutoCompleteTextView` (danh sách lớp)
          - `Space` (`~12dp`)
          - `TextInputLayout` OutlinedBox (label `@string/field_email_required`)
            - `TextInputEditText` (inputType `textEmailAddress`)
          - `Space` (`~16dp`)
          - `TextView` (12sp `@color/text_secondary`, copy `@string/note_new_student_status`)
      - `Space` (`~24dp`)
      - `LinearLayout` (horizontal, căn phải)
        - `MaterialButton` TextButton (copy `@string/btn_cancel`, chữ `@color/color_primary`)
        - `Space` (`~12dp`)
        - `MaterialButton` Filled (copy `@string/btn_save`, nền `@color/color_primary`, chữ `@color/color_on_primary`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`: Thanh tiêu đề kèm nút quay lại
- `TextInputLayout` OutlinedBox: Các ô nhập thông tin sinh viên
- `AutoCompleteTextView`: Menu chọn Khoa và Lớp
- `MaterialButton`: Nút Lưu và Hủy
- Icons: `ic_arrow_back` (Material Symbols `arrow_back`), `ic_calendar_today` (Material Symbols `calendar_today`)

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/color_on_primary`, `@color/text_primary`, `@color/text_secondary`, `@color/color_outline_variant`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/input_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/title_add_student`
- `@string/subtitle_add_student`
- `@string/form_required_note`
- `@string/field_mssv_required`
- `@string/field_name_required`
- `@string/field_dob_required`
- `@string/field_faculty_required`
- `@string/field_class_required`
- `@string/field_email_required`
- `@string/note_new_student_status`
- `@string/btn_save`
- `@string/btn_cancel`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Form nhập | Nút Lưu |
|:---|:---|:---|
| **Default** | Các trường trống sẵn sàng nhập | Kích hoạt |
| **DatePicker Active** | Mở dialog chọn ngày (xem S19) | Tạm khóa tương tác |
| **Validation Error** | Báo đỏ các ô không hợp lệ (xem S20) | Giữ nguyên dữ liệu đã nhập |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Mọi trường có dấu * đều yêu cầu validate không được để trống trước khi lưu.
- [ ] Chạm vào ô Ngày sinh kích hoạt DatePicker Dialog tiêu chuẩn.
- [ ] Nhấn nút Hủy đóng màn hình và không lưu thay đổi.
