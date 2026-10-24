# S52 - Chứng chỉ - Đang tải (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 38  
> **Ảnh tham chiếu:** [S52](../reference/S52-certificate-list-loading-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S52`
- **Tên màn:** Chứng chỉ - Đang tải (Light)
- **Route:** `certificate_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 38
- **Ảnh tham chiếu:** `reference/S52-certificate-list-loading-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giữ toolbar và card sinh viên để duy trì ngữ cảnh.
  - Khu vực danh sách thay bằng các khối skeleton/shimmer.
  - Nhãn `@string/certificate_loading` ở giữa; khóa các action ghi.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`, `ShimmerFrameLayout`, `RecyclerView` placeholder
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/title_certificates, @string/certificate_loading

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Loading** | Theo đúng ảnh S52 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Không hiển thị đồng thời dữ liệu cũ và skeleton gây nhầm trạng thái.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S52-certificate-list-loading-light.png`.
