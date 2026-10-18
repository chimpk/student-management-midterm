# Sxx - Cài đặt Hiệu ứng Chuyển động (ĐỀ XUẤT BỔ SUNG)

> [!NOTE]
> **Nhãn:** `ĐỀ XUẤT BỔ SUNG (không có trong PDF)`  
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** Đề xuất kỹ thuật bổ sung để quản lý công tắc bật/tắt Gói B (PDF thiết kế gốc chưa có màn Cài đặt)  
> **Ảnh tham chiếu:** `Không có ảnh PDF` (Không tự vẽ ảnh giả)

---

## 1. Định danh & Điều hướng
- **ID:** `Sxx-settings-effects` (hoặc tích hợp vào `S17-profile-admin-default-light` / `S35-profile-admin-dark`)
- **Tên màn:** Cài đặt Hiệu ứng Chuyển động (Motion Settings)
- **Route:** `TODO(design)` (Đề xuất `settings_effects` hoặc lồng trong `profile`)
- **Chế độ hiển thị:** Light & Dark (Kế thừa toàn bộ hệ thống token màu tương ứng)
- **Nguồn thiết kế:** Đề xuất bổ sung theo yêu cầu kỹ thuật quản trị Gói B
- **Ảnh tham chiếu:** `Không có ảnh PDF`
- **Phạm vi sở hữu:** Thành viên 1

> [!IMPORTANT]
> **Phương án bố trí giao diện (USER cần quyết định):**
> - **Phương án 1 (Khuyến nghị):** Tích hợp trực tiếp thành một khối (Section Card) "Hiệu ứng & Chuyển động" nằm trong màn hình **Hồ sơ cá nhân** ([S17](file:///d:/Study/mobile/MidTerm/docs/design/screens/S17-profile-admin-default-light.md) / [S35](file:///d:/Study/mobile/MidTerm/docs/design/screens/S35-profile-admin-dark.md)), đặt ngay dưới khối "Thông tin tài khoản". Ưu điểm: Tiết kiệm số lượng màn hình, không phải tạo thêm route điều hướng mới, người dùng dễ tìm thấy.
> - **Phương án 2:** Tạo màn hình con riêng biệt "Cài đặt ứng dụng" mở ra từ biểu tượng bánh răng cài đặt trên AppBar màn Hồ sơ. Ưu điểm: Phù hợp nếu tương lai mở rộng thêm nhiều cài đặt khác (ngôn ngữ, thông báo, chủ đề giao diện).

---

## 2. Cây cấu trúc giao diện (Layout Tree)

- `MaterialCardView` (nền `@color/bg_card`, bo góc `@dimen/card_corner_radius`, padding `@dimen/screen_margin`, viền `@dimen/card_stroke_width` `@color/color_outline_variant`)
  - `LinearLayout` (vertical)
    - Tiêu đề nhóm: `TextView` (15sp/500 `@color/text_primary`, copy `@string/settings_effects_group_title`)
    - `Space` (`~12dp`)
    - Dòng công tắc hiệu ứng:
      - `LinearLayout` (horizontal, căn giữa dọc, minHeight `@dimen/touch_target_min`)
        - `LinearLayout` (vertical, trọng số 1, marginEnd `~16dp`)
          - `TextView` Tiêu đề công tắc (15sp/500 `@color/text_primary`, copy `@string/settings_effects_switch_title`)
          - `TextView` Mô tả tính năng (13sp `@color/text_secondary`, copy `@string/settings_effects_switch_desc`)
        - `MaterialSwitch` (checked=true mặc định, minWidth `@dimen/touch_target_min`, minHeight `@dimen/touch_target_min`, tint `@color/color_primary`)
    - Dòng chú thích khi bị hệ thống vô hiệu hóa:
      - `LinearLayout` (horizontal, căn giữa dọc, marginStart `~4dp`, marginTop `~8dp`, visibility có điều kiện)
        - `ImageView` (`~16dp`, icon `ic_info`, tint `@color/text_secondary`)
        - `TextView` (12sp `@color/text_secondary`, copy `@string/settings_effects_disabled_system_note`, marginStart `~8dp`)

---

## 3. Thành phần giao diện (Components & Material Catalog)

- `MaterialCardView`: Khung bao quanh khối cài đặt
- `MaterialSwitch`: Công tắc trượt chuẩn Material 3
- `TextView`: Nhãn tiêu đề, mô tả và ghi chú hệ thống
- Icons: `ic_info` (Material Symbols `info`)

---

## 4. Hệ thống Token thiết kế (Design Tokens)

- Màu sắc:
  - Nền khối: `@color/bg_card`
  - Viền khối: `@color/color_outline_variant`
  - Văn bản chính: `@color/text_primary`
  - Văn bản phụ & ghi chú: `@color/text_secondary`
  - Màu công tắc khi bật: `@color/color_primary`, `@color/color_on_primary`
  - Màu công tắc khi vô hiệu: `@color/text_disabled`
- Kích thước:
  - Lề khối: `@dimen/screen_margin`
  - Bo góc khối: `@dimen/card_corner_radius`
  - Vùng chạm tối thiểu: `@dimen/touch_target_min` (48dp)

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)

- `@string/settings_title`
- `@string/settings_effects_group_title`
- `@string/settings_effects_switch_title`
- `@string/settings_effects_switch_desc`
- `@string/settings_effects_disabled_system_note`
- `@string/settings_effects_status_on`
- `@string/settings_effects_status_off`
- `@string/settings_effects_status_disabled`

---

## 6. Ma trận trạng thái (UI States)

| Trạng thái giao diện | Vị trí công tắc | Trạng thái tương tác | Trạng thái ghi chú hệ thống |
|:---|:---:|:---:|:---|
| **Bật (Enabled - ON)** | Nút gạt bên phải (Checked) | Cho phép gạt tự do | Ẩn (`View.GONE`) |
| **Tắt (Enabled - OFF)**| Nút gạt bên trái (Unchecked)| Cho phép gạt tự do | Ẩn (`View.GONE`) |
| **Vô hiệu hóa do hệ thống (Disabled by System)** | Khóa theo trạng thái gần nhất | Không cho phép gạt (enabled=false) | Hiển thị (`View.VISIBLE`) kèm `@string/settings_effects_disabled_system_note` |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)

- [ ] **Đảm bảo vùng chạm chuẩn Accessibility:** Toàn bộ khu vực dòng công tắc có chiều cao tối thiểu 48dp (`@dimen/touch_target_min`).
- [ ] **Hỗ trợ TalkBack chi tiết:** Trình đọc màn hình đọc rõ tên công tắc kèm trạng thái văn bản tương ứng (`@string/settings_effects_status_on`, `@string/settings_effects_status_off`, hoặc `@string/settings_effects_status_disabled`).
- [ ] **Vô hiệu hóa an toàn:** Khi hệ thống Android bật chế độ "Giảm hiệu ứng động" (`ANIMATOR_DURATION_SCALE == 0`), công tắc tự động mờ đi (disabled) và hiển thị dòng chú thích giải thích rõ lý do bằng tiếng Việt.
- [ ] **Lưu trữ cục bộ tức thì:** Trạng thái gạt được lưu ngay vào bộ nhớ cục bộ thiết bị (không gửi mạng lên Firestore, không phụ thuộc vai trò người dùng) và có hiệu lực ngay lập tức cho các màn hình.
