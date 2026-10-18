# UniData UI/UX Design Specification

**Nguồn thiết kế:** `docs/Untitled.pdf`  
**Ngày phân tích:** 2026-10-18  
**Phạm vi:** toàn bộ giao diện Android; giữ nguyên logic Firebase/Auth/RBAC đã hoàn thành.

## 1. Kết luận từ mẫu

Mẫu bàn giao dùng hướng **Clean Academic Material 3**, khung tham chiếu `412 × 892 dp`, nền phẳng, card không elevation, lưới 8dp và điều hướng đáy ba mục. PDF có 37 trang: 12 màn tham chiếu ban đầu, 24 màn/trạng thái bổ sung và 1 trang bàn giao token/tương tác.

Không coi mockup là bằng chứng runtime. Shimmer, validate, transition, offline, upload và import/export trong PDF là yêu cầu cần triển khai và kiểm chứng riêng.

## 2. Bản đồ màn hình và trạng thái

| Nhóm | Trang PDF | UI cần có |
|---|---:|---|
| Đăng nhập | 1–3, 27 | mặc định, đang đăng nhập, lỗi inline, dark |
| Dashboard | 4, 9, 28 | Admin/Manager, Employee, dark; card chức năng theo role |
| Sinh viên | 7, 12–14, 24–26, 29 | danh sách, tìm/lọc/sắp xếp, xóa + hoàn tác, loading skeleton, empty, offline cache, Employee read-only, dark |
| Chi tiết sinh viên | 8, 15–16, 30 | thông tin, chứng chỉ, menu sửa/xóa theo role, dialog xóa cascade, dark |
| Form sinh viên | 5–6, 19–20, 31–32 | thêm/sửa, date picker, MSSV read-only khi sửa, lỗi từng trường, CTA ghim đáy, dark |
| Người dùng | 10, 21, 33 | danh sách, role/status, không tự khóa, dialog khóa, mở khóa inline, dark |
| Lịch sử đăng nhập | 11, 34 | nhóm theo ngày, thiết bị/thời gian/trạng thái, dark |
| Hồ sơ | 17, 22, 35 | avatar, thông tin tài khoản thật, lịch sử của tôi, upload 65%, dark |
| Import/Export | 18, 23, 36 | chọn Student/Certificate, chọn/tải mẫu/xuất CSV, kết quả từng dòng, chia sẻ báo cáo lỗi, dark |

## 3. Design tokens bắt buộc

### Màu

| Token repo | Light | Dark |
|---|---:|---:|
| `color_primary` | `#3F51B5` | `#B6BFFF` |
| `color_secondary` | `#00897B` | `#6FD8C8` |
| `color_surface` | `#FAFAFD` | `#121318` |
| `color_on_surface` | `#1B1B23` | `#F1F0F7` |
| `color_on_surface_variant` | `#5E6072` | `#BFC1D2` |
| `color_status_normal` | `#2E7D32` | `#81C784` |
| `color_status_locked` | `#C62828` | `#EF9A9A` |
| `color_role_admin` | `#6A1B9A` | `#E2B8FA` |
| `color_role_manager` | `#1565C0` | `#A6CDFF` |
| `color_role_employee` | `#546E7A` | `#C1D8E2` |
| `bg_card` | `#FFFFFF` | `#1B1C24` |
| `bg_primary` | `#EEF0FC` | `#292F54` |
| `bg_normal` | `#EDF6EE` | `#203927` |
| `bg_locked` | `#FDECEC` | `#48282C` |
| `bg_offline` | `#FFF0DE` | `#482F1D` |

- `ic_default = color_on_surface_variant`, `ic_active = color_primary`, `ic_verified = color_secondary`, `ic_locked = color_status_locked`.
- Màu role/status luôn đi cùng nhãn chữ; không dùng màu làm tín hiệu duy nhất.
- Dark primary đặt chữ tối `#18204F` trên nút nền `#B6BFFF`.

### Kiểu chữ, shape và spacing

- Roboto hệ thống; tiêu đề màn `22sp/500`, tên/card title `16sp/500`, nội dung phụ `14sp`, chip `12sp`.
- Lề màn 16dp; nhịp 4/8dp; khoảng cách card 12dp.
- Card radius 16dp, input radius 12dp, button/chip dạng pill; stroke 1dp; elevation 0.
- Mọi vùng chạm tối thiểu 48dp trên Android.
- Nội dung phải cuộn khi fontScale tăng; CTA đáy không che field cuối hoặc bàn phím.

## 4. Điều hướng và phân quyền

