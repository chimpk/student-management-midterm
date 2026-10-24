# S57 - Import chứng chỉ - Đang xử lý (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 45  
> **Ảnh tham chiếu:** [S57](../reference/S57-certificate-import-processing-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S57`
- **Tên màn:** Import chứng chỉ - Đang xử lý (Light)
- **Route:** `import_export`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 45
- **Ảnh tham chiếu:** `reference/S57-certificate-import-processing-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giữ lựa chọn Chứng chỉ/Nhập CSV.
  - Card tiến trình hiển thị tệp, số dòng đã xử lý và phần trăm.
  - `LinearProgressIndicator` determinate.
  - Nút xử lý disabled và không cho chọn/gửi lại tệp.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `LinearProgressIndicator`, `MaterialCardView`, `MaterialButton` disabled
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/certificate_import_processing, @string/import_progress_format, @string/import_wait_note, @string/btn_processing, @string/import_no_resubmit

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Processing** | Theo đúng ảnh S57 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Tiến độ tăng đơn điệu; tránh import trùng do nhấn lặp hoặc xoay màn hình.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S57-certificate-import-processing-light.png`.
