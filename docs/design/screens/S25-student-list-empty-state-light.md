# S25 - Sinh viên - Danh sách trống (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 25  
> **Ảnh tham chiếu:** [S25](reference/S25-student-list-empty-state-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S25`
- **Tên màn:** Sinh viên - Danh sách trống (Light)
- **Route:** `student_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 25
- **Ảnh tham chiếu:** `reference/S25-student-list-empty-state-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giao diện danh sách sinh viên S07
- Vùng giữa màn hình `EmptyStateLayout` (căn giữa toàn màn hình):
  - `ImageView` (`~64dp x 64dp`, icon `ic_folder_open`, tint `@color/text_secondary`)
  - `Space` (`~16dp`)
  - `TextView` Tiêu đề rỗng (18sp/500 `@color/text_primary`, copy `@string/student_empty_title`)
  - `Space` (`~8dp`)
  - `TextView` Mô tả (14sp `@color/text_secondary`, copy `@string/student_empty_desc`, căn giữa)
  - `Space` (`~24dp`)
  - `LinearLayout` (horizontal)
    - `MaterialButton` Filled (copy `@string/btn_add_student_inline`, nền `@color/color_primary`, chữ `@color/color_on_primary`)
    - `Space` (`~12dp`)
    - `MaterialButton` Outlined (copy `@string/btn_import_csv_inline`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `ImageView`: Minh họa thư mục trống
- `MaterialButton`: 2 nút hành động trực tiếp tạo mới hoặc nhập CSV
- Icons: `ic_folder_open` (Material Symbols `folder_open`), `ic_add`, `ic_upload_file`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_primary`, `@color/color_on_primary`, `@color/text_primary`, `@color/text_secondary`
- Kích thước: `@dimen/screen_margin`, `@dimen/input_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/student_empty_title`
- `@string/student_empty_desc`
- `@string/btn_add_student_inline`
- `@string/btn_import_csv_inline`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Nút hành động |
|:---|:---|
| **Empty State** | Cung cấp 2 lựa chọn: Thêm sinh viên thủ công hoặc Nhập từ CSV |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Xuất hiện khi hệ thống chưa có dữ liệu hoặc bộ lọc không tìm thấy kết quả nào.
- [ ] Bấm nút chuyển đúng màn hình thêm mới hoặc import CSV.
