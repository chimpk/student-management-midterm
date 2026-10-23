# Đặc tả Chi tiết Hiệu ứng Chuyển động (Motion Effects Spec)

Tài liệu này đặc tả chi tiết kỹ thuật cho các hiệu ứng chuyển động được phê duyệt theo hai phân nhóm:
- **Gói A (Luôn bật):** FX-01 đến FX-04 (Tuân theo chính sách giảm chuyển động của hệ điều hành).
- **Gói B (Tùy chọn):** FX-05 đến FX-11 (Người dùng chủ động bật/tắt trong màn Cài đặt).

Toàn bộ hiệu ứng được điều phối thông qua chính sách [motion-policy.md](file:///d:/Study/mobile/MidTerm/docs/design/motion-policy.md) và hệ thống token tại [motion-tokens.md](file:///d:/Study/mobile/MidTerm/docs/design/motion-tokens.md).

---

## PHẦN 1: GÓI A - HIỆU ỨNG LUÔN BẬT (FX-01 → FX-04)

### FX-01: Skeleton Shimmer đúng bố cục Card
- **Gói:** Gói A (Luôn bật)
- **Màn hình áp dụng:** 
  - Màn hình Sinh viên: [S07](file:///d:/Study/mobile/MidTerm/docs/design/screens/S07-student-list-admin-light.md), [S12](file:///d:/Study/mobile/MidTerm/docs/design/screens/S12-student-list-employee-light.md), [S24](file:///d:/Study/mobile/MidTerm/docs/design/screens/S24-student-list-loading-shimmer-light.md), [S29](file:///d:/Study/mobile/MidTerm/docs/design/screens/S29-student-list-admin-dark.md) (`Chủ sở hữu: Thành viên 2`)
  - Màn hình Quản lý người dùng: [S10](file:///d:/Study/mobile/MidTerm/docs/design/screens/S10-user-list-admin-light.md), [S33](file:///d:/Study/mobile/MidTerm/docs/design/screens/S33-user-list-admin-dark.md) (`Chủ sở hữu: Thành viên 1`)
- **Trigger:** Khi màn hình bắt đầu nạp dữ liệu từ Local Cache hoặc Firestore (Trạng thái Loading).
- **Trạng thái chuyển dịch:** 3 khung xương card (`~72dp`, bo góc `@dimen/card_corner_radius`) hiển thị màu xám mờ; dải sáng quét chéo qua bề mặt theo chu kỳ lặp lại.
- **Hành vi khi bị ngắt giữa chừng:** Khi dữ liệu tải xong hoặc xảy ra lỗi mạng, hiệu ứng dừng ngay lập tức tại chu kỳ hiện tại, khung xương biến mất và nhường chỗ cho danh sách dữ liệu thật hoặc trạng thái Empty/Error.
- **Công cụ / API:** `ValueAnimator` điều khiển tọa độ ma trận gradient hoặc Drawable Animation chuẩn Android SDK.
- **API Level tối thiểu:** API 26 (Android 8.0).
- **Tình trạng Dependency:** Đã có sẵn trong Android SDK, **không cần dependency mới**.
- **Token tham chiếu:** `motion_shimmer_cycle` (1200 ms), `motion_easing_linear`.
- **Phương án tĩnh (Bắt buộc):** Hiển thị 3 khung xương card xám tĩnh (alpha cố định 0.4), không có dải sáng quét chuyển động.
- **Rủi ro hiệu năng / Trợ năng:** Nếu dữ liệu tải lâu, shimmer chạy liên tục có thể tiêu tốn pin; giải pháp: Giới hạn tự động dừng shimmer sau 10 giây nếu quá thời gian chờ.
- **Tiêu chuẩn nghiệm thu (Acceptance Criteria):**
  - [ ] Khi hệ thống tắt animation (`systemAnimationsEnabled == false`), hiển thị ngay phương án tĩnh không quét sáng.
  - [ ] Dừng quét sáng ngay khi bản ghi đầu tiên được render vào RecyclerView.
  - [ ] Duy trì vùng chạm an toàn, không gây crash khi người dùng nhấn Back trong lúc đang shimmer.

---

### FX-02: Shared-Element Transition (Avatar + Tên)
- **Gói:** Gói A (Luôn bật)
- **Màn hình áp dụng:** Từ danh sách sinh viên ([S07](file:///d:/Study/mobile/MidTerm/docs/design/screens/S07-student-list-admin-light.md), [S12](file:///d:/Study/mobile/MidTerm/docs/design/screens/S12-student-list-employee-light.md), [S29](file:///d:/Study/mobile/MidTerm/docs/design/screens/S29-student-list-admin-dark.md)) sang Chi tiết sinh viên ([S08](file:///d:/Study/mobile/MidTerm/docs/design/screens/S08-student-detail-admin-light.md), [S30](file:///d:/Study/mobile/MidTerm/docs/design/screens/S30-student-detail-admin-dark.md)).
- **Chủ sở hữu:** `Thành viên 2` (Ghi chú: Thành viên 1 chỉ lập đặc tả, không kéo vào phạm vi code).
- **Trigger:** Người dùng chạm vào một item sinh viên trong danh sách.
- **Trạng thái chuyển dịch:** 
  - Avatar chữ cái đầu (`44dp`) mở rộng và dịch chuyển mượt mà thành Avatar lớn (`56dp`) trên màn hình Chi tiết.
  - TextView tên sinh viên trượt và chuyển kích thước từ `16sp` sang `18sp`.
- **Hành vi khi bị ngắt giữa chừng:** Nếu người dùng nhấn nút Back ngay khi màn hình chi tiết vừa mở, hiệu ứng lập tức đảo chiều (Re-enter Transition) đưa avatar và tên trở lại đúng vị trí item trong danh sách.
- **Công cụ / API:** AndroidX Navigation Component kết hợp `FragmentNavigatorExtras` và `ChangeBounds`, `ChangeTransform`.
- **API Level tối thiểu:** API 26.
- **Tình trạng Dependency:** Đã có trong `androidx.navigation:navigation-fragment`, **không cần dependency mới**.
- **Token tham chiếu:** `motion_duration_standard` (300 ms), `motion_easing_emphasized`.
- **Phương án tĩnh (Bắt buộc):** Chuyển đổi màn hình tức thì, mở màn hình Chi tiết sinh viên trực tiếp không qua hiệu ứng phóng to avatar.
- **Rủi ro hiệu năng / Trợ năng:** Giật khung hình nếu danh sách sinh viên có hình ảnh quá nặng; giải pháp: Sử dụng placeholder bộ nhớ cache của Glide.
- **Tiêu chuẩn nghiệm thu (Acceptance Criteria):**
  - [ ] Khi tắt animation hệ thống, màn hình Chi tiết mở ra ngay lập tức với đầy đủ dữ liệu.
  - [ ] Tên và Avatar không bị biến dạng tỷ lệ khung hình (Aspect Ratio) trong suốt 300 ms chuyển động.
  - [ ] Vùng chạm nút Back trên màn Chi tiết đạt ≥48dp ngay cả trong quá trình animate.

---

### FX-03: Phản hồi xúc giác (Haptic Feedback)
- **Gói:** Gói A (Luôn bật)
- **Màn hình áp dụng:** 
  - Màn hình Đăng nhập ([S01](file:///d:/Study/mobile/MidTerm/docs/design/screens/S01-login-default-light.md), [S03](file:///d:/Study/mobile/MidTerm/docs/design/screens/S03-login-error-light.md), [S27](file:///d:/Study/mobile/MidTerm/docs/design/screens/S27-login-default-dark.md))
  - Màn hình Quản lý người dùng ([S10](file:///d:/Study/mobile/MidTerm/docs/design/screens/S10-user-list-admin-light.md), [S21](file:///d:/Study/mobile/MidTerm/docs/design/screens/S21-user-lock-dialog-light.md), [S33](file:///d:/Study/mobile/MidTerm/docs/design/screens/S33-user-list-admin-dark.md))
- **Chủ sở hữu:** `Thành viên 1`
- **Trigger:**
  - Nhập sai thông tin đăng nhập (Báo lỗi inline).
  - Đăng nhập thành công (Vào Dashboard).
  - Xác nhận Khóa / Mở khóa tài khoản người dùng thành công.
- **Trạng thái chuyển dịch:** Phát nhịp rung cảm ứng vật lý siêu ngắn (20–50 ms) qua bộ rung thiết bị.
- **Hành vi khi bị ngắt giữa chừng:** Nhịp rung diễn ra độc lập, không chặn luồng chính (Main Thread).
- **Công cụ / API:** `View.performHapticFeedback()` tiêu chuẩn Android SDK.
- **API Level tối thiểu:** API 26 (Fallback tự động giữa các mức hằng số).
- **Tình trạng Dependency:** Chuẩn Android Framework, **không cần dependency mới**.
- **Token tham chiếu:** `haptic_confirm`, `haptic_warning_reject`.
- **Phương án tĩnh (Bắt buộc):** Không phát xung rung, chỉ hiển thị thông báo trực quan trên giao diện màn hình.
- **Rủi ro hiệu năng / Trợ năng:** Người dùng tắt rung trong cài đặt hệ điều hành Android; giải pháp: Kiểm tra `Settings.System.HAPTIC_FEEDBACK_ENABLED`, tuyệt đối không ép rung.
- **Tiêu chuẩn nghiệm thu (Acceptance Criteria):**
  - [ ] Tôn trọng cài đặt hệ thống: Thiết bị tắt rung thì không phát sinh haptic.
  - [ ] Không tiết lộ thông tin bảo mật: Rung cảnh báo sai mật khẩu giống hệt nhau cho mọi nguyên nhân lỗi đăng nhập.
  - [ ] Không gây đơ giật giao diện hoặc làm trễ việc xuất hiện banner thông báo lỗi.

---

### FX-04: Nút Đăng nhập biến đổi hình thái (Morphing Button)
- **Gói:** Gói A (Luôn bật)
- **Màn hình áp dụng:** Màn hình Đăng nhập ([S01](file:///d:/Study/mobile/MidTerm/docs/design/screens/S01-login-default-light.md), [S02](file:///d:/Study/mobile/MidTerm/docs/design/screens/S02-login-loading-light.md), [S27](file:///d:/Study/mobile/MidTerm/docs/design/screens/S27-login-default-dark.md))
- **Chủ sở hữu:** `Thành viên 1`
- **Trigger:** Người dùng nhấn nút "Đăng nhập".
- **Trạng thái chuyển dịch:** 
  - Dòng chữ `@string/btn_login` mờ dần (Fade out, 100 ms).
  - Xuất hiện vòng xoay CircularProgressIndicator nhỏ gọn căn giữa nút, đồng thời khóa tương tác nút (disabled).
  - Nếu đăng nhập thành công: Vòng xoay chuyển thành icon dấu tick hoàn tất trước khi chuyển trang.
  - Nếu thất bại: Vòng xoay mờ dần, phục hồi chữ `@string/btn_login` và kích hoạt lại nút.
- **Hành vi khi bị ngắt giữa chừng:** Khi mạng trả về lỗi tức thì, nút lập tức hoàn nguyên trạng thái sẵn sàng để người dùng thử lại.
- **Công cụ / API:** `TransitionManager.beginDelayedTransition()` hoặc `AnimatedVectorDrawable`.
- **API Level tối thiểu:** API 26.
- **Tình trạng Dependency:** Đã có trong `com.google.android.material:material`, **không cần dependency mới**.
- **Token tham chiếu:** `motion_duration_medium` (250 ms), `motion_easing_standard`.
- **Phương án tĩnh (Bắt buộc):** Đổi nhãn nút bấm tức thì sang `@string/btn_logging_in` (S02), không co giãn hay biến đổi hình dạng nút.
- **Rủi ro hiệu năng / Trợ năng:** Nút bị khóa tương tác phải có thông báo cho TalkBack rằng hệ thống đang xử lý đăng nhập.
- **Tiêu chuẩn nghiệm thu (Acceptance Criteria):**
  - [ ] Khi hệ thống tắt animation, chuyển ngay sang giao diện tải tĩnh mà không chạy hiệu ứng chuyển hình thái.
  - [ ] Nút bị khóa click lặp lại trong suốt thời gian biến đổi để chống gửi request trùng lặp.
  - [ ] Giữ nguyên kích thước chiều cao chuẩn `@dimen/touch_target_min` (48dp).

---

## PHẦN 2: GÓI B - HIỆU ỨNG TÙY CHỌN BẬT/TẮT TRONG CÀI ĐẶT (FX-05 → FX-11)

### FX-05: Đếm tăng số liệu thống kê (Count-up Numbers)
- **Gói:** Gói B (Tùy chọn)
- **Màn hình áp dụng:** Dashboard Quản trị viên ([S04](file:///d:/Study/mobile/MidTerm/docs/design/screens/S04-dashboard-admin-light.md), [S28](file:///d:/Study/mobile/MidTerm/docs/design/screens/S28-dashboard-admin-dark.md))
- **Chủ sở hữu:** `Thành viên 1`
- **Trigger:** Mở màn hình Dashboard Admin khi số liệu tổng hợp (sinh viên, khoa, lớp) nạp thành công.
- **Trạng thái chuyển dịch:** Số lượng tăng dần từ 0 đến giá trị thực (ví dụ 0 -> 120 sinh viên) trong 400 ms.
- **Hành vi khi bị ngắt giữa chừng:** Người dùng chuyển tab hoặc thoát app -> Dừng ValueAnimator ngay, gán thẳng số liệu cuối cùng.
- **Công cụ / API:** `ValueAnimator.ofInt()` kết hợp cập nhật chuỗi `@string/stats_summary_format`.
- **API Level tối thiểu:** API 26.
- **Tình trạng Dependency:** Có sẵn trong Android SDK, **không cần dependency mới**.
- **Token tham chiếu:** `motion_duration_countup` (400 ms), `motion_easing_linear`.
- **Phương án tĩnh (Bắt buộc):** Hiển thị ngay con số thực tế tĩnh (ví dụ "120 sinh viên • 5 khoa • 12 lớp") mà không chạy hiệu ứng đếm số.
- **Rủi ro hiệu năng / Trợ năng:** Gây khó chịu cho người đọc màn hình TalkBack nếu đọc từng số nhảy; giải pháp: Chỉ thông báo kết quả số cuối cùng qua TalkBack sau khi kết thúc animation.
- **Tiêu chuẩn nghiệm thu (Acceptance Criteria):**
  - [ ] Tắt công tắc Cài đặt Gói B -> Số hiển thị ngay lập tức, không chạy số từ 0.
  - [ ] Thời gian chạy số không bao giờ vượt quá 400 ms.
  - [ ] TalkBack chỉ đọc con số hoàn tất một lần duy nhất.

---

### FX-06: Hiệu ứng xuất hiện so le (Staggered Cards)
- **Gói:** Gói B (Tùy chọn)
- **Màn hình áp dụng:** 
  - Danh sách Sinh viên: [S07](file:///d:/Study/mobile/MidTerm/docs/design/screens/S07-student-list-admin-light.md), [S12](file:///d:/Study/mobile/MidTerm/docs/design/screens/S12-student-list-employee-light.md), [S29](file:///d:/Study/mobile/MidTerm/docs/design/screens/S29-student-list-admin-dark.md) (`Thành viên 2`)
  - Danh sách Người dùng: [S10](file:///d:/Study/mobile/MidTerm/docs/design/screens/S10-user-list-admin-light.md), [S33](file:///d:/Study/mobile/MidTerm/docs/design/screens/S33-user-list-admin-dark.md) (`Thành viên 1`)
  - Lịch sử đăng nhập: [S11](file:///d:/Study/mobile/MidTerm/docs/design/screens/S11-login-history-user-light.md), [S34](file:///d:/Study/mobile/MidTerm/docs/design/screens/S34-login-history-user-dark.md) (`Thành viên 1`)
- **Trigger:** Danh sách nạp dữ liệu thành công lần đầu hoặc khi người dùng thay đổi bộ lọc.
- **Trạng thái chuyển dịch:** Tối đa 8 card đầu tiên trượt nhẹ từ dưới lên (`~16dp`, alpha 0 -> 1) với độ trễ 30 ms giữa mỗi card liên tiếp.
- **Hành vi khi bị ngắt giữa chừng:** Người dùng cuộn danh sách lập tức -> Animation dừng ngay, hiển thị toàn bộ các card ở trạng thái alpha 1.0 đầy đủ.
- **Công cụ / API:** `LayoutAnimationController` hoặc `RecyclerView.ItemAnimator`.
- **API Level tối thiểu:** API 26.
- **Tình trạng Dependency:** Có sẵn trong Android Framework, **không cần dependency mới**.
- **Token tham chiếu:** `motion_stagger_delay` (30 ms), `motion_stagger_max_items` (8 items), `motion_stagger_total_clamp` (~240 ms), `motion_easing_decelerate`.
- **Phương án tĩnh (Bắt buộc):** Toàn bộ các card xuất hiện đồng thời ngay lập tức, không có độ trễ trượt.
- **Rủi ro hiệu năng / Trợ năng:** Nếu danh sách quá dài gây drop fps; giải pháp: Giới hạn nghiêm ngặt chỉ chạy stagger cho tối đa 8 item đầu tiên.
- **Tiêu chuẩn nghiệm thu (Acceptance Criteria):**
  - [ ] Khi tắt công tắc Gói B, toàn bộ danh sách hiện ngay cùng lúc.
  - [ ] Cuộn danh sách không bị hiện tượng giật lag khung hình.
  - [ ] Vùng chạm của từng card khả dụng ngay cả khi card đang trượt vào.

---

### FX-07: Độ nảy đàn hồi (Spring Interaction)
- **Gói:** Gói B (Tùy chọn)
- **Màn hình áp dụng:**
  - Nút gạt Switch Cài đặt: [Sxx-settings-effects](file:///d:/Study/mobile/MidTerm/docs/design/screens/Sxx-settings-effects.md) (`Thành viên 1`)
  - Các nút Chip bộ lọc: [S07](file:///d:/Study/mobile/MidTerm/docs/design/screens/S07-student-list-admin-light.md), [S12](file:///d:/Study/mobile/MidTerm/docs/design/screens/S12-student-list-employee-light.md) (`Thành viên 2`)
  - Nút Khóa / Mở khóa tài khoản: [S10](file:///d:/Study/mobile/MidTerm/docs/design/screens/S10-user-list-admin-light.md), [S33](file:///d:/Study/mobile/MidTerm/docs/design/screens/S33-user-list-admin-dark.md) (`Thành viên 1`)
- **Trigger:** Người dùng chạm vào Chip bộ lọc, gạt Switch hoặc bấm nút Khóa.
- **Trạng thái chuyển dịch:** Nảy nhẹ tỷ lệ kích thước (`scale 1.0 -> 0.95 -> 1.05 -> 1.0`) tạo cảm giác vật lý sống động theo Material Components.
- **Hành vi khi bị ngắt giữa chừng:** Chạm liên tục -> Reset chu kỳ nảy, đưa về scale 1.0 an toàn.
- **Công cụ / API:** 
  - `androidx.dynamicanimation:dynamicanimation` (`CẦN DUYỆT DEPENDENCY`).
  - **Phương án thay thế không cần dependency:** Sử dụng `ObjectAnimator` kết hợp `OvershootInterpolator(tension = 1.2f)` có sẵn trong SDK chuẩn.
- **API Level tối thiểu:** API 26.
- **Tình trạng Dependency:** `CẦN DUYỆT DEPENDENCY` nếu dùng SpringAnimation native; khuyến nghị dùng phương án thay thế `OvershootInterpolator` để không tăng dung lượng app.
- **Token tham chiếu:** `motion_spring_subtle`, `motion_spring_bouncy`.
- **Phương án tĩnh (Bắt buộc):** Chuyển đổi trạng thái màu sắc và icon tức thì, không có hiệu ứng nảy co giãn kích thước.
- **Rủi ro hiệu năng / Trợ năng:** Phóng to thu nhỏ làm lệch vùng chạm nếu không kiểm soát tốt; giải pháp: Giới hạn biên độ co giãn không quá 5%.
- **Tiêu chuẩn nghiệm thu (Acceptance Criteria):**
  - [ ] Tắt công tắc Gói B -> Chuyển trạng thái tức thì không có độ nảy.
  - [ ] Kích thước vùng chạm thực tế luôn duy trì tối thiểu 48dp trong suốt quá trình nảy.

---

### FX-08: Container Transform (FAB mở rộng thành Form)
- **Gói:** Gói B (Tùy chọn)
- **Màn hình áp dụng:** Từ Danh sách sinh viên ([S07](file:///d:/Study/mobile/MidTerm/docs/design/screens/S07-student-list-admin-light.md), [S29](file:///d:/Study/mobile/MidTerm/docs/design/screens/S29-student-list-admin-dark.md)) mở Form Thêm sinh viên ([S05](file:///d:/Study/mobile/MidTerm/docs/design/screens/S05-student-add-default-light.md), [S31](file:///d:/Study/mobile/MidTerm/docs/design/screens/S31-student-add-default-dark.md)).
- **Chủ sở hữu:** `Thành viên 2` (Ghi chú: Thành viên 1 chỉ lập đặc tả).
- **Trigger:** Người dùng nhấn nút Floating Action Button (FAB "+ Thêm sinh viên").
- **Trạng thái chuyển dịch:** Nút FAB tròn (`56dp`) nở rộng mượt mà về 4 cạnh biến thành khung Form thêm sinh viên toàn màn hình trong 350 ms.
- **Hành vi khi bị ngắt giữa chừng:** Nhấn nút Hủy hoặc Back trong lúc mở -> Đảo ngược hiệu ứng (Form co lại thành nút FAB).
- **Công cụ / API:** `com.google.android.material.transition.MaterialContainerTransform`.
- **API Level tối thiểu:** API 26.
- **Tình trạng Dependency:** Đã có trong `com.google.android.material:material:1.11.0`, **không cần dependency mới**.
- **Token tham chiếu:** `motion_duration_long` (350 ms), `motion_easing_emphasized`.
- **Phương án tĩnh (Bắt buộc):** Mở Form thêm mới trực tiếp bằng thao tác thay thế Fragment tức thì, không có chuyển động mở rộng container.
- **Rủi ro hiệu năng / Trợ năng:** Quá trình nở rộng có thể làm chớp nháy bàn phím ảo (Soft Keyboard); giải pháp: Chỉ mở bàn phím ảo sau khi hiệu ứng transform kết thúc hoàn toàn.
- **Tiêu chuẩn nghiệm thu (Acceptance Criteria):**
  - [ ] Khi tắt công tắc Gói B, form thêm sinh viên hiển thị ngay không qua hiệu ứng mở rộng container.
  - [ ] Form không bị tràn viền hoặc méo góc trong lúc biến đổi.
  - [ ] Bàn phím ảo không tự động bật lên làm gián đoạn chuyển cảnh.

---

### FX-09: Chuyển tab hòa trộn mờ (Fade-through Bottom Navigation)
- **Gói:** Gói B (Tùy chọn)
- **Màn hình áp dụng:** Điều hướng giữa 3 tab đáy: Trang chủ ([S04](file:///d:/Study/mobile/MidTerm/docs/design/screens/S04-dashboard-admin-light.md) / [S09](file:///d:/Study/mobile/MidTerm/docs/design/screens/S09-dashboard-employee-light.md)), Sinh viên ([S07](file:///d:/Study/mobile/MidTerm/docs/design/screens/S07-student-list-admin-light.md) / [S12](file:///d:/Study/mobile/MidTerm/docs/design/screens/S12-student-list-employee-light.md)), Hồ sơ ([S17](file:///d:/Study/mobile/MidTerm/docs/design/screens/S17-profile-admin-default-light.md)).
- **Chủ sở hữu:** `Thành viên 1` & `Thành viên 2`
- **Trigger:** Người dùng chọn một tab khác trên `BottomNavigationView`.
- **Trạng thái chuyển dịch:** Màn hình cũ mờ dần (Fade out, 100 ms) kèm thu nhỏ cực nhẹ (scale 1.0 -> 0.96); màn hình mới hiện dần lên (Fade in, 150 ms, scale 0.96 -> 1.0). Tổng thời gian 250 ms.
- **Hành vi khi bị ngắt giữa chừng:** Nhấn chuyển tab liên tục -> Bỏ qua các tab trung gian, chuyển thẳng tới tab cuối cùng được chọn.
- **Công cụ / API:** `MaterialFadeThrough` (Material Components) tích hợp Fragment Transition.
- **API Level tối thiểu:** API 26.
- **Tình trạng Dependency:** Đã có trong `com.google.android.material:material`, **không cần dependency mới**.
- **Token tham chiếu:** `motion_duration_medium` (250 ms), `motion_easing_standard`.
- **Phương án tĩnh (Bắt buộc):** Đổi Fragment hiển thị tức thì không qua hiệu ứng mờ dần hay thay đổi tỷ lệ.
- **Rủi ro hiệu năng / Trợ năng:** Làm chậm cảm giác chuyển tab nếu thời lượng quá dài; giải pháp: Giữ chặt chẽ thời lượng tổng ở mức 250 ms.
- **Tiêu chuẩn nghiệm thu (Acceptance Criteria):**
  - [ ] Tắt công tắc Gói B -> Chuyển tab hiển thị ngay tức khắc.
  - [ ] Không có hiện tượng hai màn hình đè lên nhau gây rối mắt.

---

### FX-10: Chuyển động Thêm/Xóa Item & Snackbar Hoàn tác
- **Gói:** Gói B (Tùy chọn)
- **Màn hình áp dụng:** 
  - Danh sách Sinh viên: [S07](file:///d:/Study/mobile/MidTerm/docs/design/screens/S07-student-list-admin-light.md), [S14](file:///d:/Study/mobile/MidTerm/docs/design/screens/S14-student-delete-undo-snackbar-light.md), [S29](file:///d:/Study/mobile/MidTerm/docs/design/screens/S29-student-list-admin-dark.md) (`Thành viên 2`)
  - Danh sách Người dùng: [S10](file:///d:/Study/mobile/MidTerm/docs/design/screens/S10-user-list-admin-light.md), [S33](file:///d:/Study/mobile/MidTerm/docs/design/screens/S33-user-list-admin-dark.md) (`Thành viên 1`)
- **Trigger:** Xóa sinh viên hoặc cập nhật trạng thái khóa tài khoản; kích hoạt thanh Snackbar Hoàn tác.
- **Trạng thái chuyển dịch:** Item bị xóa trượt sang ngang và thu hẹp chiều cao về 0dp (250 ms); các item phía dưới trượt lên lấp khoảng trống. Snackbar trượt êm từ đáy lên trên Navigation Bar.
- **Hành vi khi bị ngắt giữa chừng:** Người dùng nhấn nút "Hoàn tác" trên Snackbar -> Quá trình xóa bị hủy, item bung trở lại đúng vị trí cũ trong danh sách.
- **Công cụ / API:** `DefaultItemAnimator` của RecyclerView và `Snackbar` (Material Components).
- **API Level tối thiểu:** API 26.
- **Tình trạng Dependency:** Đã có sẵn trong thư viện hiện tại, **không cần dependency mới**.
- **Token tham chiếu:** `motion_duration_medium` (250 ms), `motion_easing_standard`.
- **Phương án tĩnh (Bắt buộc):** Item biến mất khỏi RecyclerView tức thì không trượt; Snackbar xuất hiện ngay lập tức không có hiệu ứng trượt từ đáy.
- **Rủi ro hiệu năng / Trợ năng:** Nhấn Hoàn tác liên tục có thể gây lỗi vị trí Adapter (IndexOutOfBoundsException); giải pháp: Sử dụng `DiffUtil` hoặc quản lý chỉ số index cẩn trọng.
- **Tiêu chuẩn nghiệm thu (Acceptance Criteria):**
  - [ ] Tắt công tắc Gói B -> Item biến mất ngay, Snackbar hiện tĩnh.
  - [ ] Nhấn Hoàn tác khôi phục bản ghi sinh viên đúng vị trí cũ mà không gây crash app.
  - [ ] Nút Hoàn tác trên Snackbar đảm bảo vùng chạm tối thiểu 48dp.

---

### FX-11: Avatar Cross-fade & Vòng tiến độ tải ảnh mượt mà
- **Gói:** Gói B (Tùy chọn)
- **Màn hình áp dụng:** Hồ sơ cá nhân ([S17](file:///d:/Study/mobile/MidTerm/docs/design/screens/S17-profile-admin-default-light.md), [S22](file:///d:/Study/mobile/MidTerm/docs/design/screens/S22-profile-avatar-upload-progress-light.md), [S35](file:///d:/Study/mobile/MidTerm/docs/design/screens/S35-profile-admin-dark.md))
- **Chủ sở hữu:** `Thành viên 1`
- **Trigger:** Người dùng chọn ảnh mới và tải ảnh đại diện lên Firebase Storage.
- **Trạng thái chuyển dịch:** Thanh tiến độ `LinearProgressIndicator` chạy mượt mà từ 0% đến 100% kèm nhãn `@string/uploading_avatar_format`; khi tải xong, ảnh đại diện mới chuyển mờ dần (Cross-fade, 250 ms) thay thế ảnh cũ.
- **Hành vi khi bị ngắt giữa chừng:** Mạng lỗi -> Dừng thanh tiến độ, giữ nguyên avatar cũ và báo lỗi, không chạy hiệu ứng đổi ảnh.
- **Công cụ / API:** Glide transition `DrawableTransitionOptions.withCrossFade(250)` kết hợp `LinearProgressIndicator`.
- **API Level tối thiểu:** API 26.
- **Tình trạng Dependency:** Đã có trong `com.github.bumptech.glide:glide:4.16.0`, **không cần dependency mới**.
- **Token tham chiếu:** `motion_duration_medium` (250 ms), `motion_easing_standard`.
- **Phương án tĩnh (Bắt buộc):** Thanh tiến độ nhảy bước thô; khi ảnh tải xong thì gán thẳng ảnh mới vào ImageView mà không qua hiệu ứng hòa trộn mờ dần.
- **Rủi ro hiệu năng / Trợ năng:** Giật lag khi render ảnh dung lượng quá lớn; giải pháp: Glide tự động nén và resize ảnh đại diện về kích thước phù hợp (`80dp x 80dp`).
- **Tiêu chuẩn nghiệm thu (Acceptance Criteria):**
  - [ ] Tắt công tắc Gói B -> Gán ảnh mới ngay khi tải xong mà không chạy hiệu ứng cross-fade.
  - [ ] Không làm lộ khung hình trống (Black frame) trong suốt 250 ms chuyển ảnh.

---

## PHẦN 3: BẢNG TỔNG HỢP HIỆU ỨNG (SUMMARY MATRIX)

| ID | Tên hiệu ứng | Phân gói | Màn hình áp dụng | Chi phí triển khai | Tình trạng Dependency | Chủ sở hữu |
|:---|:---|:---:|:---|:---:|:---:|:---:|
| **FX-01** | Skeleton Shimmer bố cục Card | **Gói A** (Luôn bật) | Sinh viên (S07, S12, S24, S29), Người dùng (S10, S33) | Thấp | Có sẵn SDK | Thành viên 1 & 2 |
| **FX-02** | Shared-element Avatar + Tên | **Gói A** (Luôn bật) | Sinh viên -> Chi tiết (S07/S12/S29 -> S08/S30) | Trung bình | Có sẵn Navigation | **Thành viên 2** |
| **FX-03** | Phản hồi xúc giác Haptic | **Gói A** (Luôn bật) | Login (S01, S03), Người dùng (S10, S21) | Rất thấp | Có sẵn SDK | **Thành viên 1** |
| **FX-04** | Nút Login biến đổi hình thái | **Gói A** (Luôn bật) | Login (S01, S02, S27) | Trung bình | Có sẵn Material | **Thành viên 1** |
| **FX-05** | Đếm tăng số liệu thống kê | **Gói B** (Tùy chọn) | Dashboard Admin (S04, S28) | Thấp | Có sẵn SDK | **Thành viên 1** |
| **FX-06** | Stagger card vào lần lượt | **Gói B** (Tùy chọn) | Danh sách Sinh viên, Người dùng, Lịch sử | Thấp | Có sẵn Framework | Thành viên 1 & 2 |
| **FX-07** | Độ nảy đàn hồi Spring | **Gói B** (Tùy chọn) | Switch Cài đặt, Chip bộ lọc, Nút Khóa | Thấp - TB | `CẦN DUYỆT DEPENDENCY` (hoặc dùng Overshoot) | Thành viên 1 & 2 |
| **FX-08** | Container Transform (FAB -> Form)| **Gói B** (Tùy chọn) | Danh sách SV -> Form thêm (S07 -> S05) | Cao | Có sẵn Material | **Thành viên 2** |
| **FX-09** | Chuyển tab hòa trộn mờ | **Gói B** (Tùy chọn) | Điều hướng BottomNavigationView (Home/SV/Profile) | Thấp | Có sẵn Material | Thành viên 1 & 2 |
| **FX-10** | Chuyển động Thêm/Xóa & Snackbar | **Gói B** (Tùy chọn) | Danh sách Sinh viên, Người dùng | Trung bình | Có sẵn RecyclerView | Thành viên 1 & 2 |
| **FX-11** | Avatar Cross-fade & Vòng tiến độ | **Gói B** (Tùy chọn) | Hồ sơ cá nhân (S17, S22, S35) | Thấp | Có sẵn Glide | **Thành viên 1** |

---

## PHẦN 4: DANH MỤC HIỆU ỨNG KHÔNG ÁP DỤNG

Các hiệu ứng sau đây **tuyệt đối không áp dụng** trong hệ thống thiết kế nhằm bảo vệ hiệu năng máy, thời lượng pin và trải nghiệm nghiệp vụ:

1. **Predictive Back (Cử chỉ xem trước khi Back):** Đã loại bỏ theo yêu cầu của USER; ứng dụng sử dụng điều hướng Back tiêu chuẩn của Fragment / AndroidX Activity.
2. **Hiệu ứng làm mờ thời gian thực (Real-time Blur / Glassmorphism):** Gây sụt giảm khung hình nghiêm trọng trên các dòng máy Android phân khúc phổ thông; không phù hợp ứng dụng quản lý hành chính.
3. **Hiệu ứng thị sai cuộn (Parallax Scrolling):** Gây mất tập trung khi tra cứu danh sách nhiều dòng và không có trong tài liệu bàn giao PDF.
4. **Hiệu ứng hạt bay / Pháo hoa mừng (Particles / Confetti):** Làm quá tải tài nguyên đồ họa không cần thiết cho ứng dụng dữ liệu học đường.
5. **Hiệu ứng nhấp nháy / Thở lặp vô tận (Infinite Pulsing Glow):** Gây nguy cơ co giật thị giác (vi phạm tiêu chuẩn WCAG A11y) và gây mất tập trung.
6. **Toàn bộ Gói C (Chuyển động nâng cao / Phức tạp):** Không nằm trong phạm vi lựa chọn của dự án.

---

## PHẦN 5: THỨ TỰ TRIỂN KHAI ĐỀ XUẤT (IMPLEMENTATION ORDER)

Khi bắt đầu triển khai code (trong các task tương lai, tuân thủ đúng phân công vai trò):

1. **Giai đoạn 1 (Nền tảng chính sách & Cài đặt - Thành viên 1):**
   - Viết lớp logic `MotionPolicy` thuần Java/Kotlin và bộ Unit Test JVM tương ứng.
   - Xây dựng kho lưu trữ cục bộ (SharedPreferences) cho công tắc "Hiệu ứng nâng cao".
   - Tích hợp khối công tắc Cài đặt [Sxx-settings-effects](file:///d:/Study/mobile/MidTerm/docs/design/screens/Sxx-settings-effects.md) vào màn Hồ sơ cá nhân.
2. **Giai đoạn 2 (Gói A - Hiệu ứng thiết yếu):**
   - Triển khai Haptic Feedback (`FX-03`) và Nút Morphing (`FX-04`) trên màn Login (Thành viên 1).
   - Triển khai Skeleton Shimmer (`FX-01`) trên các danh sách (Thành viên 1 & 2).
   - Thành viên 2 triển khai Shared-element Transition (`FX-02`) trên luồng Sinh viên.
3. **Giai đoạn 3 (Gói B - Tùy chọn nâng cao):**
   - Triển khai Count-up Dashboard (`FX-05`) và Fade-through Tab (`FX-09`).
   - Triển khai Avatar Cross-fade (`FX-11`) trên màn Hồ sơ cá nhân.
   - Triển khai Stagger (`FX-06`) và Chuyển động Thêm/Xóa Item (`FX-10`).
   - Triển khai Spring nảy nhẹ (`FX-07`) bằng phương án thay thế `OvershootInterpolator`.
   - Thành viên 2 triển khai Container Transform FAB (`FX-08`) khi mở form thêm sinh viên.
