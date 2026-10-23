# Tài liệu Thiết kế & Hướng dẫn Kỹ thuật Giao diện (Design Specs)

Tài liệu này tổng hợp toàn bộ đặc tả giao diện (UI/UX specs), hệ thống tokens thiết kế, quy ước đặt tên và ma trận trạng thái được trích xuất trực tiếp từ tài liệu PDF thiết kế bàn giao (`docs/Untitled.pdf`).

---

## 1. Hướng dẫn & Thứ tự đọc tài liệu

Để tiếp cận và triển khai giao diện chuẩn xác nhất, khuyến nghị đọc các tài liệu theo thứ tự sau:

1. **[README.md](file:///d:/Study/mobile/MidTerm/docs/design/README.md):** Tổng quan, quy ước đặt tên, bảng mục lục 36 màn hình và danh sách quyết định thiết kế `TODO(design)`.
2. **[branding.md](file:///d:/Study/mobile/MidTerm/docs/design/branding.md):** Nhận diện thương hiệu tạm thời (Placeholder), vùng slot logo/tên và danh mục kiểm tra khi thay đổi nhận diện thương hiệu chính thức.
3. **[tokens/](file:///d:/Study/mobile/MidTerm/docs/design/tokens/):**
   - [colors.xml](file:///d:/Study/mobile/MidTerm/docs/design/tokens/colors.xml) & [colors-night.xml](file:///d:/Study/mobile/MidTerm/docs/design/tokens/colors-night.xml): Hệ thống token màu sắc hai chế độ Sáng / Tối.
   - [dimens.xml](file:///d:/Study/mobile/MidTerm/docs/design/tokens/dimens.xml): Hệ thống kích thước, khoảng cách lưới 8dp, bo góc và vùng chạm tối thiểu 48dp.
   - [typography.md](file:///d:/Study/mobile/MidTerm/docs/design/tokens/typography.md): Quy chuẩn phông chữ Roboto, cấp độ văn bản và tỷ lệ tương phản WCAG.
4. **[strings-vi.xml](file:///d:/Study/mobile/MidTerm/docs/design/strings-vi.xml):** Toàn bộ bản sao văn bản (Copy tiếng Việt) giữ nguyên dấu và văn phong thiết kế.
5. **[fixtures.json](file:///d:/Study/mobile/MidTerm/docs/design/fixtures.json):** Dữ liệu mẫu (mock data) đối soát hiển thị.
6. **[states/state-matrix.md](file:///d:/Study/mobile/MidTerm/docs/design/states/state-matrix.md):** Ma trận trạng thái (Loading, Shimmer, Error, Empty, Offline, Dialogs, Dark Mode).
7. **[rbac-visibility.md](file:///d:/Study/mobile/MidTerm/docs/design/rbac-visibility.md):** Bảng ma trận phân quyền hiển thị theo vai trò (Admin, Manager, Employee).
8. **[a11y.md](file:///d:/Study/mobile/MidTerm/docs/design/a11y.md):** Hướng dẫn trợ năng (Accessibility: TalkBack, `contentDescription`, `fontScale`, Touch Targets).
9. **[gap-analysis.md](file:///d:/Study/mobile/MidTerm/docs/design/gap-analysis.md):** Bảng đối soát giữa mockup PDF và hiện trạng mã nguồn / schema hệ thống.
10. **[screens/](file:///d:/Study/mobile/MidTerm/docs/design/screens/):** Chi tiết đặc tả 36 màn hình (S01 đến S36) theo cấu trúc 7 mục quy chuẩn cùng màn đề xuất bổ sung [Sxx-settings-effects](file:///d:/Study/mobile/MidTerm/docs/design/screens/Sxx-settings-effects.md).
11. **[motion-tokens.md](file:///d:/Study/mobile/MidTerm/docs/design/motion-tokens.md):** Hệ thống tokens thời lượng, easing, spring, stagger và haptic dùng chung.
12. **[motion-policy.md](file:///d:/Study/mobile/MidTerm/docs/design/motion-policy.md):** Chính sách điều phối chuyển động duy nhất (MotionPolicy, logic thuần túy, 8 tổ hợp chân trị).
13. **[motion-effects.md](file:///d:/Study/mobile/MidTerm/docs/design/motion-effects.md):** Đặc tả chi tiết các hiệu ứng chuyển động Gói A (luôn bật) và Gói B (tùy chọn Cài đặt).
14. **[reference/](file:///d:/Study/mobile/MidTerm/docs/design/reference/):** Kho ảnh chụp rasterize độ phân giải cao 2x từ file PDF bàn giao để đối chiếu layout trực quan.

---

## 2. Quy ước đặt tên & Mã hóa

- **Mã định danh màn hình (Screen ID):** Định dạng `Sxx-[tên-chức-năng]-[trạng-thái]-[theme]`, ví dụ:
  - `S01-login-default-light.md`
  - `S27-login-default-dark.md`
- **Ảnh tham chiếu (Reference Images):** Đặt tên tương ứng `reference/Sxx-*.png`.
- **Hệ thống Token:**
  - Token màu: `@color/color_primary`, `@color/color_surface`, `@color/bg_card`, `@color/bg_offline`, v.v. Không ghi mã màu hex trong file đặc tả màn hình.
  - Token kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`, `@dimen/touch_target_min`, v.v. Các kích thước ước lượng từ hình ảnh thực tế được đánh dấu tiền tố `~`.
- **Bản sao văn bản:** Mọi chuỗi giao diện đều tham chiếu khóa XML trong `strings-vi.xml` theo chuẩn `snake_case`.
- **Nhãn UI ↔ Giá trị dữ liệu:**
  - Trạng thái người dùng: `Hoạt động` ↔ `Normal`, `Đã khóa` ↔ `Locked`.
  - Vai trò: `Admin` ↔ `admin`, `Manager` ↔ `manager`, `Employee` ↔ `employee`.

---

## 3. Bảng danh mục toàn bộ màn hình thiết kế (36 màn)

| ID | Tên màn hình | Trang PDF | Phân nhóm | Chủ sở hữu | Ảnh tham chiếu | File đặc tả |
|:---|:---|:---:|:---|:---:|:---|:---|
| **S01** | Đăng nhập - Mặc định (Light) | Trang 1 | Xác thực | Thành viên 1 | `reference/S01-login-default-light.png` | [S01](file:///d:/Study/mobile/MidTerm/docs/design/screens/S01-login-default-light.md) |
| **S02** | Đăng nhập - Đang xử lý (Light) | Trang 2 | Xác thực | Thành viên 1 | `reference/S02-login-loading-light.png` | [S02](file:///d:/Study/mobile/MidTerm/docs/design/screens/S02-login-loading-light.md) |
| **S03** | Đăng nhập - Lỗi xác thực (Light) | Trang 3 | Xác thực | Thành viên 1 | `reference/S03-login-error-light.png` | [S03](file:///d:/Study/mobile/MidTerm/docs/design/screens/S03-login-error-light.md) |
| **S04** | Dashboard - Admin (Light) | Trang 4 | Điều hướng chính | Thành viên 1 | `reference/S04-dashboard-admin-light.png` | [S04](file:///d:/Study/mobile/MidTerm/docs/design/screens/S04-dashboard-admin-light.md) |
| **S05** | Sinh viên - Thêm mới (Light) | Trang 5 | Quản lý sinh viên | Thành viên 2 | `reference/S05-student-add-default-light.png` | [S05](file:///d:/Study/mobile/MidTerm/docs/design/screens/S05-student-add-default-light.md) |
| **S06** | Sinh viên - Sửa đổi (Light) | Trang 6 | Quản lý sinh viên | Thành viên 2 | `reference/S06-student-edit-default-light.png` | [S06](file:///d:/Study/mobile/MidTerm/docs/design/screens/S06-student-edit-default-light.md) |
| **S07** | Sinh viên - Danh sách Admin (Light) | Trang 7 | Quản lý sinh viên | Thành viên 2 | `reference/S07-student-list-admin-light.png` | [S07](file:///d:/Study/mobile/MidTerm/docs/design/screens/S07-student-list-admin-light.md) |
| **S08** | Sinh viên - Chi tiết Admin (Light) | Trang 8 | Quản lý sinh viên | Thành viên 2 | `reference/S08-student-detail-admin-light.png` | [S08](file:///d:/Study/mobile/MidTerm/docs/design/screens/S08-student-detail-admin-light.md) |
| **S09** | Dashboard - Employee (Light) | Trang 9 | Điều hướng chính | Thành viên 1 | `reference/S09-dashboard-employee-light.png` | [S09](file:///d:/Study/mobile/MidTerm/docs/design/screens/S09-dashboard-employee-light.md) |
| **S10** | Quản lý người dùng - Admin (Light) | Trang 10 | Quản trị người dùng | Thành viên 1 | `reference/S10-user-list-admin-light.png` | [S10](file:///d:/Study/mobile/MidTerm/docs/design/screens/S10-user-list-admin-light.md) |
| **S11** | Lịch sử đăng nhập - Admin xem (Light) | Trang 11 | Quản trị người dùng | Thành viên 1 | `reference/S11-login-history-user-light.png` | [S11](file:///d:/Study/mobile/MidTerm/docs/design/screens/S11-login-history-user-light.md) |
| **S12** | Sinh viên - Danh sách chỉ đọc (Light) | Trang 12 | Tra cứu sinh viên | Thành viên 2 | `reference/S12-student-list-employee-light.png` | [S12](file:///d:/Study/mobile/MidTerm/docs/design/screens/S12-student-list-employee-light.md) |
| **S13** | Sinh viên - Sắp xếp BottomSheet (Light) | Trang 13 | Quản lý sinh viên | Thành viên 2 | `reference/S13-student-sort-bottomsheet-light.png` | [S13](file:///d:/Study/mobile/MidTerm/docs/design/screens/S13-student-sort-bottomsheet-light.md) |
| **S14** | Sinh viên - Hoàn tác xóa Snackbar (Light) | Trang 14 | Quản lý sinh viên | Thành viên 2 | `reference/S14-student-delete-undo-snackbar-light.png` | [S14](file:///d:/Study/mobile/MidTerm/docs/design/screens/S14-student-delete-undo-snackbar-light.md) |
| **S15** | Sinh viên - Menu chi tiết (Light) | Trang 15 | Quản lý sinh viên | Thành viên 2 | `reference/S15-student-detail-action-menu-light.png` | [S15](file:///d:/Study/mobile/MidTerm/docs/design/screens/S15-student-detail-action-menu-light.md) |
| **S16** | Sinh viên - Dialog xóa phân tầng (Light) | Trang 16 | Quản lý sinh viên | Thành viên 2 | `reference/S16-student-delete-dialog-cascade-light.png` | [S16](file:///d:/Study/mobile/MidTerm/docs/design/screens/S16-student-delete-dialog-cascade-light.md) |
| **S17** | Hồ sơ cá nhân - Admin (Light) | Trang 17 | Hồ sơ & Bảo mật | Thành viên 1 | `reference/S17-profile-admin-default-light.png` | [S17](file:///d:/Study/mobile/MidTerm/docs/design/screens/S17-profile-admin-default-light.md) |
| **S18** | Import / Export CSV - Mặc định (Light) | Trang 18 | Dữ liệu CSV | Thành viên 2 | `reference/S18-import-export-default-light.png` | [S18](file:///d:/Study/mobile/MidTerm/docs/design/screens/S18-import-export-default-light.md) |
| **S19** | Sinh viên - DatePicker Dialog (Light) | Trang 19 | Quản lý sinh viên | Thành viên 2 | `reference/S19-student-add-datepicker-dialog-light.png` | [S19](file:///d:/Study/mobile/MidTerm/docs/design/screens/S19-student-add-datepicker-dialog-light.md) |
| **S20** | Sinh viên - Lỗi form Validation (Light) | Trang 20 | Quản lý sinh viên | Thành viên 2 | `reference/S20-student-add-validation-errors-light.png` | [S20](file:///d:/Study/mobile/MidTerm/docs/design/screens/S20-student-add-validation-errors-light.md) |
| **S21** | Quản lý người dùng - Dialog khóa (Light) | Trang 21 | Quản trị người dùng | Thành viên 1 | `reference/S21-user-lock-dialog-light.png` | [S21](file:///d:/Study/mobile/MidTerm/docs/design/screens/S21-user-lock-dialog-light.md) |
| **S22** | Hồ sơ - Tải ảnh đại diện 65% (Light) | Trang 22 | Hồ sơ & Bảo mật | Thành viên 1 | `reference/S22-profile-avatar-upload-progress-light.png` | [S22](file:///d:/Study/mobile/MidTerm/docs/design/screens/S22-profile-avatar-upload-progress-light.md) |
| **S23** | Import/Export - Kết quả tóm tắt (Light) | Trang 23 | Dữ liệu CSV | Thành viên 2 | `reference/S23-import-export-result-summary-light.png` | [S23](file:///d:/Study/mobile/MidTerm/docs/design/screens/S23-import-export-result-summary-light.md) |
| **S24** | Sinh viên - Shimmer Loading (Light) | Trang 24 | Quản lý sinh viên | Thành viên 2 | `reference/S24-student-list-loading-shimmer-light.png` | [S24](file:///d:/Study/mobile/MidTerm/docs/design/screens/S24-student-list-loading-shimmer-light.md) |
| **S25** | Sinh viên - Danh sách trống Empty (Light) | Trang 25 | Quản lý sinh viên | Thành viên 2 | `reference/S25-student-list-empty-state-light.png` | [S25](file:///d:/Study/mobile/MidTerm/docs/design/screens/S25-student-list-empty-state-light.md) |
| **S26** | Sinh viên - Banner ngoại tuyến (Light) | Trang 26 | Ngoại tuyến | Thành viên 2 | `reference/S26-student-list-offline-banner-light.png` | [S26](file:///d:/Study/mobile/MidTerm/docs/design/screens/S26-student-list-offline-banner-light.md) |
| **S27** | Đăng nhập - Chế độ tối (Dark) | Trang 27 | Xác thực | Thành viên 1 | `reference/S27-login-default-dark.png` | [S27](file:///d:/Study/mobile/MidTerm/docs/design/screens/S27-login-default-dark.png) |
| **S28** | Dashboard - Admin Chế độ tối (Dark) | Trang 28 | Điều hướng chính | Thành viên 1 | `reference/S28-dashboard-admin-dark.png` | [S28](file:///d:/Study/mobile/MidTerm/docs/design/screens/S28-dashboard-admin-dark.md) |
| **S29** | Sinh viên - Danh sách Admin Dark | Trang 29 | Quản lý sinh viên | Thành viên 2 | `reference/S29-student-list-admin-dark.png` | [S29](file:///d:/Study/mobile/MidTerm/docs/design/screens/S29-student-list-admin-dark.md) |
| **S30** | Sinh viên - Chi tiết Admin Dark | Trang 30 | Quản lý sinh viên | Thành viên 2 | `reference/S30-student-detail-admin-dark.png` | [S30](file:///d:/Study/mobile/MidTerm/docs/design/screens/S30-student-detail-admin-dark.md) |
| **S31** | Sinh viên - Thêm mới Dark | Trang 31 | Quản lý sinh viên | Thành viên 2 | `reference/S31-student-add-default-dark.png` | [S31](file:///d:/Study/mobile/MidTerm/docs/design/screens/S31-student-add-default-dark.md) |
| **S32** | Sinh viên - Sửa đổi Dark | Trang 32 | Quản lý sinh viên | Thành viên 2 | `reference/S32-student-edit-default-dark.png` | [S32](file:///d:/Study/mobile/MidTerm/docs/design/screens/S32-student-edit-default-dark.md) |
| **S33** | Quản lý người dùng - Admin Dark | Trang 33 | Quản trị người dùng | Thành viên 1 | `reference/S33-user-list-admin-dark.png` | [S33](file:///d:/Study/mobile/MidTerm/docs/design/screens/S33-user-list-admin-dark.md) |
| **S34** | Lịch sử đăng nhập - Dark | Trang 34 | Quản trị người dùng | Thành viên 1 | `reference/S34-login-history-user-dark.png` | [S34](file:///d:/Study/mobile/MidTerm/docs/design/screens/S34-login-history-user-dark.md) |
| **S35** | Hồ sơ cá nhân - Admin Dark | Trang 35 | Hồ sơ & Bảo mật | Thành viên 1 | `reference/S35-profile-admin-dark.png` | [S35](file:///d:/Study/mobile/MidTerm/docs/design/screens/S35-profile-admin-dark.md) |
| **S36** | Import / Export CSV - Dark | Trang 36 | Dữ liệu CSV | Thành viên 2 | `reference/S36-import-export-default-dark.png` | [S36](file:///d:/Study/mobile/MidTerm/docs/design/screens/S36-import-export-default-dark.md) |
| **Sxx** | Cài đặt hiệu ứng (Đề xuất) | Bổ sung | Cài đặt & Trợ năng | Thành viên 1 | `Không có ảnh PDF` | [Sxx](file:///d:/Study/mobile/MidTerm/docs/design/screens/Sxx-settings-effects.md) |

---

## 4. Danh sách các hạng mục chờ quyết định thiết kế `TODO(design)`

Các mục sau đây là chi tiết kỹ thuật hoặc luồng hành vi mà bản thiết kế PDF tĩnh chưa cung cấp đủ thông tin. Đội ngũ phát triển cần lấy ý kiến quyết định từ USER/Product Owner trước khi code chi tiết:

1. `TODO(design-routes)`: **Đồ thị định tuyến (Navigation Routes):** Các route trong file spec (`login`, `dashboard_admin`, `dashboard_employee`, `student_list`, `student_detail`, `student_add`, `student_edit`, `user_list`, `login_history_user`, `profile`, `import_export`, `settings_effects`) cần được cấu hình đồng bộ với Android Jetpack Navigation Graph (`res/navigation/nav_graph.xml`).
2. `TODO(design-icons)`: **Tên Vector Drawable chính xác:** Một số icon trong mockup (`ic_analytics`, `ic_sync_alt`, `ic_folder_empty`, `ic_search_check`, `ic_info`) là tên suy luận từ Material Symbols; cần chốt asset XML chính thức đặt trong `res/drawable/`.
3. `TODO(design-padding-dp)`: **Quy chuẩn khoảng cách lưới:** Kích thước padding/margin chi tiết trong các form và item danh sách được ước lượng từ ảnh (`~12dp`, `~16dp`, `~24dp`); cần thống nhất tuân thủ lưới cơ sở 8dp (`dimens.xml`).
4. `TODO(design-back-behavior)`: **Hành vi nút Back phần cứng/hệ thống:** 
   - Từ màn Chi tiết sinh viên quay lại Danh sách: Giữ nguyên trạng thái cuộn, bộ lọc và từ khóa tìm kiếm hay đặt lại về đầu?
   - Tại màn Trang chủ (Dashboard): Nhấn nút Back thoát app luôn hay hiển thị thông báo "Nhấn lần nữa để thoát"?
5. `TODO(design-role-manager-screens)`: **Giao diện riêng cho vai trò Manager:** Bản thiết kế PDF chỉ có mockup Dashboard Admin và Dashboard Employee. Cần quyết định vai trò Manager sẽ dùng chung giao diện Dashboard Admin (ẩn thẻ Người dùng) hay có màn hình riêng.
6. `TODO(design-dark-employee-dashboard)`: **Màn hình Dark mode cho Dashboard Employee:** PDF thiếu trang Dark mode riêng cho Dashboard Employee (trang 9); hiện đang giả định kế thừa token Dark mode như trang 28.
7. `TODO(design-csv-error-share)`: **Định dạng báo cáo chia sẻ lỗi CSV:** Nút `@string/btn_share_error_report` trên màn S23 sẽ chia sẻ văn bản thô (Plain text qua Intent Android) hay tạo tệp tin đính kèm `.txt`/`.csv`.
8. `TODO(design-motion-b-default)`: **Giá trị mặc định của Gói B (USER quyết định):** Khi người dùng cài mới ứng dụng, công tắc "Hiệu ứng nâng cao" (Gói B) mặc định là BẬT hay TẮT? (Khuyến nghị: **BẬT** để mang lại trải nghiệm tối ưu khi máy cho phép).
9. `TODO(design-motion-storage)`: **Kho lưu trữ công tắc Cài đặt hiệu ứng:** Đề xuất sử dụng `SharedPreferences` hoặc Jetpack `DataStore` (Khuyến nghị: `SharedPreferences` để đọc đồng bộ tức thì lúc Application Start, tránh giật chớp trạng thái UI).
10. `TODO(design-settings-location)`: **Vị trí bố trí màn hình Cài đặt hiệu ứng:** Tích hợp trực tiếp thành một khối Card trong màn Hồ sơ cá nhân (S17/S35) hay tạo màn hình con riêng mở từ icon bánh răng AppBar? (Khuyến nghị: Tích hợp trực tiếp trong Hồ sơ cá nhân).
11. `TODO(design-spring-dependency)`: **Duyệt Dependency cho Spring Animation:** Dự án hiện chưa có thư viện `androidx.dynamicanimation`. Cần quyết định bổ sung dependency này hay sử dụng phương án thay thế `OvershootInterpolator` có sẵn trong SDK tiêu chuẩn (Khuyến nghị: Dùng `OvershootInterpolator`).

> **Phụ thuộc implementation, không phải quyết định thiết kế:** Frame Add User suy ra chỉ được coi là hoàn chỉnh khi đã nối backend/Cloud Function dùng Admin SDK để tạo cả Firebase Auth và `users/{uid}`. Client không tự tạo Auth và không yêu cầu Admin nhập UID.
