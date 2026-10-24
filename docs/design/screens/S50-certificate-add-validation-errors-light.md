# S50 - Chứng chỉ - Lỗi ngày cấp (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 36  
> **Ảnh tham chiếu:** [S50](../reference/S50-certificate-add-validation-errors-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S50`
- **Tên màn:** Chứng chỉ - Lỗi ngày cấp (Light)
- **Route:** `certificate_add`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 36
- **Ảnh tham chiếu:** `reference/S50-certificate-add-validation-errors-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Form thêm chứng chỉ giữ nguyên dữ liệu đã nhập.
  - Trường ngày cấp có viền lỗi và helper `@string/error_issue_date_future`.
  - Nút Lưu disabled cho tới khi ngày hợp lệ.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `TextInputLayout` error state, `MaterialDatePicker`, `MaterialButton` disabled
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/title_add_certificate, @string/error_issue_date_future, @string/btn_save

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Validation error** | Theo đúng ảnh S50 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Ngày cấp phải nhỏ hơn hoặc bằng ngày hiện tại theo múi giờ thiết bị.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S50-certificate-add-validation-errors-light.png`.
