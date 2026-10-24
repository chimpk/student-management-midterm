# S55 - Import chứng chỉ - Mặc định (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 43  
> **Ảnh tham chiếu:** [S55](../reference/S55-certificate-import-default-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S55`
- **Tên màn:** Import chứng chỉ - Mặc định (Light)
- **Route:** `import_export`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 43
- **Ảnh tham chiếu:** `reference/S55-certificate-import-default-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Màn Import/Export có bộ chọn loại dữ liệu, tab Nhập CSV/Xuất CSV.
  - Tab Chứng chỉ + Nhập CSV đang chọn.
  - Card tệp hiển thị tên, dung lượng, Bắt đầu nhập và Chọn tệp khác.
  - Card cấu trúc cột: `mssv,ten_chung_chi,ngay_cap,don_vi_cap`; CTA tải mẫu.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `SegmentedButton` tương thích Material Components, `MaterialCardView`, `MaterialButton`, `Chip`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/title_import_export, @string/data_type_certificate, @string/import_certificate_title, @string/btn_start_import, @string/btn_choose_other_file, @string/certificate_csv_columns, @string/btn_download_csv_template

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **File selected** | Theo đúng ảnh S55 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Chỉ nhận CSV UTF-8 tối đa 5 MB; MSSV phải tồn tại; ngày cấp không ở tương lai.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S55-certificate-import-default-light.png`.
