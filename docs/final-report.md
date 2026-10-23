# BÁO CÁO BÀI TẬP GIỮA KỲ
## Ứng dụng di động quản lý thông tin sinh viên với Firebase

> **CẢNH BÁO ĐẦU RA:** File này hiện là bản nháp tiếng Việt. `Mid-term.pdf` bắt buộc báo cáo nộp cuối là PDF **bằng tiếng Anh** theo mẫu chung của Khoa; không được xuất file này để nộp khi chưa dịch, biên tập và bổ sung đủ bìa/mục lục/lời cảm ơn/phụ lục/tài liệu tham khảo.

> **Trạng thái:** BÁO CÁO TIẾN ĐỘ THỰC HIỆN — Phạm vi Thành viên 1 còn thiếu backend tạo/xóa danh tính Firebase Auth và nghiệm thu live
>
> **Học phần:** Phát triển ứng dụng di động (Mobile Application Development - Mã HP: 503074)
>
> **Khoa:** Công nghệ Thông tin, Trường Đại học Tôn Đức Thắng
>
> **Học kỳ:** Học kỳ 1, năm học 2026–2027
>
> **Ngày cập nhật:** 18/10/2026

---

## Thông tin nhóm và Phân công trách nhiệm

| Hạng mục | Thông tin chi tiết |
|---|---|
| **Giảng viên hướng dẫn** | `[BẮT BUỘC ĐIỀN: Tên giảng viên hướng dẫn]` |
| **Lớp học phần / Nhóm** | `[BẮT BUỘC ĐIỀN: Mã lớp học phần / Nhóm bài tập]` |
| **Thành viên A (Trưởng nhóm)** | **Trần Ngọc Tấn** — MSSV: **52400158** (Email: `52400158@student.tdtu.edu.vn`) |
| **Thành viên B** | **Nguyễn Vĩnh Khâm** — MSSV: **52400128** (Email: `52400128@student.tdtu.edu.vn`) |
| **Gói ứng dụng (Package)** | `com.example.studentmgmt` |
| **Firebase Project ID** | `mid-moblie` (Xác minh từ tệp cấu hình `google-services.json`) |
| **Firebase Storage Bucket** | `[CHƯA CẤP PHÁT TRÊN CONSOLE - CẦN BỔ SUNG KHI NÂNG CẤP PLAN]` |

### Phân công trách nhiệm chi tiết:
- **Thành viên 1 (Trần Ngọc Tấn):** Đảm nhiệm toàn bộ Module Xác thực (Auth), Quản lý người dùng (User Management), Hồ sơ cá nhân & Ảnh đại diện (Profile & Avatar), Lịch sử đăng nhập (Login History), Phân quyền truy cập (RBAC), Quy tắc bảo mật (Firestore & Storage Security Rules), Chuẩn hóa giao diện Material Components, xử lý trạng thái tải/lỗi/ngoại tuyến (UI State & Offline/Error Handling), TDD Unit Tests, Kiểm thử bảo mật tĩnh và Tổng hợp báo cáo.
- **Thành viên 2 (Nguyễn Vĩnh Khâm):** Đảm nhiệm Module Quản lý sinh viên & Chứng chỉ (Student & Certificate CRUD), Đồng bộ thời gian thực (Realtime Sync), Nhập/Xuất tệp CSV (CSV Import/Export), Bộ dữ liệu mẫu tiếng Việt, Kịch bản kiểm thử sinh viên và Quay video demo thực tế.

---

## Tóm tắt nội dung thực hiện (Executive Summary)

Đề tài xây dựng ứng dụng Android quản lý thông tin sinh viên thời gian thực trên nền tảng Google Firebase. Ứng dụng áp dụng kiến trúc **MVVM (Model - View - ViewModel)** kết hợp **ViewBinding**, **LiveData**, **Material Components**, đảm bảo tính tách bạch tầng dữ liệu, tính khả thử (testability) và an toàn bảo mật nhiều lớp.

