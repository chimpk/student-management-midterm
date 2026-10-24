# S54 - Chứng chỉ - Lỗi tải dữ liệu (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 40  
> **Ảnh tham chiếu:** [S54](../reference/S54-certificate-list-error-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S54`
- **Tên màn:** Chứng chỉ - Lỗi tải dữ liệu (Light)
- **Route:** `certificate_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 40
- **Ảnh tham chiếu:** `reference/S54-certificate-list-error-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giữ toolbar và card sinh viên.
  - Error state có icon, tiêu đề, mô tả kết nối gián đoạn và nút Thử lại.
  - Không hiện dữ liệu giả thay cho lỗi.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`, error illustration, `MaterialButton`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/certificate_error_title, @string/certificate_error_desc, @string/btn_retry

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Error** | Theo đúng ảnh S54 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Thử lại giữ nguyên sinh viên đang xem và phát lại request tải danh sách.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S54-certificate-list-error-light.png`.
