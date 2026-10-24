# S67 - Export chứng chỉ (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 59  
> **Ảnh tham chiếu:** [S67](../reference/S67-certificate-export-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S67`
- **Tên màn:** Export chứng chỉ (Light)
- **Route:** `import_export`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 59
- **Ảnh tham chiếu:** `reference/S67-certificate-export-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Tab Chứng chỉ + Xuất CSV đang chọn.
  - Card mô tả CSV UTF-8 và số chứng chỉ theo MSSV.
  - Danh sách ánh xạ nhãn với bốn cột xuất.
  - Nút Xuất CSV và khối xem trước nội dung.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `SegmentedButton` tương thích Material Components, `MaterialCardView`, `MaterialButton`, code preview
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/export_certificate_title, @string/export_certificate_spec_format, @string/export_certificate_note, @string/certificate_csv_columns, @string/btn_export_csv, @string/csv_preview_title

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Ready to export** | Theo đúng ảnh S67 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Tệp xuất luôn chứa MSSV và đúng thứ tự bốn cột đã công bố.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S67-certificate-export-light.png`.
