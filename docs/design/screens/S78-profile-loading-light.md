# S78 - Hồ sơ - Đang cập nhật (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 72  
> **Ảnh tham chiếu:** [S78](../reference/S78-profile-loading-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S78`
- **Tên màn:** Hồ sơ - Đang cập nhật (Light)
- **Route:** `profile`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 72
- **Ảnh tham chiếu:** `reference/S78-profile-loading-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Header hồ sơ và dữ liệu cache hiển thị ngay.
  - Banner ghi thời điểm dữ liệu được lưu.
  - Khu lịch sử cá nhân hiển thị trạng thái đang cập nhật.
  - Các action nhạy cảm chỉ bật khi trạng thái online được xác nhận.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`, `MaterialBanner`, `CircularProgressIndicator`, `BottomNavigationView`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/title_profile, @string/cached_at_format, @string/profile_history_updating, @string/btn_change_password

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Cached + refreshing** | Theo đúng ảnh S78 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Không che toàn bộ hồ sơ nếu đã có cache; phân biệt rõ dữ liệu lưu và dữ liệu mới.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S78-profile-loading-light.png`.
