# S49 - Chứng chỉ - Sửa đổi (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 35  
> **Ảnh tham chiếu:** [S49](../reference/S49-certificate-edit-default-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S49`
- **Tên màn:** Chứng chỉ - Sửa đổi (Light)
- **Route:** `certificate_edit`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 35
- **Ảnh tham chiếu:** `reference/S49-certificate-edit-default-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Cấu trúc form S48 với dữ liệu chứng chỉ hiện tại điền sẵn.
  - MSSV/sinh viên liên kết chỉ đọc.
  - Ngày cấp mở Material Date Picker và bị chặn nếu ở tương lai.
  - Nút Lưu ghim đáy.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `TextInputLayout`, `MaterialDatePicker`, `MaterialButton`, `MaterialCardView`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/title_edit_certificate, @string/label_certificate_name, @string/label_issue_date, @string/label_issuer, @string/btn_save

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Default** | Theo đúng ảnh S49 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Update đúng chứng chỉ con, không tạo bản ghi mới hoặc đổi chủ sở hữu.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S49-certificate-edit-default-light.png`.