Đến thời điểm hiện tại, **phần lớn phạm vi Thành viên 1 đã có code và unit test, nhưng chưa thể kết luận hoàn thành 100%**:
- 16 test suite / 146 unit tests thuộc phạm vi Thành viên 1 đã có trong bộ test Debug; kết quả fresh được ghi ở Mục 4.
- Toàn bộ quy tắc bảo mật `firestore.rules` và `storage.rules` được thiết kế chặt chẽ theo nguyên lý **phòng vệ đa tầng (Defense in Depth)** và **thất bại an toàn (Fail-Closed)**.
- Giao diện người dùng được chuẩn hóa bằng tài nguyên chuỗi (`strings.xml`), đạt chỉ số **0 cảnh báo scoped lint** cho `HardcodedText`, `SetTextI18n` và `DefaultLocale`.
- Phần Thành viên 2 hiện đã điều hướng tới `StudentListFragment`, nhưng Student/Certificate provider mặc định vẫn là in-memory mock; chưa phải bằng chứng Firestore realtime/live.

---

## 1. Công nghệ và Kiến trúc hệ thống

| Thành phần | Công nghệ / Thư viện lựa chọn | Mô tả vai trò |
|---|---|---|
| **Ngôn ngữ phát triển** | Java 17 | Ngôn ngữ hướng đối tượng tiêu chuẩn, áp dụng các tính năng hiện đại |
| **Nền tảng Android SDK** | `minSdk 26` (Android 8.0) / `targetSdk 34` (Android 14) | Đảm bảo tương thích thiết bị rộng rãi và hỗ trợ API bảo mật mới |
| **Kiến trúc ứng dụng** | MVVM (Model - View - ViewModel) | Tách biệt giao diện (View), trạng thái nghiệp vụ (ViewModel) và dữ liệu (Repository) |
| **Giao diện người dùng** | XML Layouts, ViewBinding, Material Components | Đảm bảo Type-safety khi bind view, thiết kế hiện đại, hỗ trợ Dark/Light Theme |
| **Quản lý điều hướng** | AndroidX Navigation Component (`nav_graph.xml`) | Quản lý điều hướng đa màn hình an toàn và kiểm soát ranh giới phân quyền |
| **Xác thực người dùng** | Firebase Authentication (Email/Password Provider) | Dịch vụ xác thực tài khoản an toàn từ Google |
| **Cơ sở dữ liệu** | Cloud Firestore | Cơ sở dữ liệu NoSQL tài liệu, hỗ trợ đồng bộ thời gian thực và offline persistence |
| **Lưu trữ tệp tin** | Cloud Storage for Firebase / Local App Storage | Lưu trữ ảnh đại diện người dùng kèm cơ chế fallback nội bộ |
| **Tải và xử lý ảnh** | Bumptech Glide 4.16 | Tải ảnh bất đồng bộ, bo tròn ảnh đại diện (`circleCrop`) và quản lý bộ nhớ đệm |
| **Kiểm thử tự động** | JUnit 4, AndroidX Architecture Core Testing | Kiểm thử đơn vị logic thuần Java, ViewModel, State Machine và Audit Rules |

---

## 2. Chi tiết Triển khai Module Thành viên 1 (Tasks 1–8)

### 2.1. Module Xác thực & Quản lý phiên (Authentication & Session Security - Tasks 1, 2)
- **Cơ chế phân giải tài khoản:** `DefaultUsernameAuthResolver` nhận username hoặc email hợp lệ. Username được ánh xạ sang định danh Firebase Auth `username@<technical-domain>`; source hiện dùng `tdtu.edu.vn` làm giá trị demo mặc định nhưng constructor cho phép cấu hình domain khác. Domain kỹ thuật này không giới hạn trường email hồ sơ.
- **Email tùy chọn, không khóa domain:** Chuỗi có `@` được chấp nhận khi đúng định dạng email và được chuyển sang chữ thường trước khi gửi Firebase Auth. Email hồ sơ có thể thuộc mọi domain hợp lệ; tài khoản không có email vẫn đăng nhập bằng username.
- **Ranh giới xác thực tài liệu người dùng (`UserDocumentValidator`):**
  - Kiểm tra case sensitivity nghiêm ngặt: Vai trò bắt buộc khớp `admin`, `manager`, `employee`; Trạng thái bắt buộc khớp `Normal`, `Locked`.
  - Tuổi (`age`) bắt buộc là kiểu số nguyên trong khoảng từ `18` đến `60`, được kiểm tra trên kiểu `long` 64-bit trước khi chuyển sang `int` để ngăn ngừa hoàn toàn lỗi tràn số (integer overflow bypass).
  - Số điện thoại bắt buộc gồm đúng 10 chữ số bắt đầu bằng chữ số `0`.