- Bottom navigation chỉ có `Trang chủ`, `Sinh viên`, `Hồ sơ`.
- `Người dùng` chỉ Admin thấy và mở từ dashboard.
- `Import/Export` chỉ Admin/Manager thấy và mở từ dashboard.
- Admin/Manager thấy thao tác thêm/sửa/xóa Student và Certificate; Employee chỉ xem.
- Back phải theo NavController; deep link/direct navigation tiếp tục qua `RoleNavigationPolicy` fail-closed.
- Bottom navigation chỉ hiện tại ba destination gốc; ẩn ở detail/form/history/import-export để không che CTA.

## 5. Tương tác chuẩn

- Form validate khi rời field và khi submit; lỗi nằm dưới đúng field, giữ nguyên dữ liệu đã nhập; nút Lưu disabled khi còn lỗi.
- Date picker hỗ trợ chuyển tháng và nhập ngày bằng bàn phím.
- Search/filter ghim đầu danh sách; sort dùng bottom sheet; danh sách không nhảy vị trí khi loading.
- Loading danh sách dùng skeleton/shimmer đúng bố cục card, không dùng spinner giữa màn.
- Offline có banner cam, thời điểm bản lưu và nhãn `Ngoại tuyến`; không đồng thời hiện `Đã đồng bộ`.
- Khóa tài khoản dùng dialog nêu tên, username và hậu quả. Mở khóa dùng xác nhận inline trong card. Không cho tự khóa.
- Xóa sinh viên dùng dialog nêu rõ xóa cascade chứng chỉ; thành công hiện Snackbar có `Hoàn tác` trong demo repository.
- Upload avatar có tiến độ xác định và Snackbar khi thành công.
- Motion 250–300ms chỉ dùng khi giúp định hướng; tôn trọng reduced motion/Animator duration scale.

## 6. Ranh giới dữ liệu thật và dữ liệu demo

1. Auth, User, Profile, Login History tiếp tục dùng repository/Firebase hiện có. Không đổi schema, Rules hoặc cơ chế fail-closed chỉ để khớp mockup.
2. Student, Certificate và CSV được phép dùng `Demo*Repository`; mọi class phải có nhãn `DEMO_ONLY` trong Javadoc/comment và được cấp qua Provider/Interface để thay bằng Firebase sau này.
3. Không hiển thị dữ liệu demo như dữ liệu đã đồng bộ thật. `Đã đồng bộ` chỉ là trạng thái mô phỏng do demo repository trả về và phải được mô tả trong tài liệu/dev comment.
4. PDF có trường email sinh viên và điểm/kết quả chứng chỉ ngoài schema hiện tại. Chúng chỉ là thuộc tính trình bày tùy chọn trong model demo; không sửa `firestore.rules` hoặc tuyên bố đã lưu Firebase.
5. `Quên mật khẩu`, chuông thông báo và `Đổi mật khẩu` phải có phản hồi UI rõ ràng nhưng không được giả là backend đã xử lý nếu chưa có luồng tin cậy.

## 7. Trợ năng

- Contrast chữ thường tối thiểu 4.5:1; control/icon mang nghĩa tối thiểu 3:1.
- Icon trang trí bị loại khỏi accessibility tree; icon độc lập có `contentDescription`.
- Các nhãn cần có gồm: quay lại, đổi avatar, chọn/ngày bằng bàn phím, tháng trước/sau, menu sinh viên, sort, search, lọc khoa/lớp, thêm sinh viên, hiện mật khẩu, thông báo, chọn/tải mẫu/xuất CSV và chia sẻ báo cáo lỗi.
- Switch đọc cả hành động, tên tài khoản và trạng thái; avatar/tiến độ đọc tên và phần trăm.
- Kiểm tra fontScale lớn, portrait/landscape, light/dark và không để bottom bar/CTA che nội dung.

## 8. Sai khác hiện trạng cần xử lý

- Theme hiện tại là Material Components `DarkActionBar`, màu xanh lá và hardcode cục bộ; phải chuyển sang Material 3 NoActionBar + semantic tokens light/night.
- `activity_main.xml` chỉ có NavHost, chưa có dashboard và bottom navigation.
- `nav_graph.xml` bắt đầu ở Student placeholder; chưa có Home, detail/form Student, Certificate hoặc Import/Export.
- Student/Certificate/CSV chưa có model/repository/UI thật.
- Login đang dùng spinner và card elevation; User/Profile/History chưa theo hierarchy, token và trạng thái trong mockup.
- Một số icon dùng Android platform drawable; phải thay bằng vector asset đồng bộ.

## 9. Điều kiện nghiệm thu visual

- Build/lint/unit test chỉ chứng minh mã và resource hợp lệ, không chứng minh hình ảnh đúng.
- Mỗi checkpoint phải cung cấp ảnh light + dark tại kích thước gần `412 × 892 dp` nếu có emulator/device.
- Nếu không có AVD/device, ghi rõ `VISUAL NOT TESTED`; Agent B vẫn review XML, token, state/role tests và build nhưng không gọi visual là PASS.
