# S38 - Dashboard - Manager (Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 13  
> **Ảnh tham chiếu:** [S38](../reference/S38-dashboard-manager-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S38`
- **Tên màn:** Dashboard - Manager (Dark)
- **Route:** `dashboard_manager`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 13
- **Ảnh tham chiếu:** `reference/S38-dashboard-manager-dark.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` toàn màn hình, nền `@color/color_surface`.
  - `MaterialToolbar`: thương hiệu, lời chào, avatar, chip `@string/role_label_manager` và trạng thái đồng bộ.
  - `NestedScrollView` chứa khối `@string/space_mgmt_title`.
    - Card Sinh viên: số lượng và lối vào danh sách.
    - Card Import/Export: nhập và xuất dữ liệu.
    - Card Hồ sơ: thông tin và bảo mật cá nhân.
    - Banner thống kê dữ liệu sẵn sàng và thời điểm cập nhật.
  - `BottomNavigationView`: Trang chủ, Sinh viên, Hồ sơ.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`, `MaterialCardView`, `Chip`, `BottomNavigationView`, `NestedScrollView`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Dark lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/greeting_format, @string/role_label_manager, @string/card_student_title, @string/card_io_title, @string/card_profile_title, @string/stats_ready_banner

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Ready** | Theo đúng ảnh S38 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Không hiển thị thẻ quản lý người dùng; Manager chỉ vào phân hệ sinh viên, Import/Export và hồ sơ.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S38-dashboard-manager-dark.png`.
