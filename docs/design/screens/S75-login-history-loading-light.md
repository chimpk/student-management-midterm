# S75 - Lịch sử đăng nhập - Đang tải (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 69  
> **Ảnh tham chiếu:** [S75](../reference/S75-login-history-loading-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S75`
- **Tên màn:** Lịch sử đăng nhập - Đang tải (Light)
- **Route:** `login_history_user`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 69
- **Ảnh tham chiếu:** `reference/S75-login-history-loading-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Toolbar và card tài khoản đích được giữ.
  - Danh sách lịch sử thay bằng skeleton/loading indicator.
  - Footer ghi múi giờ Việt Nam GMT+7.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`, `CircularProgressIndicator` hoặc shimmer, `RecyclerView` placeholder
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/title_login_history, @string/login_history_loading, @string/vietnam_timezone_note

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Loading** | Theo đúng ảnh S75 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Giữ đúng người dùng đang xem trong suốt quá trình tải.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S75-login-history-loading-light.png`.
