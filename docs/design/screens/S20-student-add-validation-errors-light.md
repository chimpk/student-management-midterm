# S20 - Sinh viên - Lỗi xác thực Form (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 20  
> **Ảnh tham chiếu:** [S20](reference/S20-student-add-validation-errors-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S20`
- **Tên màn:** Sinh viên - Lỗi xác thực Form (Light)
- **Route:** `student_add`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 20
- **Ảnh tham chiếu:** `reference/S20-student-add-validation-errors-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giao diện form thêm sinh viên S05
- Ô MSSV:
  - `TextInputLayout` OutlinedBox (viền `@color/color_error`, errorEnabled=true, errorText `@string/error_mssv_exists_format`)
- Ô Email:
  - `TextInputLayout` OutlinedBox (viền `@color/color_error`, errorEnabled=true, errorText `@string/error_email_invalid_example`)
- Các ô hợp lệ khác: Giữ nguyên dữ liệu đã nhập

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `TextInputLayout` OutlinedBox: Trạng thái hiển thị lỗi xác thực
- `MaterialButton`: Nút Lưu sẵn sàng để thử lại

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_error`, `@color/color_surface`, `@color/text_primary`
- Kích thước: `@dimen/input_corner_radius`, `@dimen/card_stroke_width`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/error_mssv_exists_format`
- `@string/error_email_invalid_example`
- `@string/btn_save`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Lỗi hiển thị |
|:---|:---|
| **Validation Error** | Báo lỗi trùng MSSV và sai định dạng Email ngay dưới từng ô tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Tuyệt đối không xóa trắng các ô thông tin đã điền hợp lệ khác.
- [ ] Focus tự động di chuyển về ô lỗi đầu tiên kèm thông báo TalkBack.
