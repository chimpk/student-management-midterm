# S81 - Tài khoản bị khóa (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 77  
> **Ảnh tham chiếu:** [S81](../reference/S81-account-locked-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S81`
- **Tên màn:** Tài khoản bị khóa (Light)
- **Route:** `account_locked`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 77
- **Ảnh tham chiếu:** `reference/S81-account-locked-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Màn trạng thái toàn trang có logo UniData.
  - Icon khóa, tiêu đề và hướng dẫn liên hệ quản trị viên.
  - Nút chính Quay lại đăng nhập.
  - Không có nội dung ứng dụng hoặc action phục hồi quyền tại client.

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
- @string/account_locked_title, @string/account_locked_desc, @string/btn_back_to_login, @string/account_access_unavailable

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Terminal auth state** | Theo đúng ảnh S81 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Chặn mọi route bảo vệ; không cung cấp tự mở khóa hoặc tự tạo tài khoản.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S81-account-locked-light.png`.