- **Chính sách Giám sát phiên hiện hành (`SessionSecurityPolicy`):** `MainActivity` thiết lập listener lắng nghe theo thời gian thực tài liệu của chính người dùng đang đăng nhập. Nếu tài khoản bị Admin chuyển sang `Locked`, tài liệu bị xóa hoặc quyền truy cập bị từ chối (`PERMISSION_DENIED`/`UNAUTHENTICATED`), hệ thống lập tức cưỡng chế đăng xuất (forced sign-out) và chuyển hướng về màn hình đăng nhập. Các lỗi mạng tạm thời (`UNAVAILABLE`/`DEADLINE_EXCEEDED`) được giữ nguyên để không làm gián đoạn trải nghiệm của người dùng.

### 2.2. Điều hướng & Phân quyền truy cập (RBAC Navigation - Task 3)
- **Chính sách phân quyền `RoleNavigationPolicy`:**
  - **Admin:** Hiển thị mục menu "Quản lý người dùng" (`action_users`), cho phép mở màn hình quản lý tài khoản.
  - **Manager / Employee:** Tự động ẩn menu "Quản lý người dùng". Nếu cố tình kích hoạt điều hướng trực tiếp bằng ID điểm đến, `RoleNavigationPolicy` sẽ chặn đứng và chuyển hướng an toàn về màn hình mặc định kèm thông báo lỗi.
- **Bảo mật phiên:** `MainActivity` lấy thông tin quyền từ phiên đăng nhập tin cậy của `AuthRepositoryProvider`, hoàn toàn không tin tưởng các tham số role truyền qua Intent.

### 2.3. Quy tắc Bảo mật Firestore & Storage (Security Rules - Task 4)
- **Cloud Firestore Rules (`firestore.rules`):**
  - Triển khai helper fail-closed `isActiveUser()`: Bắt buộc người dùng đã đăng nhập Firebase Auth, có tài liệu tại `users/{uid}`, có `status == "Normal"` và vai trò thuộc danh sách cho phép.
  - Nguồn quyền hạn tin cậy: Quyền `isAdmin()` và `isManagerOrAdmin()` luôn được truy vấn từ tài liệu hiện có trong Firestore `currentUser().data.role`, tuyệt đối không đọc từ dữ liệu gửi lên trong yêu cầu `request.resource.data`.
  - Chống Create/Update Bypass: Các trường `uid` và `createdAt` bị khóa bất biến, không thể sửa đổi sau khi tạo; người dùng thông thường chỉ được phép cập nhật duy nhất trường `avatarUrl` ($\le 2048$ ký tự); mọi quyền tạo, cập nhật vai trò, trạng thái hoặc xóa tài liệu `users` đều dành riêng cho Admin.
  - Subcollection `loginHistory`: Quy định nghiêm ngặt **Append-Only / Create-Only** (cho phép tạo bản ghi mới với dữ liệu hợp lệ, nghiêm cấm hoàn toàn thao tác cập nhật `update` và xóa `delete`).
- **Cloud Storage Rules (`storage.rules`):**
  - Tích hợp kiểm tra chéo dịch vụ (Cross-Service Lookup) qua hàm `firestore.get()` đến cơ sở dữ liệu mặc định để xác thực `isActiveUser()` trước khi cấp quyền đọc hoặc ghi tệp.
  - Đường dẫn tệp ảnh đại diện bắt buộc tuân theo quy ước `avatars/{uid}.{jpg|jpeg|png}`, giới hạn định dạng ảnh `image/jpeg` hoặc `image/png` và dung lượng tệp không được vượt quá 5 MiB ($5 \times 1024 \times 1024$ bytes).
- **Harness kiểm thử bảo mật:** Triển khai `SecurityRulesAuditTest` với 9 bài kiểm thử tĩnh xác thực cấu trúc cú pháp và toàn bộ ranh giới bảo mật của cả Firestore và Storage.

