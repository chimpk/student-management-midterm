# S79 - Hồ sơ - Lỗi và dữ liệu lưu (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 75  
> **Ảnh tham chiếu:** [S79](../reference/S79-profile-error-cached-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S79`
- **Tên màn:** Hồ sơ - Lỗi và dữ liệu lưu (Light)
- **Route:** `profile`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 75
- **Ảnh tham chiếu:** `reference/S79-profile-error-cached-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Banner lỗi cho biết không thể cập nhật và đang hiển thị dữ liệu đã lưu.
  - Nút Thử lại nằm trong banner.
  - Thông tin tài khoản và lịch sử cache vẫn đọc được, có thời điểm lưu.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialBanner`, `MaterialCardView`, `MaterialButton`, `BottomNavigationView`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/profile_cached_error_title, @string/profile_cached_error_desc, @string/btn_retry, @string/cached_at_format

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Error with cache** | Theo đúng ảnh S79 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Không gắn nhãn dữ liệu cache là dữ liệu trực tuyến; retry không làm mất nội dung đang xem.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S79-profile-error-cached-light.png`.
