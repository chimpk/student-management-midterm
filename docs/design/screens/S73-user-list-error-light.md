# S73 - Người dùng - Lỗi tải dữ liệu (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 67  
> **Ảnh tham chiếu:** [S73](../reference/S73-user-list-error-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S73`
- **Tên màn:** Người dùng - Lỗi tải dữ liệu (Light)
- **Route:** `user_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 67
- **Ảnh tham chiếu:** `reference/S73-user-list-error-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Toolbar và mô tả quyền Admin.
  - Error state giữa màn hình với nút Thử lại.
  - Không render danh sách stale nếu không gắn nhãn dữ liệu lưu.

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
- @string/user_error_title, @string/user_error_desc, @string/btn_retry

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Error** | Theo đúng ảnh S73 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Nút Thử lại phát lại truy vấn và có trạng thái loading chống nhấn lặp.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S73-user-list-error-light.png`.