### 2.4. Phân hệ Quản lý người dùng (User Management - Task 5)
- **Giao diện & Thao tác quản trị (`UserListFragment`, `UserManagementViewModel`):**
  - Hiển thị danh sách người dùng realtime qua `RecyclerView` với `MaterialCardView`, chip màu phân biệt vai trò và trạng thái.
  - Chức năng Khóa/Mở khóa tài khoản với AlertDialog xác nhận hành động.
  - Chức năng Xóa hồ sơ hiện tại: Có hộp thoại cảnh báo nêu rõ thao tác mới xóa tài liệu Firestore `users/{uid}`; xóa danh tính Firebase Auth hoàn toàn vẫn cần backend/Admin SDK.
  - Chức năng Tạo người dùng mục tiêu: Admin nhập username, mật khẩu, vai trò và hồ sơ trên ứng dụng; backend/Cloud Function xác minh Admin, tạo Firebase Auth bằng Admin SDK rồi tạo `users/{uid}` mà không làm đổi phiên Admin.
  - **Giới hạn implementation hiện tại:** `UserRepository.createUserProfile()` mới tạo Firestore profile cho UID đã được cấp sẵn và chưa gọi backend tạo Firebase Auth. Vì vậy FR-USR-02 phải ghi `NOT IMPLEMENTED END-TO-END` cho tới khi có bằng chứng tạo được cả Auth và Firestore từ thao tác Admin.
  - Cơ chế tự bảo vệ: Vô hiệu hóa nút Khóa và Xóa đối với chính tài khoản Admin đang đăng nhập.

### 2.5. Phân hệ Hồ sơ cá nhân & Tải ảnh đại diện (Profile & Avatar - Task 6)
- **Giao diện hồ sơ (`ProfileFragment`, `ProfileViewModel`):**
  - Hiển thị thông tin cá nhân chi tiết: Họ tên, Username, Email, Tuổi, Số điện thoại, Vai trò và Trạng thái.
  - Hiển thị ảnh đại diện bo tròn bằng Glide.
- **Quy trình tải ảnh an toàn (`AvatarUploadPolicy`):**
  - Lựa chọn ảnh qua `ActivityResultContracts.GetContent()`, không yêu cầu quyền đọc bộ nhớ nguy hiểm (legacy storage permissions).
  - State machine đa trạng thái: `IDLE` $\rightarrow$ `UPLOADING` $\rightarrow$ `UPDATING_FIRESTORE` $\rightarrow$ `SUCCESS` / `ERROR`.
  - Cơ chế khôi phục lỗi: Nếu upload Storage thành công nhưng cập nhật Firestore thất bại, hệ thống lưu tạm download URL vào bộ nhớ đệm và hiển thị nút "Thử lại lưu hồ sơ" để người dùng lưu lại mà không cần tải ảnh lên lần hai.
  - Hỗ trợ lưu trữ ảnh cục bộ dự phòng (Local Private Storage Fallback) khi dịch vụ Cloud Storage chưa kích hoạt bucket trên Firebase Console.

### 2.6. Phân hệ Ghi & Xem Lịch sử đăng nhập (Login History - Task 7)
- **Ghi nhận lịch sử an toàn (`LoginHistoryPolicy`):**
  - Khi đăng nhập thành công và tài khoản hợp lệ (`Normal`), hệ thống tự động ghi bản ghi vào subcollection `users/{uid}/loginHistory` với thời gian từ máy chủ `FieldValue.serverTimestamp()` và tên thiết bị chuẩn hóa ($\le 200$ ký tự).
  - Cơ chế Non-fatal: Nếu việc ghi lịch sử gặp sự cố mạng hoặc lỗi, phiên đăng nhập của người dùng vẫn được bảo toàn nguyên vẹn; cảnh báo lỗi ghi lịch sử được vận chuyển an toàn qua `LoginOutcome` và hiển thị một lần duy nhất qua Toast tại `MainActivity`.
- **Xem lịch sử (`LoginHistoryFragment`, `LoginHistoryAdapter`):**
  - Truy vấn sắp xếp giảm dần theo thời gian (`orderBy("timestamp", DESCENDING)`), bản ghi mới nhất luôn ở trên đầu.
  - Admin có thể xem lịch sử của bất kỳ người dùng nào khi nhấp vào item trong danh sách quản lý; người dùng thông thường chỉ có thể xem lịch sử của chính mình.

