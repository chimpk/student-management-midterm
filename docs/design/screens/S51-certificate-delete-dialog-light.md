# S51 - Chứng chỉ - Dialog xóa (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 37  
> **Ảnh tham chiếu:** [S51](../reference/S51-certificate-delete-dialog-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S51`
- **Tên màn:** Chứng chỉ - Dialog xóa (Light)
- **Route:** `certificate_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 37
- **Ảnh tham chiếu:** `reference/S51-certificate-delete-dialog-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Nền là danh sách chứng chỉ S46.
  - Dialog nêu tên chứng chỉ, sinh viên và MSSV.
  - Cảnh báo chỉ chứng chỉ này bị xóa và thao tác không thể hoàn tác.
  - Nút Hủy và Xóa.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialAlertDialog`, `MaterialButton`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/dialog_delete_certificate_title, @string/dialog_delete_certificate_desc_format, @string/btn_cancel, @string/btn_delete

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Confirmation** | Theo đúng ảnh S51 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Xóa đúng document chứng chỉ con; không xóa sinh viên hoặc chứng chỉ khác.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S51-certificate-delete-dialog-light.png`.
