# Hệ thống Tokens chuyển động (Motion Tokens)

Tài liệu này chuẩn hóa toàn bộ các thông số thời lượng (Duration), đường cong gia tốc (Easing), độ nảy đàn hồi (Spring), độ trễ so le (Stagger) và rung phản hồi xúc giác (Haptic) dùng chung trong toàn bộ ứng dụng.

---

## 1. Tokens thời lượng (Motion Duration Tokens)

Tuân thủ nguyên tắc chuyển động an toàn của Android Material 3: thời lượng vi mô 150–400 ms, không làm chậm thao tác người dùng, tự động đưa về 0 ms khi hệ thống bật chế độ "Giảm hiệu ứng động" (Reduce Motion).

| Token | Giá trị đề xuất | Mục đích sử dụng | Ghi chú & Giới hạn |
|:---|:---:|:---|:---|
| `motion_duration_micro` | `100 ms` | Phản hồi vi mô nút bấm, icon toggle, highlight | Phản hồi tức thì |
| `motion_duration_short` | `150 ms` | Biến đổi trạng thái nhỏ: Checkbox, Chip, Switch | Tránh trễ cảm giác chạm |
| `motion_duration_medium`| `250 ms` | Fade-through chuyển tab đáy, biến đổi hình dạng nút | Mượt mà, rõ nét |
| `motion_duration_standard` | `300 ms` | Shared-element transition (Avatar + Tên), Slide item | Điểm dừng thị giác tối ưu |
| `motion_duration_long` | `350 ms` | Container transform (FAB mở ra form thêm SV) | Chuyển cảnh phức tạp |
| `motion_duration_countup` | `400 ms` | Đếm tăng số liệu thống kê Dashboard (Home) | Tối đa 400 ms để không gây sốt ruột |
| `motion_shimmer_cycle` | `1200 ms` | Một chu kỳ quét sáng Shimmer khung xương | Dừng ngay khi dữ liệu về |

---

## 2. Tokens đường cong gia tốc (Easing & Interpolator Tokens)

Sử dụng các đường cong nội suy chuẩn của Android Material Components:

| Token | Tên chuẩn Material / Android | Cubic Bézier | Ứng dụng |
|:---|:---|:---:|:---|
| `motion_easing_standard` | `FastOutSlowInInterpolator` | `(0.4, 0.0, 0.2, 1.0)` | Các thành phần di chuyển bắt đầu và kết thúc trên màn hình |
| `motion_easing_emphasized` | `PathInterpolator(0.2, 0.0, 0.0, 1.0)` | `(0.2, 0.0, 0.0, 1.0)` | Container transform, Shared-element, mở Dialog |
| `motion_easing_decelerate` | `LinearOutSlowInInterpolator` | `(0.0, 0.0, 0.2, 1.0)` | Thành phần xuất hiện từ ngoài vào trong màn hình |
| `motion_easing_accelerate` | `FastOutLinearInInterpolator` | `(0.4, 0.0, 1.0, 1.0)` | Thành phần ẩn hoặc trượt ra khỏi màn hình |
| `motion_easing_linear` | `LinearInterpolator` | `(0.0, 0.0, 1.0, 1.0)` | Hiệu ứng quét Shimmer và đếm số tăng dần |

---

## 3. Tokens độ nảy đàn hồi (Spring Tokens) & Phương án thay thế

> [!WARNING]
> **Tình trạng Dependency hiện tại:** Dự án chưa khai báo thư viện `androidx.dynamicanimation:dynamicanimation`.
> - Nhãn: `CẦN DUYỆT DEPENDENCY` nếu muốn dùng Spring physics native (`SpringAnimation`, `SpringForce`).
> - **Phương án thay thế khả dụng ngay:** Sử dụng `OvershootInterpolator` có sẵn trong Android SDK tiêu chuẩn mà không cần bổ sung bất kỳ thư viện nào.

| Token | Thông số Spring (`SpringForce`) | Phương án thay thế SDK có sẵn | Áp dụng |
|:---|:---|:---|:---|
| `motion_spring_subtle` | `stiffness = 1500f, dampingRatio = 0.75f` | `OvershootInterpolator(tension = 1.2f)` | Nảy nhẹ cho Switch Cài đặt, Filter Chip |
| `motion_spring_bouncy` | `stiffness = 1000f, dampingRatio = 0.65f` | `OvershootInterpolator(tension = 1.5f)` | Nút FAB, Nút Khóa / Mở khóa tài khoản |

---

## 4. Tokens xuất hiện so le (Stagger Tokens)

Áp dụng cho việc nạp các danh sách card (Sinh viên, Người dùng, Lịch sử):

| Token | Giá trị | Quy tắc |
|:---|:---:|:---|
| `motion_stagger_delay` | `30 ms / item` | Trễ thời gian bắt đầu trượt vào giữa 2 card kế tiếp |
| `motion_stagger_max_items` | `8 items` | Chỉ áp dụng so le cho tối đa 8 item nhìn thấy đầu tiên |
| `motion_stagger_total_clamp` | `~240 ms` | Tổng thời gian trễ của cả danh sách không vượt quá 240 ms |
| `motion_stagger_slide_offset`| `~16 dp` | Khoảng cách trượt nhẹ từ dưới lên (kết hợp alpha 0 -> 1) |

---

## 5. Tokens phản hồi xúc giác (Haptic Feedback Tokens)

Chỉ kích hoạt thông qua `View.performHapticFeedback()` tiêu chuẩn hệ thống Android, tôn trọng tuyệt đối cài đặt rung/âm bàn phím của người dùng:

| Token | Hằng số Android SDK | Ngữ cảnh kích hoạt | Cảm giác người dùng |
|:---|:---|:---|:---|
| `haptic_confirm` | `HapticFeedbackConstants.CONFIRM` (API 30+) hoặc `VIRTUAL_KEY` | Đăng nhập thành công, mở khóa tài khoản | Nhịp gõ nhẹ xác nhận |
| `haptic_warning_reject`| `HapticFeedbackConstants.REJECT` (API 30+) hoặc `LONG_PRESS` | Sai mật khẩu đăng nhập, khóa tài khoản | Nhịp rung kép cảnh báo lỗi |
| `haptic_toggle` | `HapticFeedbackConstants.CLOCK_TICK` (API 21+) | Gạt Switch Cài đặt hiệu ứng, chọn Chip | Khấc chạm cơ học nhẹ |

---

## 6. Quy tắc an toàn & Giảm chuyển động (Accessibility First)

1. **Tuân thủ cài đặt hệ thống (`Settings.Global.ANIMATOR_DURATION_SCALE`):** Khi giá trị scale bằng 0 (chế độ "Remove animations" / "Giảm hiệu ứng động" đang bật), toàn bộ các token duration chuyển tức thì về `0 ms`, bỏ qua toàn bộ animation và hiển thị ngay trạng thái cuối cùng.
2. **Tần số an toàn thị giác:** Tuyệt đối không sử dụng hiệu ứng nhấp nháy, lóa sáng có chu kỳ vượt quá 3 lần / giây (bảo vệ người dùng nhạy cảm ánh sáng và chứng động kinh quang cảm ứng).
3. **Không làm chậm tác vụ quan trọng:** Mọi hiệu ứng xác nhận kết quả nghiệp vụ (lỗi form, đăng nhập thất bại) phải hiển thị thông điệp văn bản ngay lập tức, không để animation làm trễ việc người dùng tiếp nhận thông tin bảo mật.