### 2.7. Hoàn thiện Trạng thái Giao diện, Ngoại tuyến & Trợ năng (Task 8)
- **Mô hình trạng thái chuẩn hóa (`UiState<T>`):**
  - Quản lý 4 pha giao diện rõ ràng: `LOADING`, `CONTENT`, `EMPTY`, `ERROR`.
  - Phục hồi ngoại tuyến: Khi mất kết nối, danh sách hiển thị khối lỗi `layout_error_*` kèm nút "Thử lại" (Retry). Lỗi tải danh sách được lưu giữ cố định trên màn hình, tách biệt hoàn toàn với thông báo hành động (Action Errors) dạng Snackbar.
- **Ngăn chặn Double-Submit:** Tách riêng cờ `actionInFlight` LiveData khỏi trạng thái tải của listener; sự kiện nạp dữ liệu nền xen giữa không thể reset cờ đang xử lý thao tác.
- **Chống Stale Callback:** Áp dụng token thế hệ `listenerGeneration` trên tất cả các ViewModel; mỗi lần hủy/đăng ký lại listener sẽ tăng số thế hệ, các callback cũ đến muộn tự động bị loại bỏ hoàn toàn.
- **Bảo vệ tái tạo Activity (Rotation/Recreation):** Triển khai cơ chế tiêu thụ một lần `consumeUser()` trong `LoginOutcome`, ngăn chặn việc kích hoạt điều hướng nhiều lần khi xoay màn hình.
- **Chuẩn hóa ngôn ngữ và trợ năng (Accessibility & Lint):**
  - Toàn bộ chuỗi giao diện được đưa vào tài nguyên `strings.xml`.
  - Cung cấp thuộc tính `contentDescription` cho các nút bấm và hình ảnh.
  - Đạt chỉ số **0 scoped lint warnings** đối với `HardcodedText`, `SetTextI18n` và `DefaultLocale` trên toàn bộ mã nguồn Thành viên 1.

---

## 3. Phân định Phạm vi Thành viên 2 (Student, Certificate, CSV)

Theo đúng quy ước phân rã trách nhiệm giữa hai thành viên, các chức năng sau thuộc phạm vi của **Thành viên 2 (Nguyễn Vĩnh Khâm)**:
1. **Quản lý sinh viên & Chứng chỉ:** Mô hình dữ liệu `Student`, `Certificate`, các Repository, ViewModel và màn hình danh sách/chi tiết/thêm/sửa/xóa sinh viên và chứng chỉ.
2. **Nhập / Xuất dữ liệu CSV:** Đọc/ghi tệp CSV hỗ trợ tiếng Việt có dấu, xử lý phân chia lô (batching) tối đa 400 thao tác ghi Firestore mỗi đợt.
3. **Đồng bộ thời gian thực:** Realtime listener cho bộ sưu tập sinh viên và chứng chỉ.

**Hiện trạng tích hợp trên nhánh mã nguồn:**
- `nav_student_list` hiện liên kết trực tiếp với `StudentListFragment`, không còn dùng `StudentPlaceholderFragment` cho route này.
- `StudentRepositoryProvider` và `CertificateRepositoryProvider` hiện mặc định khởi tạo `MockStudentRepository`/`MockCertificateRepository`. Vì vậy CRUD hiện chỉ được chứng minh ở mức demo in-memory và unit test, chưa phải tích hợp Firestore realtime.
- Các yêu cầu bắt buộc còn thiếu ở phần này: sắp xếp sinh viên; sửa chứng chỉ từ UI; import/export sinh viên bằng file thật; import/export chứng chỉ. `StudentCsvHelper` mới xử lý chuỗi CSV, UI import còn dùng sample hardcode và export chỉ hiển thị Toast.

---

## 4. Kết quả Kiểm thử & Minh chứng Kỹ thuật

### 4.1. Kết quả Chạy Gate Kiểm thử Tự động (All-Gates Verification)
Lệnh kiểm thử fresh toàn diện đã được thực thi trên môi trường máy tính phát triển:
```bash
.\gradlew.bat testDebugUnitTest assembleDebug --rerun-tasks --console=plain
```
**Kết quả thực tế từ hệ thống:**
- **Build Status:** `BUILD SUCCESSFUL` (Toàn bộ actionable tasks được thực thi thành công).
- **Kiểm thử đơn vị Debug (`testDebugUnitTest`):** **170 / 170 tests PASS** (0 failures, 0 errors, 0 skipped) trên 21 test suites:
  - **Thành viên 1:** 16 suites / 146 unit tests (PASS 100%).
  - **Thành viên 2:** 5 suites / 24 unit tests (PASS 100%).
