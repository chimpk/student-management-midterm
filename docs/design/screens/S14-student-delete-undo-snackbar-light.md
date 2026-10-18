# S14 - Sinh viên - Hoàn tác xóa Snackbar (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 14  
> **Ảnh tham chiếu:** [S14](reference/S14-student-delete-undo-snackbar-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S14`
- **Tên màn:** Sinh viên - Hoàn tác xóa Snackbar (Light)
- **Route:** `student_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 14
- **Ảnh tham chiếu:** `reference/S14-student-delete-undo-snackbar-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giao diện danh sách sinh viên S07
- `Snackbar` (nổi phía dưới, nền `@color/text_primary`, bo góc `~8dp`, padding `~12dp`)
  - `LinearLayout` (horizontal, căn giữa dọc)
    - `TextView` Thông điệp (14sp `@color/color_surface`, format `@string/snackbar_deleted_student_format`)
    - `View` (trọng số 1)
    - `MaterialButton` TextButton (copy `@string/btn_undo`, chữ `@color/color_primary_container`, touch target `~48dp`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `Snackbar`: Thanh thông báo phản hồi thao tác kèm hành động Hoàn tác

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/text_primary`, `@color/color_surface`, `@color/color_primary_container`
- Kích thước: `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/snackbar_deleted_student_format`
- `@string/btn_undo`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Thời gian tồn tại | Hành vi |
|:---|:---|:---|
| **Active** | Hiển thị trong khoảng 4000ms | Bấm Hoàn tác khôi phục lại sinh viên vừa xóa |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Bấm Hoàn tác chèn lại bản ghi sinh viên vào đúng vị trí cũ trong danh sách.
- [ ] Vùng chạm của nút Hoàn tác đạt tối thiểu 48dp.
