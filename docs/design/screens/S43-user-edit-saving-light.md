# S43 - Người dùng - Đang lưu (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 27  
> **Ảnh tham chiếu:** [S43](../reference/S43-user-edit-saving-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S43`
- **Tên màn:** Người dùng - Đang lưu (Light)
- **Route:** `user_edit`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 27
- **Ảnh tham chiếu:** `reference/S43-user-edit-saving-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Form sửa người dùng giữ nguyên dữ liệu đã nhập.
  - Vùng trạng thái trên footer hiển thị tiến trình lưu.
  - Nút `@string/btn_saving` disabled để chặn gửi lặp.
  - Không cho thay đổi trường trong lúc request đang chạy.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `TextInputLayout`, dropdown Material, `CircularProgressIndicator`, `MaterialButton` disabled
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/title_edit_user, @string/user_saving_note, @string/btn_saving

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Saving** | Theo đúng ảnh S43 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Chỉ gửi một request; thất bại phải phục hồi form và giữ dữ liệu.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S43-user-edit-saving-light.png`.
