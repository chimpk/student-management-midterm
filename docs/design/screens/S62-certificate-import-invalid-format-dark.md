# S62 - Import chứng chỉ - Sai định dạng (Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 52  
> **Ảnh tham chiếu:** [S62](../reference/S62-certificate-import-invalid-format-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S62`
- **Tên màn:** Import chứng chỉ - Sai định dạng (Dark)
- **Route:** `import_export`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 52
- **Ảnh tham chiếu:** `reference/S62-certificate-import-invalid-format-dark.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giữ card tệp đã chọn và hướng dẫn cấu trúc CSV.
  - Error card nêu tệp `.xlsx` không hỗ trợ.
  - CTA duy nhất là Chọn tệp khác; không cho bắt đầu import.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView` error state, `MaterialButton`, `Chip`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Dark lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/import_invalid_format_title, @string/import_invalid_format_desc, @string/btn_choose_other_file, @string/certificate_csv_columns

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Invalid format** | Theo đúng ảnh S62 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Kiểm tra MIME/đuôi tệp và vẫn giữ hướng dẫn sửa lỗi trên màn hình.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S62-certificate-import-invalid-format-dark.png`.
