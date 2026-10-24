# S74 - Người dùng - Ngoại tuyến (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 68  
> **Ảnh tham chiếu:** [S74](../reference/S74-user-list-offline-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S74`
- **Tên màn:** Người dùng - Ngoại tuyến (Light)
- **Route:** `user_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 68
- **Ảnh tham chiếu:** `reference/S74-user-list-offline-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Banner ngoại tuyến nêu thời điểm lưu gần nhất.
  - Danh sách cache vẫn hiển thị ở chế độ chỉ đọc.
  - Mỗi card ghi rõ cần mạng để chỉnh sửa; action Add/Edit/Lock/Unlock/Delete bị khóa.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialBanner`, `MaterialCardView`, `Chip`, `RecyclerView`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/user_offline_banner, @string/cached_at_format, @string/offline_read_only_note

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Offline cached** | Theo đúng ảnh S74 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Không cho ghi khi ngoại tuyến; dữ liệu cache và thời điểm lưu phải rõ ràng.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S74-user-list-offline-light.png`.
