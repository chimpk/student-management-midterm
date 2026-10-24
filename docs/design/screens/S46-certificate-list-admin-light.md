# S46 - Chứng chỉ - Danh sách Admin (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 30  
> **Ảnh tham chiếu:** [S46](../reference/S46-certificate-list-admin-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S46`
- **Tên màn:** Chứng chỉ - Danh sách Admin (Light)
- **Route:** `certificate_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 30
- **Ảnh tham chiếu:** `reference/S46-certificate-list-admin-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `MaterialToolbar` tiêu đề Chứng chỉ và chip Admin.
  - Card tóm tắt sinh viên: avatar, họ tên, MSSV, lớp và khoa.
  - Header danh sách hiển thị tổng số và nút Thêm chứng chỉ.
  - Mỗi card chứng chỉ hiển thị tên, kết quả demo nếu có, đơn vị/ngày cấp và nút Sửa/Xóa.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`, `MaterialCardView`, `Chip`, `MaterialButton`, `RecyclerView`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/title_certificates, @string/btn_add_certificate, @string/menu_action_edit, @string/menu_action_delete

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Ready editable** | Theo đúng ảnh S46 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Admin và Manager có quyền CRUD; trường kết quả chỉ là dữ liệu demo nếu schema chưa hỗ trợ.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S46-certificate-list-admin-light.png`.
