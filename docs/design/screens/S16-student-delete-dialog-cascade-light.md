# S16 - Sinh viên - Dialog xác nhận xóa phân tầng (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 16  
> **Ảnh tham chiếu:** [S16](reference/S16-student-delete-dialog-cascade-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S16`
- **Tên màn:** Sinh viên - Dialog xác nhận xóa phân tầng (Light)
- **Route:** `student_delete_dialog`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 16
- **Ảnh tham chiếu:** `reference/S16-student-delete-dialog-cascade-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `MaterialAlertDialog` (nền `@color/color_surface`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
  - `LinearLayout` (vertical)
    - `ImageView` (`~32dp`, icon `ic_warning`, tint `@color/color_error`)
    - `TextView` Tiêu đề (18sp/500 `@color/text_primary`, copy `@string/dialog_delete_student_title`)
    - `Space` (`~12dp`)
    - `TextView` Cảnh báo phân tầng (14sp `@color/text_secondary`, format `@string/dialog_delete_student_warning_format`)
    - `Space` (`~24dp`)
    - `LinearLayout` (horizontal, căn phải)
      - `MaterialButton` TextButton (copy `@string/btn_cancel`, chữ `@color/text_secondary`)
      - `Space` (`~8dp`)
      - `MaterialButton` Filled (copy `@string/btn_confirm_delete`, nền `@color/color_error`, chữ `@color/color_on_primary`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialAlertDialog`: Hộp thoại cảnh báo nguy hiểm
- `MaterialButton`: Nút xác nhận xóa và nút Hủy
- Icons: `ic_warning` (Material Symbols `warning`)

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_surface`, `@color/color_error`, `@color/color_on_primary`, `@color/text_primary`, `@color/text_secondary`
- Kích thước: `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/dialog_delete_student_title`
- `@string/dialog_delete_student_warning_format`
- `@string/btn_cancel`
- `@string/btn_confirm_delete`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Nội dung cảnh báo |
|:---|:---|
| **Active** | Nêu rõ số lượng chứng chỉ sẽ bị xóa theo dạng cascade |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Hiển thị chính xác số chứng chỉ bị xóa kèm theo trong chuỗi format.
- [ ] Nút xóa mang màu đỏ cảnh báo `@color/color_error`.