- **Kiểm tra Lint (`lintDebug`):** 0 errors. Scoped `HardcodedText`, `SetTextI18n`, `DefaultLocale` đạt **0 warnings**.
- **Đóng gói APK (`assembleDebug`):** Hoàn thành thành công, sinh tệp APK debug sẵn sàng cài đặt.

### 4.2. Bảng phân rã chi tiết 21 Test Suites (170 Tests)

| STT | Tên Test Suite | Thành viên | Số lượng Tests | Trạng thái | Nội dung kiểm thử chính |
|:---:|---|:---:|:---:|:---:|---|
| 1 | `UserManagementPolicyTest` | Mem 1 | 17 | **PASS** | Kiểm tra quyền Admin, RBAC, chống self-lock/delete, ràng buộc dữ liệu người dùng |
| 2 | `UserDocumentValidatorTest` | Mem 1 | 16 | **PASS** | Kiểm tra case sensitive role/status, tuổi số nguyên 18-60, chống tràn số 64-bit |
| 3 | `AvatarUploadPolicyTest` | Mem 1 | 14 | **PASS** | Kiểm tra định dạng ảnh JPEG/PNG, giới hạn dung lượng < 5 MiB, logic đường dẫn UID |
| 4 | `UiStateAndOfflinePolicyTest` | Mem 1 | 12 | **PASS** | Kiểm tra 4 pha UI, nút Retry, chống double submit, chặn stale callback, rotation |
| 5 | `SessionSecurityPolicyTest` | Mem 1 | 10 | **PASS** | Phân loại lỗi typed, cưỡng chế đăng xuất khi tài khoản bị khóa/xóa, giữ kết nối lỗi mạng |
| 6 | `ProfileViewModelTest` | Mem 1 | 10 | **PASS** | State machine upload avatar, lưu cache retry Firestore, quản lý lifecycle |
| 7 | `SecurityRulesAuditTest` | Mem 1 | 9 | **PASS** | Kiểm tra tĩnh quy tắc Firestore và Storage, `isActiveUser()`, quyền create-only log |
| 8 | `LoginViewModelTest` | Mem 1 | 8 | **PASS** | Đăng nhập username, phân giải email, non-fatal history logging, one-shot outcome |
| 9 | `LoginHistoryPolicyTest` | Mem 1 | 8 | **PASS** | Kiểm tra điều kiện ghi lịch sử, RBAC xem lịch sử (Admin xem hết, User xem self) |
| 10 | `UserManagementViewModelTest` | Mem 1 | 8 | **PASS** | CRUD người dùng, trạng thái tải, kiểm soát `actionInFlight`, hủy listener |
| 11 | `ValidationUtilsTest` | Chung | 8 | **PASS** | Kiểm tra định dạng MSSV TDTU 8 ký tự, mã lớp CNTT, username hợp lệ |
| 12 | `RoleNavigationPolicyTest` | Mem 1 | 7 | **PASS** | Kiểm tra menu visibility, bảo vệ navigation guard theo 3 vai trò |
| 13 | `StudentRepositoryTest` | Mem 2 | 7 | **PASS** | CRUD sinh viên trên Repository (Get, Add, chặn trùng MSSV, Update, Delete) |
| 14 | `AuthRepositoryTest` | Mem 1 | 6 | **PASS** | Kiểm tra hợp đồng xác thực, xử lý callback đăng nhập/đăng xuất |
| 15 | `LoginHistoryViewModelTest` | Mem 1 | 6 | **PASS** | Quản lý state lịch sử đăng nhập, token thế hệ retry, sắp xếp thời gian |
| 16 | `StudentListViewModelTest` | Mem 2 | 5 | **PASS** | LiveData danh sách SV, tìm kiếm họ tên/MSSV, lọc Khoa, thêm/xóa cập nhật view |
| 17 | `FoundationTest` | Chung | 4 | **PASS** | Kiểm tra khởi tạo ứng dụng nền tảng và các phụ thuộc cốt lõi |
| 18 | `CertificateRepositoryTest` | Mem 2 | 4 | **PASS** | CRUD chứng chỉ trong subcollection (Auto ID, Add, Update, Delete) |
| 19 | `StudentDetailViewModelTest` | Mem 2 | 4 | **PASS** | Chi tiết SV, nạp chứng chỉ, trạng thái rỗng 0 chứng chỉ, Add/Delete chứng chỉ |
| 20 | `StudentModelAndCsvTest` | Mem 2 | 4 | **PASS** | Model Student/Certificate getters-setters, CSV export UTF-8 BOM/import helper |
| 21 | `DefaultUsernameAuthResolverTest` | Mem 1 | 3 | **PASS** | Kiểm tra ánh xạ username sang domain kỹ thuật, chấp nhận email hợp lệ thuộc domain bất kỳ và từ chối identifier sai định dạng |
| **TỔNG CỘNG** | **21 Suites** | **Mem 1 + 2** | **170 Tests** | **PASS 100%** | **0 failures, 0 errors, 0 skipped** |

