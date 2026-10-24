# S70 - Import chứng chỉ - Lỗi máy chủ (Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 62  
> **Ảnh tham chiếu:** [S70](../reference/S70-certificate-import-server-error-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S70`
- **Tên màn:** Import chứng chỉ - Lỗi máy chủ (Dark)
- **Route:** `import_export`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 62
- **Ảnh tham chiếu:** `reference/S70-certificate-import-server-error-dark.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Card tệp đã chọn vẫn được giữ.
  - Error card mô tả kết nối máy chủ gián đoạn.
  - Nút Thử lại thực hiện lại với chính tệp hiện tại; không bắt chọn lại.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView` error state, `MaterialButton`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Dark lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/import_server_error_title, @string/import_server_error_desc, @string/btn_retry

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Server error** | Theo đúng ảnh S70 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Không xóa lựa chọn tệp; retry phải chống gửi trùng.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S70-certificate-import-server-error-dark.png`.
