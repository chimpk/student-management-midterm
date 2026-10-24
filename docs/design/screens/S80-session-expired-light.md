# S80 - Phiên đăng nhập hết hạn (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 76  
> **Ảnh tham chiếu:** [S80](../reference/S80-session-expired-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S80`
- **Tên màn:** Phiên đăng nhập hết hạn (Light)
- **Route:** `session_expired`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 76
- **Ảnh tham chiếu:** `reference/S80-session-expired-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Màn trạng thái toàn trang có logo UniData.
  - Icon phiên hết hạn, tiêu đề và mô tả ngắn.
  - Nút chính Quay lại đăng nhập.
  - Không có bottom navigation hoặc nội dung bảo vệ phía sau.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- state illustration, `MaterialButton`, brand slot
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/session_expired_title, @string/session_expired_desc, @string/btn_back_to_login, @string/session_reauth_note

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Terminal auth state** | Theo đúng ảnh S80 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Xóa phiên cục bộ an toàn và điều hướng login; Back không quay lại màn đã bảo vệ.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S80-session-expired-light.png`.
