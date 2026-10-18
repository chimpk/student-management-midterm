# S18 - Import / Export CSV - Mặc định (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 18  
> **Ảnh tham chiếu:** [S18](reference/S18-import-export-default-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S18`
- **Tên màn:** Import / Export CSV - Mặc định (Light)
- **Route:** `import_export`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 18
- **Ảnh tham chiếu:** `reference/S18-import-export-default-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` (nền `@color/color_background`)
  - `AppBarLayout` (nền `@color/color_surface`)
    - `MaterialToolbar` (nút Back, tiêu đề `@string/title_import_export`)
    - `TextView` Phụ đề (13sp `@color/text_secondary`, copy `@string/io_subtitle`, paddingHorizontal `@dimen/screen_margin`)
  - `NestedScrollView` (padding `@dimen/screen_margin`)
    - `LinearLayout` (vertical)
      - Khối Import dữ liệu:
        - `MaterialCardView` (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
          - `TextView` Tiêu đề (16sp/500 `@color/text_primary`, copy `@string/section_import_data`)
          - `Chip` Định dạng (nền `@color/color_surface_variant`, text `@string/io_import_spec`)
          - `Space` (`~8dp`)
          - `TextView` Hướng dẫn (13sp `@color/text_secondary`, copy `@string/io_import_desc`)
          - `Space` (`~16dp`)
          - `LinearLayout` (horizontal)
            - `MaterialButton` Filled (copy `@string/btn_choose_csv_file`, nền `@color/color_primary`, icon `ic_file_upload`)
            - `Space` (`~12dp`)
            - `MaterialButton` Outlined (copy `@string/btn_download_csv_template`, icon `ic_download`)
      - `Space` (`~24dp`)
      - Khối Export dữ liệu:
        - `MaterialCardView` (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`)
          - `TextView` Tiêu đề (16sp/500 `@color/text_primary`, copy `@string/section_export_data`)
          - `Chip` Định dạng (format `@string/io_export_spec_format`)
          - `Space` (`~8dp`)
          - `TextView` Hướng dẫn (13sp `@color/text_secondary`, copy `@string/io_export_desc`)
          - `Space` (`~16dp`)
          - `MaterialButton` Filled (copy `@string/btn_export_csv`, nền `@color/color_primary`, icon `ic_file_download`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`: Tiêu đề
- `MaterialCardView`: Khối Import và Export
- `Chip`: Quy cách tệp tệp tin
- `MaterialButton`: Các nút tải lên, tải mẫu và xuất CSV
- Icons: `ic_arrow_back`, `ic_file_upload`, `ic_download`, `ic_file_download`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface`, `@color/bg_card`, `@color/color_primary`, `@color/text_primary`, `@color/text_secondary`, `@color/color_surface_variant`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/title_import_export`
- `@string/io_subtitle`
- `@string/section_import_data`
- `@string/io_import_spec`
- `@string/io_import_desc`
- `@string/btn_choose_csv_file`
- `@string/btn_download_csv_template`
- `@string/section_export_data`
- `@string/io_export_spec_format`
- `@string/io_export_desc`
- `@string/btn_export_csv`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Nút chọn tệp | Kết quả import |
|:---|:---|:---|
| **Default** | Sẵn sàng tương tác | Chưa hiển thị |
| **Result Summary**| Sau khi chọn tệp và xử lý xong | Hiển thị bảng kết quả (xem S23) |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Nút chọn tệp mở bộ chọn file hệ thống lọc MIME `text/csv`.
- [ ] Nút xuất dữ liệu tạo tệp CSV mã hóa UTF-8 chuẩn xác.
