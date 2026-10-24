# S63 - Import chứng chỉ - Tệp quá lớn (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 53  
> **Ảnh tham chiếu:** [S63](../reference/S63-certificate-import-too-large-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S63`
- **Tên màn:** Import chứng chỉ - Tệp quá lớn (Light)
- **Route:** `import_export`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 53
- **Ảnh tham chiếu:** `reference/S63-certificate-import-too-large-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Card tệp hiển thị dung lượng thực tế.
  - Error card nêu giới hạn 5 MB và cách giảm/chia nhỏ tệp.
  - CTA Chọn tệp khác; action import bị ẩn hoặc disabled.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView` error state, `MaterialButton`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/import_too_large_title, @string/import_too_large_desc_format, @string/btn_choose_other_file

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **File too large** | Theo đúng ảnh S63 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Chặn trước khi đọc/parse tệp vượt 5 MB.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S63-certificate-import-too-large-light.png`.
