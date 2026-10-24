# S59 - Import chứng chỉ - Kết quả (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 47  
> **Ảnh tham chiếu:** [S59](../reference/S59-certificate-import-result-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S59`
- **Tên màn:** Import chứng chỉ - Kết quả (Light)
- **Route:** `import_export`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 47
- **Ảnh tham chiếu:** `reference/S59-certificate-import-result-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Card kết quả hiển thị tổng dòng, thành công và lỗi.
  - Bảng chi tiết có số dòng CSV, MSSV, tên chứng chỉ và kết quả/lý do.
  - Dòng lỗi phân biệt bằng màu/icon nhưng không chỉ dựa vào màu.
  - CTA Chia sẻ báo cáo lỗi và Chọn tệp khác.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`, `RecyclerView`/table rows, `Chip`, `MaterialButton`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/certificate_import_result, @string/import_total, @string/import_success, @string/import_failed, @string/btn_share_error_report, @string/btn_choose_other_file

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Result** | Theo đúng ảnh S59 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Tổng thành công + lỗi bằng tổng dòng dữ liệu; số dòng bắt đầu từ 2 vì dòng 1 là header.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S59-certificate-import-result-light.png`.
