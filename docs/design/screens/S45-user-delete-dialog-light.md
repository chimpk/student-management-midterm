# S45 - Người dùng - Dialog xóa (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 29  
> **Ảnh tham chiếu:** [S45](../reference/S45-user-delete-dialog-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S45`
- **Tên màn:** Người dùng - Dialog xóa (Light)
- **Route:** `user_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 29
- **Ảnh tham chiếu:** `reference/S45-user-delete-dialog-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Nền là danh sách người dùng S10.
  - `MaterialAlertDialog` hiển thị tên và username mục tiêu.
  - Mô tả hậu quả tài khoản không còn đăng nhập được và thao tác không thể hoàn tác.
  - Nút Hủy và nút Xóa mang màu lỗi.

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
- @string/dialog_delete_user_title, @string/dialog_delete_user_desc_format, @string/action_irreversible, @string/btn_cancel, @string/btn_delete

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Confirmation** | Theo đúng ảnh S45 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Không cho mở dialog xóa đối với chính tài khoản Admin đang đăng nhập.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S45-user-delete-dialog-light.png`.
