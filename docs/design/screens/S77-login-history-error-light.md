# S77 - Lịch sử đăng nhập - Lỗi tải (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 71  
> **Ảnh tham chiếu:** [S77](../reference/S77-login-history-error-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S77`
- **Tên màn:** Lịch sử đăng nhập - Lỗi tải (Light)
- **Route:** `login_history_user`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 71
- **Ảnh tham chiếu:** `reference/S77-login-history-error-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Toolbar và card tài khoản đích.
  - Error state mô tả kết nối gián đoạn, có CTA Thử lại.
  - Footer múi giờ GMT+7.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- error illustration, `MaterialButton`, `MaterialCardView`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/login_history_error_title, @string/login_history_error_desc, @string/btn_retry, @string/vietnam_timezone_note

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Error** | Theo đúng ảnh S77 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Retry không đổi tài khoản mục tiêu và không nhân đôi bản ghi.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S77-login-history-error-light.png`.
