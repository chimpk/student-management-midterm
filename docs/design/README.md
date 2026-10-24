# Tài liệu Thiết kế & Hướng dẫn Kỹ thuật Giao diện (Design Specs)

Tài liệu này tổng hợp toàn bộ đặc tả giao diện (UI/UX specs), hệ thống tokens thiết kế, quy ước đặt tên và ma trận trạng thái từ hai tài liệu bàn giao `docs/Untitled.pdf` và `docs/Untitled (1).pdf`.

---

## 1. Hướng dẫn & Thứ tự đọc tài liệu

Để tiếp cận và triển khai giao diện chuẩn xác nhất, khuyến nghị đọc các tài liệu theo thứ tự sau:

1. **[README.md](file:///d:/Study/mobile/MidTerm/docs/design/README.md):** Tổng quan, quy ước đặt tên, bảng mục lục 81 màn hình/trạng thái và danh sách quyết định thiết kế `TODO(design)`.
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
10. **[screens/](file:///d:/Study/mobile/MidTerm/docs/design/screens/):** Chi tiết đặc tả 81 màn hình/trạng thái (S01 đến S81) theo cấu trúc 7 mục quy chuẩn cùng màn đề xuất bổ sung [Sxx-settings-effects](file:///d:/Study/mobile/MidTerm/docs/design/screens/Sxx-settings-effects.md).
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

## 3. Bảng danh mục toàn bộ màn hình thiết kế (81 màn/trạng thái)

> `Untitled (1).pdf` chứa lại 36 frame đã có trong S01–S36 và bổ sung 45 frame khác biệt. Catalog chỉ tạo S37–S81 cho các frame mới, không nhân đôi màn cũ. Xem [ghi chú đối chiếu nguồn](source-handoff-untitled-1.md).

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
| **S27** | Đăng nhập - Chế độ tối (Dark) | Trang 27 | Xác thực | Thành viên 1 | `reference/S27-login-default-dark.png` | [S27](file:///d:/Study/mobile/MidTerm/docs/design/screens/S27-login-default-dark.md) |
| **S28** | Dashboard - Admin Chế độ tối (Dark) | Trang 28 | Điều hướng chính | Thành viên 1 | `reference/S28-dashboard-admin-dark.png` | [S28](file:///d:/Study/mobile/MidTerm/docs/design/screens/S28-dashboard-admin-dark.md) |
| **S29** | Sinh viên - Danh sách Admin Dark | Trang 29 | Quản lý sinh viên | Thành viên 2 | `reference/S29-student-list-admin-dark.png` | [S29](file:///d:/Study/mobile/MidTerm/docs/design/screens/S29-student-list-admin-dark.md) |
| **S30** | Sinh viên - Chi tiết Admin Dark | Trang 30 | Quản lý sinh viên | Thành viên 2 | `reference/S30-student-detail-admin-dark.png` | [S30](file:///d:/Study/mobile/MidTerm/docs/design/screens/S30-student-detail-admin-dark.md) |
| **S31** | Sinh viên - Thêm mới Dark | Trang 31 | Quản lý sinh viên | Thành viên 2 | `reference/S31-student-add-default-dark.png` | [S31](file:///d:/Study/mobile/MidTerm/docs/design/screens/S31-student-add-default-dark.md) |
| **S32** | Sinh viên - Sửa đổi Dark | Trang 32 | Quản lý sinh viên | Thành viên 2 | `reference/S32-student-edit-default-dark.png` | [S32](file:///d:/Study/mobile/MidTerm/docs/design/screens/S32-student-edit-default-dark.md) |
| **S33** | Quản lý người dùng - Admin Dark | Trang 33 | Quản trị người dùng | Thành viên 1 | `reference/S33-user-list-admin-dark.png` | [S33](file:///d:/Study/mobile/MidTerm/docs/design/screens/S33-user-list-admin-dark.md) |
| **S34** | Lịch sử đăng nhập - Dark | Trang 34 | Quản trị người dùng | Thành viên 1 | `reference/S34-login-history-user-dark.png` | [S34](file:///d:/Study/mobile/MidTerm/docs/design/screens/S34-login-history-user-dark.md) |
| **S35** | Hồ sơ cá nhân - Admin Dark | Trang 35 | Hồ sơ & Bảo mật | Thành viên 1 | `reference/S35-profile-admin-dark.png` | [S35](file:///d:/Study/mobile/MidTerm/docs/design/screens/S35-profile-admin-dark.md) |
| **S36** | Import / Export CSV - Dark | Trang 36 | Dữ liệu CSV | Thành viên 2 | `reference/S36-import-export-default-dark.png` | [S36](file:///d:/Study/mobile/MidTerm/docs/design/screens/S36-import-export-default-dark.md) |
| **S37** | Dashboard - Manager (Light) | PDF mới trang 12 | Điều hướng chính | Thành viên 1 | `reference/S37-dashboard-manager-light.png` | [S37](screens/S37-dashboard-manager-light.md) |
| **S38** | Dashboard - Manager (Dark) | PDF mới trang 13 | Điều hướng chính | Thành viên 1 | `reference/S38-dashboard-manager-dark.png` | [S38](screens/S38-dashboard-manager-dark.md) |
| **S39** | Dashboard - Employee (Dark) | PDF mới trang 14 | Điều hướng chính | Thành viên 1 | `reference/S39-dashboard-employee-dark.png` | [S39](screens/S39-dashboard-employee-dark.md) |
| **S40** | Người dùng - Thêm mới (Light) | PDF mới trang 20 | Quản trị người dùng | Thành viên 1 | `reference/S40-user-add-default-light.png` | [S40](screens/S40-user-add-default-light.md) |
| **S41** | Người dùng - Sửa đổi (Light) | PDF mới trang 21 | Quản trị người dùng | Thành viên 1 | `reference/S41-user-edit-default-light.png` | [S41](screens/S41-user-edit-default-light.md) |
| **S42** | Người dùng - Lỗi nhập liệu (Light) | PDF mới trang 22 | Quản trị người dùng | Thành viên 1 | `reference/S42-user-add-validation-errors-light.png` | [S42](screens/S42-user-add-validation-errors-light.md) |
| **S43** | Người dùng - Đang lưu (Light) | PDF mới trang 27 | Quản trị người dùng | Thành viên 1 | `reference/S43-user-edit-saving-light.png` | [S43](screens/S43-user-edit-saving-light.md) |
| **S44** | Người dùng - Bảo vệ tài khoản hiện tại (Light) | PDF mới trang 28 | Quản trị người dùng | Thành viên 1 | `reference/S44-user-edit-self-protection-light.png` | [S44](screens/S44-user-edit-self-protection-light.md) |
| **S45** | Người dùng - Dialog xóa (Light) | PDF mới trang 29 | Quản trị người dùng | Thành viên 1 | `reference/S45-user-delete-dialog-light.png` | [S45](screens/S45-user-delete-dialog-light.md) |
| **S46** | Chứng chỉ - Danh sách Admin (Light) | PDF mới trang 30 | Quản lý chứng chỉ | Thành viên 2 | `reference/S46-certificate-list-admin-light.png` | [S46](screens/S46-certificate-list-admin-light.md) |
| **S47** | Chứng chỉ - Danh sách chỉ đọc (Light) | PDF mới trang 31 | Quản lý chứng chỉ | Thành viên 2 | `reference/S47-certificate-list-employee-light.png` | [S47](screens/S47-certificate-list-employee-light.md) |
| **S48** | Chứng chỉ - Thêm mới (Light) | PDF mới trang 32 | Quản lý chứng chỉ | Thành viên 2 | `reference/S48-certificate-add-default-light.png` | [S48](screens/S48-certificate-add-default-light.md) |
| **S49** | Chứng chỉ - Sửa đổi (Light) | PDF mới trang 35 | Quản lý chứng chỉ | Thành viên 2 | `reference/S49-certificate-edit-default-light.png` | [S49](screens/S49-certificate-edit-default-light.md) |
| **S50** | Chứng chỉ - Lỗi ngày cấp (Light) | PDF mới trang 36 | Quản lý chứng chỉ | Thành viên 2 | `reference/S50-certificate-add-validation-errors-light.png` | [S50](screens/S50-certificate-add-validation-errors-light.md) |
| **S51** | Chứng chỉ - Dialog xóa (Light) | PDF mới trang 37 | Quản lý chứng chỉ | Thành viên 2 | `reference/S51-certificate-delete-dialog-light.png` | [S51](screens/S51-certificate-delete-dialog-light.md) |
| **S52** | Chứng chỉ - Đang tải (Light) | PDF mới trang 38 | Quản lý chứng chỉ | Thành viên 2 | `reference/S52-certificate-list-loading-light.png` | [S52](screens/S52-certificate-list-loading-light.md) |
| **S53** | Chứng chỉ - Danh sách trống (Light) | PDF mới trang 39 | Quản lý chứng chỉ | Thành viên 2 | `reference/S53-certificate-list-empty-light.png` | [S53](screens/S53-certificate-list-empty-light.md) |
| **S54** | Chứng chỉ - Lỗi tải dữ liệu (Light) | PDF mới trang 40 | Quản lý chứng chỉ | Thành viên 2 | `reference/S54-certificate-list-error-light.png` | [S54](screens/S54-certificate-list-error-light.md) |
| **S55** | Import chứng chỉ - Mặc định (Light) | PDF mới trang 43 | Dữ liệu CSV | Thành viên 2 | `reference/S55-certificate-import-default-light.png` | [S55](screens/S55-certificate-import-default-light.md) |
| **S56** | Import chứng chỉ - Mặc định (Dark) | PDF mới trang 44 | Dữ liệu CSV | Thành viên 2 | `reference/S56-certificate-import-default-dark.png` | [S56](screens/S56-certificate-import-default-dark.md) |
| **S57** | Import chứng chỉ - Đang xử lý (Light) | PDF mới trang 45 | Dữ liệu CSV | Thành viên 2 | `reference/S57-certificate-import-processing-light.png` | [S57](screens/S57-certificate-import-processing-light.md) |
| **S58** | Import chứng chỉ - Đang xử lý (Dark) | PDF mới trang 46 | Dữ liệu CSV | Thành viên 2 | `reference/S58-certificate-import-processing-dark.png` | [S58](screens/S58-certificate-import-processing-dark.md) |
| **S59** | Import chứng chỉ - Kết quả (Light) | PDF mới trang 47 | Dữ liệu CSV | Thành viên 2 | `reference/S59-certificate-import-result-light.png` | [S59](screens/S59-certificate-import-result-light.md) |
| **S60** | Import chứng chỉ - Kết quả (Dark) | PDF mới trang 48 | Dữ liệu CSV | Thành viên 2 | `reference/S60-certificate-import-result-dark.png` | [S60](screens/S60-certificate-import-result-dark.md) |
| **S61** | Import chứng chỉ - Sai định dạng (Light) | PDF mới trang 51 | Dữ liệu CSV | Thành viên 2 | `reference/S61-certificate-import-invalid-format-light.png` | [S61](screens/S61-certificate-import-invalid-format-light.md) |
| **S62** | Import chứng chỉ - Sai định dạng (Dark) | PDF mới trang 52 | Dữ liệu CSV | Thành viên 2 | `reference/S62-certificate-import-invalid-format-dark.png` | [S62](screens/S62-certificate-import-invalid-format-dark.md) |
| **S63** | Import chứng chỉ - Tệp quá lớn (Light) | PDF mới trang 53 | Dữ liệu CSV | Thành viên 2 | `reference/S63-certificate-import-too-large-light.png` | [S63](screens/S63-certificate-import-too-large-light.md) |
| **S64** | Import chứng chỉ - Tệp quá lớn (Dark) | PDF mới trang 54 | Dữ liệu CSV | Thành viên 2 | `reference/S64-certificate-import-too-large-dark.png` | [S64](screens/S64-certificate-import-too-large-dark.md) |
| **S65** | Import chứng chỉ - Không tìm thấy MSSV (Light) | PDF mới trang 55 | Dữ liệu CSV | Thành viên 2 | `reference/S65-certificate-import-missing-student-light.png` | [S65](screens/S65-certificate-import-missing-student-light.md) |
| **S66** | Import chứng chỉ - Không tìm thấy MSSV (Dark) | PDF mới trang 56 | Dữ liệu CSV | Thành viên 2 | `reference/S66-certificate-import-missing-student-dark.png` | [S66](screens/S66-certificate-import-missing-student-dark.md) |
| **S67** | Export chứng chỉ (Light) | PDF mới trang 59 | Dữ liệu CSV | Thành viên 2 | `reference/S67-certificate-export-light.png` | [S67](screens/S67-certificate-export-light.md) |
| **S68** | Export chứng chỉ (Dark) | PDF mới trang 60 | Dữ liệu CSV | Thành viên 2 | `reference/S68-certificate-export-dark.png` | [S68](screens/S68-certificate-export-dark.md) |
| **S69** | Import chứng chỉ - Lỗi máy chủ (Light) | PDF mới trang 61 | Dữ liệu CSV | Thành viên 2 | `reference/S69-certificate-import-server-error-light.png` | [S69](screens/S69-certificate-import-server-error-light.md) |
| **S70** | Import chứng chỉ - Lỗi máy chủ (Dark) | PDF mới trang 62 | Dữ liệu CSV | Thành viên 2 | `reference/S70-certificate-import-server-error-dark.png` | [S70](screens/S70-certificate-import-server-error-dark.md) |
| **S71** | Người dùng - Đang tải danh sách (Light) | PDF mới trang 63 | Quản trị người dùng | Thành viên 1 | `reference/S71-user-list-loading-light.png` | [S71](screens/S71-user-list-loading-light.md) |
| **S72** | Người dùng - Danh sách trống (Light) | PDF mới trang 64 | Quản trị người dùng | Thành viên 1 | `reference/S72-user-list-empty-light.png` | [S72](screens/S72-user-list-empty-light.md) |
| **S73** | Người dùng - Lỗi tải dữ liệu (Light) | PDF mới trang 67 | Quản trị người dùng | Thành viên 1 | `reference/S73-user-list-error-light.png` | [S73](screens/S73-user-list-error-light.md) |
| **S74** | Người dùng - Ngoại tuyến (Light) | PDF mới trang 68 | Quản trị người dùng | Thành viên 1 | `reference/S74-user-list-offline-light.png` | [S74](screens/S74-user-list-offline-light.md) |
| **S75** | Lịch sử đăng nhập - Đang tải (Light) | PDF mới trang 69 | Quản trị người dùng | Thành viên 1 | `reference/S75-login-history-loading-light.png` | [S75](screens/S75-login-history-loading-light.md) |
| **S76** | Lịch sử đăng nhập - Trống (Light) | PDF mới trang 70 | Quản trị người dùng | Thành viên 1 | `reference/S76-login-history-empty-light.png` | [S76](screens/S76-login-history-empty-light.md) |
| **S77** | Lịch sử đăng nhập - Lỗi tải (Light) | PDF mới trang 71 | Quản trị người dùng | Thành viên 1 | `reference/S77-login-history-error-light.png` | [S77](screens/S77-login-history-error-light.md) |
| **S78** | Hồ sơ - Đang cập nhật (Light) | PDF mới trang 72 | Hồ sơ & Bảo mật | Thành viên 1 | `reference/S78-profile-loading-light.png` | [S78](screens/S78-profile-loading-light.md) |
| **S79** | Hồ sơ - Lỗi và dữ liệu lưu (Light) | PDF mới trang 75 | Hồ sơ & Bảo mật | Thành viên 1 | `reference/S79-profile-error-cached-light.png` | [S79](screens/S79-profile-error-cached-light.md) |
| **S80** | Phiên đăng nhập hết hạn (Light) | PDF mới trang 76 | Xác thực | Thành viên 1 | `reference/S80-session-expired-light.png` | [S80](screens/S80-session-expired-light.md) |
| **S81** | Tài khoản bị khóa (Light) | PDF mới trang 77 | Xác thực | Thành viên 1 | `reference/S81-account-locked-light.png` | [S81](screens/S81-account-locked-light.md) |
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
5. `TODO(design-csv-error-share)`: **Định dạng báo cáo chia sẻ lỗi CSV:** Nút `@string/btn_share_error_report` trên màn S23/S59/S60 sẽ chia sẻ văn bản thô hay tạo tệp đính kèm `.txt`/`.csv`.
6. `TODO(design-motion-b-default)`: **Giá trị mặc định của Gói B:** Khi cài mới, công tắc "Hiệu ứng nâng cao" mặc định BẬT hay TẮT?
7. `TODO(design-motion-storage)`: **Kho lưu trữ công tắc:** Chốt `SharedPreferences` hoặc Jetpack `DataStore`.
8. `TODO(design-settings-location)`: **Vị trí màn Cài đặt hiệu ứng:** Tích hợp trong Hồ sơ hay tạo màn con riêng.
9. `TODO(design-spring-dependency)`: **Spring Animation:** Bổ sung `androidx.dynamicanimation` hay dùng `OvershootInterpolator`.

> **Phụ thuộc implementation, không phải quyết định thiết kế:** Frame Add User suy ra chỉ được coi là hoàn chỉnh khi đã nối backend/Cloud Function dùng Admin SDK để tạo cả Firebase Auth và `users/{uid}`. Client không tự tạo Auth và không yêu cầu Admin nhập UID.