### 4.3. Minh bạch Hiện trạng Môi trường & Các Hạng mục Chưa Kiểm thử (NOT TESTED)
Nhằm đảm bảo tính trung thực tuyệt đối trong báo cáo khoa học và kỹ thuật, nhóm ghi nhận rõ các hạng mục thực tế:

| Hạng mục kiểm thử | Trạng thái hiện tại | Nguyên nhân / Kế hoạch thực hiện |
|---|:---:|---|
| **Cấu hình Firebase Client** | **PASS** | Tệp `google-services.json` đã khớp Project ID `mid-moblie` và Package `com.example.studentmgmt`, được bảo vệ bởi `.gitignore`. |
| **Kiểm thử đơn vị tự động** | **PASS** | 170 tests Debug đã thực thi: 146 thuộc Mem 1 và 24 thuộc Mem 2. |
| **Firebase Rules Emulator** | **NOT TESTED** | Lệnh CLI `firebase` chưa được cài đặt trong biến môi trường PATH máy phát triển. |
| **Thiết bị thật / Máy ảo AVD** | **NOT TESTED** | Chưa kết nối thiết bị online tại thời điểm chạy gate tự động (`adb devices` rỗng). |
| **Giao diện Visual Light / Dark** | **NOT TESTED** | Cần khởi chạy trực tiếp trên màn hình thiết bị để chụp ảnh minh chứng visual. |
| **Firebase Live Backend** | **NOT TESTED / CHƯA ĐỦ** | Storage live chưa có bằng chứng; backend Admin SDK cho tạo/xóa danh tính Auth cũng chưa được triển khai. |
| **Kịch bản Thành viên 2 (Student/CSV)** | **NOT TESTED** | Giữ nguyên độc lập theo đúng thỏa thuận phân rã trách nhiệm. |

---

## 5. Danh mục Hình ảnh & Video Minh chứng

> *Ghi chú: Các hình ảnh và video dưới đây sẽ được chụp và đính kèm đầy đủ khi ứng dụng được nạp lên thiết bị thử nghiệm thực tế và hoàn tất module của cả hai thành viên.*

1. **Giao diện Đăng nhập & Xác thực:** `[HÌNH ẢNH: Ảnh chụp màn hình LoginActivity ở chế độ Sáng và Tối]`
2. **Giao diện Quản lý người dùng (Admin):** `[HÌNH ẢNH: Danh sách người dùng, hộp thoại Khóa/Mở khóa, form Thêm/Sửa]`
3. **Giao diện Hồ sơ cá nhân & Ảnh đại diện:** `[HÌNH ẢNH: Màn hình ProfileFragment, quá trình chọn ảnh và thanh tiến trình upload]`
4. **Giao diện Lịch sử đăng nhập:** `[HÌNH ẢNH: Danh sách lịch sử đăng nhập sắp xếp mới nhất trước]`
5. **Minh chứng Firebase Console:**
   - Authentication Users: `[HÌNH ẢNH: Danh sách tài khoản đã cấp phát trên Firebase Console]`
   - Firestore Collections: `[HÌNH ẢNH: Cấu trúc collection users và subcollection loginHistory trên Console]`
   - Security Rules: `[HÌNH ẢNH: Tab Rules trên Firebase Console hiển thị firestore.rules đã triển khai]`
