# S39 - Dashboard - Employee (Dark)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 14  
> **Ảnh tham chiếu:** [S39](../reference/S39-dashboard-employee-dark.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S39`
- **Tên màn:** Dashboard - Employee (Dark)
- **Route:** `dashboard_employee`
- **Chế độ hiển thị:** Dark
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 14
- **Ảnh tham chiếu:** `reference/S39-dashboard-employee-dark.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- `CoordinatorLayout` toàn màn hình, dùng bộ token Dark.
  - `MaterialToolbar`: lời chào, avatar, vai trò Employee và trạng thái đồng bộ.
  - `NestedScrollView`.
    - Card Sinh viên và card Hồ sơ; không có thẻ Người dùng hoặc Import/Export.
    - Banner `@string/employee_dashboard_title` mô tả quyền tra cứu chỉ đọc.
  - `BottomNavigationView`: Trang chủ, Sinh viên, Hồ sơ.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialToolbar`, `MaterialCardView`, `Chip`, `BottomNavigationView`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Dark lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/greeting_format, @string/role_label_employee, @string/card_student_title, @string/card_profile_title, @string/employee_dashboard_title, @string/employee_dashboard_desc

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Ready** | Theo đúng ảnh S39 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Không render chức năng ghi dữ liệu hoặc quản trị cho Employee.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S39-dashboard-employee-dark.png`.
