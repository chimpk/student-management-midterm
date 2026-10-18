# S19 - Sinh viên - Bộ chọn ngày sinh DatePicker (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 19  
> **Ảnh tham chiếu:** [S19](reference/S19-student-add-datepicker-dialog-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S19`
- **Tên màn:** Sinh viên - Bộ chọn ngày sinh DatePicker (Light)
- **Route:** `student_datepicker_dialog`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 19
- **Ảnh tham chiếu:** `reference/S19-student-add-datepicker-dialog-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `MaterialDatePickerDialog` (nền `@color/color_surface`, bo góc `@dimen/card_corner_radius`)
  - Header:
    - `TextView` (12sp `@color/text_secondary`, copy `@string/datepicker_title`)
    - `TextView` Ngày đang chọn (24sp/500 `@color/color_primary`, ví dụ `T6, 15 thg 10`)
  - Lịch chọn ngày tháng năm theo tháng (Material Calendar Grid)
  - Footer hành động:
    - `MaterialButton` TextButton (copy `@string/btn_cancel`)
    - `MaterialButton` TextButton (copy `@string/btn_save` hoặc "OK")

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialDatePicker`: Bộ chọn ngày chuẩn Material Design
- Icons: `ic_keyboard_arrow_left`, `ic_keyboard_arrow_right`, `ic_edit`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_surface`, `@color/color_primary`, `@color/color_on_primary`, `@color/text_primary`, `@color/text_secondary`
- Kích thước: `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/datepicker_title`
- `@string/btn_cancel`
- `@string/btn_save`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hành động |
|:---|:---|
| **Active** | Người dùng chọn ngày và bấm OK để điền vào ô Ngày sinh |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Không cho phép chọn ngày sinh trong tương lai.
- [ ] Định dạng ngày chọn hiển thị theo chuẩn `dd/MM/yyyy`.