6. **Video Demo Tổng thể:** `[VIDEO DEMO: Đường link video YouTube/Google Drive ghi lại toàn bộ kịch bản sử dụng thực tế]`

---

## 6. Kế hoạch Hoàn thiện Trước Khi Nộp Bài

1. **Phối hợp tích hợp Thành viên 2:**
   - Nhận bàn giao mã nguồn Module Quản lý sinh viên, Chứng chỉ và CSV từ Thành viên 2 (Nguyễn Vĩnh Khâm).
   - Thay thế các Placeholder Fragment bằng Fragment thực tế của Thành viên 2 trong `nav_graph.xml`.
2. **Triển khai Firebase Live:**
   - Cấp phát và cấu hình Firebase Storage Bucket trên Firebase Console (hoặc cấu hình nâng cấp nếu cần thiết).
   - Triển khai backend/Cloud Function Admin SDK cho luồng Admin tạo/xóa tài khoản. Trước khi có backend, chỉ cấp thủ công 3 tài khoản demo `admin`, `manager`, `employee` và gắn nhãn `MANUALLY PROVISIONED`.
   - Triển khai `firestore.rules` và `storage.rules` lên máy chủ Firebase Live.
3. **Chạy nghiệm thu trên Thiết bị thật:**
   - Chạy toàn bộ 28 kịch bản kiểm thử trên máy ảo Pixel và thiết bị Android thật.
   - Chụp ảnh màn hình giao diện ở cả hai chế độ Light Theme và Dark Theme.
   - Quay video demo thực tế có thuyết minh kịch bản.
4. **Hoàn thiện Báo cáo:**
   - Chuyển toàn bộ nội dung sang tiếng Anh và định dạng theo mẫu chung của Khoa.
   - Bổ sung phần kiến thức Firebase Firestore đã học, phân tích/đánh giá và chi tiết cách xây dựng ứng dụng; không chỉ liệt kê code.
   - Điền đầy đủ họ tên Giảng viên hướng dẫn và Mã lớp học phần / Nhóm.
   - Bổ sung đường dẫn video demo và xuất tệp PDF chính thức nộp bài.

---

## Tài liệu Tham khảo

1. Google Firebase, *Cloud Firestore Documentation*: <https://firebase.google.com/docs/firestore>
2. Google Firebase, *Firebase Authentication for Android*: <https://firebase.google.com/docs/auth/android/start>
3. Google Firebase, *Firebase Security Rules Guide*: <https://firebase.google.com/docs/rules>
4. Android Developers, *Guide to App Architecture*: <https://developer.android.com/topic/architecture>
5. Material Components for Android: <https://github.com/material-components/material-components-android>

---

## Checklist Bàn giao Báo cáo Task 9

- [x] Điền đầy đủ thông tin định danh Thành viên A và Thành viên B kèm MSSV chính xác.
- [x] Báo cáo nêu rõ phần đã có, phần chưa có backend và phần chưa nghiệm thu live của Thành viên 1.
- [x] Bảo vệ ranh giới phân rã: Không viết thay hoặc chỉnh sửa phần Student/Certificate/CSV của Thành viên 2.
- [x] Phản ánh chính xác các quyết định kiến trúc: Username mapping qua domain kỹ thuật cấu hình được, email tùy chọn không khóa domain, session forced logout, append-only login history và local storage fallback.
- [x] Đưa ra số liệu kiểm thử Debug: 21 suites / 170 tests toàn dự án, trong đó 16 suites / 146 tests thuộc Mem 1.
- [x] Ghi rõ các hạng mục `NOT TESTED` (Firebase live, Rules emulator, thiết bị thật, giao diện visual).
- [x] Đặt nhãn placeholder rõ ràng cho giảng viên, lớp học phần, Storage bucket, ảnh chụp và video demo.
- [ ] Điền thông tin Giảng viên hướng dẫn và Lớp học phần khi nộp bài chính thức.
- [ ] Dịch và biên tập toàn bộ báo cáo sang tiếng Anh theo mẫu Khoa.
- [ ] Chèn ảnh chụp màn hình và video demo sau khi nghiệm thu thiết bị thật cùng Thành viên 2.
- [ ] Xuất tệp PDF báo cáo nộp môn học.
