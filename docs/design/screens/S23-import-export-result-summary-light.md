# S23 - Import / Export CSV - Kết quả nhập liệu (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 23  
> **Ảnh tham chiếu:** [S23](reference/S23-import-export-result-summary-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S23`
- **Tên màn:** Import / Export CSV - Kết quả nhập liệu (Light)
- **Route:** `import_export`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 23
- **Ảnh tham chiếu:** `reference/S23-import-export-result-summary-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giao diện Import/Export S18
- Thêm `MaterialCardView` Tổng kết kết quả (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`, viền `@color/color_outline_variant`)
  - `LinearLayout` (vertical)
    - `TextView` Tiêu đề (16sp/500 `@color/text_primary`, copy `@string/title_import_result`)
    - `TextView` Tóm tắt dòng (14sp/500 `@color/color_primary`, format `@string/import_summary_format`)
    - `TextView` Chi tiết số lượng (13sp `@color/text_secondary`, copy `@string/import_detail_summary`)
    - `Space` (`~12dp`)
    - Danh sách lỗi chi tiết theo dòng (Card cảnh báo dòng lỗi MSSV trùng, định dạng ngày sai)
    - `Space` (`~16dp`)
    - `MaterialButton` Outlined (copy `@string/btn_share_error_report`, icon `ic_share`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`: Bảng thống kê kết quả nhập liệu
- `MaterialButton`: Nút chia sẻ báo cáo lỗi CSV
- Icons: `ic_check_circle`, `ic_error_outline`, `ic_share`

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/bg_card`, `@color/color_primary`, `@color/color_error`, `@color/text_primary`, `@color/text_secondary`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/touch_target_min`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/title_import_result`
- `@string/import_summary_format`
- `@string/import_detail_summary`
- `@string/btn_share_error_report`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Thống kê dòng |
|:---|:---|
| **Result Summary** | Báo cáo rõ ràng: 5 dòng: 3 thành công, 2 lỗi |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Thống kê chính xác số dòng thành công và số dòng thất bại.
- [ ] Cho phép sao chép hoặc chia sẻ báo cáo danh sách dòng bị lỗi.
