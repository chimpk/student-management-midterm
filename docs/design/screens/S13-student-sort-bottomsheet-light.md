# S13 - Sinh viên - Sắp xếp BottomSheet (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 13  
> **Ảnh tham chiếu:** [S13](reference/S13-student-sort-bottomsheet-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S13`
- **Tên màn:** Sinh viên - Sắp xếp BottomSheet (Light)
- **Route:** `student_sort_sheet`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 13
- **Ảnh tham chiếu:** `reference/S13-student-sort-bottomsheet-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout`
  - Nền mờ `Scrim` (``)
  - `BottomSheetDialog` (nền `@color/color_surface`, bo góc trên `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
    - Thanh kéo `DragHandle` (`~32dp x 4dp`, căn giữa, nền `@color/color_outline_variant`)
    - `Space` (`~12dp`)
    - `TextView` Tiêu đề (18sp/500 `@color/text_primary`, copy `@string/sort_sheet_title`)
    - `TextView` Mô tả (14sp `@color/text_secondary`, copy `@string/sort_sheet_desc`)
    - `Space` (`~16dp`)
    - `RadioGroup`:
      - `RadioButton` (copy `@string/sort_name_asc`, checked=true)
      - `RadioButton` (copy `@string/sort_name_desc`)
      - `RadioButton` (copy `@string/sort_mssv_asc`)
      - `RadioButton` (copy `@string/sort_birth_asc`)
    - `Space` (`~20dp`)
    - `MaterialButton` Filled (copy `@string/btn_apply_sort`, nền `@color/color_primary`, chữ `@color/color_on_primary`, touch target `~48dp`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `BottomSheetDialog`: Khung bảng trượt từ đáy
- `RadioGroup` & `RadioButton`: Các tiêu chí sắp xếp
- `MaterialButton`: Nút Áp dụng

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_surface`, `@color/color_primary`, `@color/color_on_primary`, `@color/text_primary`, `@color/text_secondary`, `@color/color_outline_variant`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/sort_sheet_title`
- `@string/sort_sheet_desc`
- `@string/sort_name_asc`
- `@string/sort_name_desc`
- `@string/sort_mssv_asc`
- `@string/sort_birth_asc`
- `@string/btn_apply_sort`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Lựa chọn Radio |
|:---|:---|
| **Default** | Mặc định chọn `@string/sort_name_asc` |
| **Selection Changed** | Đổi Radio sang tiêu chí mới, bấm Áp dụng để kích hoạt |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Bảng trượt mở từ đáy màn hình, chạm ra ngoài để đóng.
- [ ] Áp dụng sắp xếp danh sách tức thì mà không cần tải lại toàn bộ trang.
