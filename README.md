# Ứng dụng quản lý thông tin sinh viên thời gian thực

> **Học phần:** Phát triển ứng dụng di động (Mobile App Development) – Mã HP: 503074  
> **Trường:** Đại học Tôn Đức Thắng (TDTU) – Khoa Công nghệ Thông tin  
> **Đề bài giữa kỳ:** [`docs/Mid-term.pdf`](docs/Mid-term.pdf)  
> **Trạng thái dự án:** **Khung ứng dụng đang phát triển**. Hiện repository mới có màn hình mẫu và tài liệu thiết kế; các chức năng, kiểm thử và bằng chứng demo bên dưới là mục tiêu triển khai, chưa phải kết quả đã hoàn thành.

---

## 1. Giới thiệu tổng quan

Dự án môn học hướng tới một ứng dụng Android quản lý thông tin sinh viên bằng **Cloud Firestore** và **Cloud Storage for Firebase**. Thiết kế đích sử dụng MVVM, đồng bộ thời gian thực, làm việc ngoại tuyến và ba vai trò **Admin, Manager, Employee**.

---

## 2. Tính năng & Phân quyền Role-Based (RBAC)

- **Admin (Quản trị viên):** 
  - Toàn quyền hệ thống. Quản lý danh sách tài khoản người dùng (tạo mới Manager/Employee, chỉnh sửa, khóa/mở khóa tài khoản).
  - Xem lịch sử đăng nhập chi tiết của mọi tài khoản.
  - Toàn quyền quản trị sinh viên, chứng chỉ, và xuất dữ liệu CSV.
- **Manager (Quản lý sinh viên):** 
  - Toàn quyền đối với sinh viên và chứng chỉ: Xem, thêm mới, chỉnh sửa, xóa sinh viên (kèm cascade delete chứng chỉ).
  - Tìm kiếm thời gian thực (hỗ trợ tiếng Việt có dấu), sắp xếp theo tên, ngày sinh và lớp.
  - Nhập (Import) và Xuất (Export) dữ liệu hàng loạt qua file CSV chuẩn UTF-8 BOM.
  - Bị chặn truy cập các chức năng quản lý tài khoản.
- **Employee (Nhân viên):** 
  - Chỉ xem (Read-only) danh sách sinh viên và chứng chỉ. Các nút Thêm/Sửa/Xóa bị ẩn/chặn.
  - Được phép cập nhật thông tin cá nhân và thay đổi ảnh đại diện (Upload Avatar).

*Chi tiết đặc tả:* [`docs/SRS.md`](docs/SRS.md) | [`docs/rbac-matrix.md`](docs/rbac-matrix.md).

---

## 3. Công nghệ & Kiến trúc

- **Ngôn ngữ & Nền tảng:** Android Java (JDK 17), `minSdk 26` (Android 8.0+), `targetSdk 34` (Android 14).
- **Kiến trúc đích:** MVVM (Model - View - ViewModel) với Android Architecture Components (ViewModel, LiveData, ViewBinding).
- **Điều hướng đích:** hai Activity: `LoginActivity` cho đăng nhập và `MainActivity` chứa Navigation Component cùng các Fragment nghiệp vụ.
- **Cloud Backend:** 
  - **Xác thực:** giao diện dùng tên đăng nhập/mật khẩu; email chỉ là bí danh tùy chọn. Nếu chọn Firebase Authentication Email/Password khi triển khai, phải có lớp ánh xạ phù hợp và tuân thủ giới hạn mật khẩu của Firebase.
  - **Cloud Firestore:** Cơ sở dữ liệu NoSQL thời gian thực, quản lý snapshot listener hai chiều.
  - **Cloud Storage for Firebase:** Lưu trữ tệp ảnh đại diện của người dùng.
- **Thư viện bên thứ ba:** Bumptech Glide (nạp và cache ảnh mượt mà).

---

## 4. Hướng dẫn cài đặt & Chạy ứng dụng

> Chi tiết từng bước kèm hình ảnh và xử lý sự cố: xem tại **[`docs/test-and-setup.md`](docs/test-and-setup.md)** và **[`docs/firebase-setup.md`](docs/firebase-setup.md)**.

### Bước 1: Yêu cầu môi trường & Cấu hình `JAVA_HOME`
- Cần **JDK 17**. Đặt biến môi trường trong PowerShell:
  ```powershell
  $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
  $env:Path = "$env:JAVA_HOME\bin;$env:Path"
  java -version
  ```

### Bước 2: Tạo Firebase Project & Cấu hình
1. Truy cập [Firebase Console](https://console.firebase.google.com/), tạo một dự án mới.
2. Thêm Android App với **Package name chính xác tuyệt đối**: `com.example.studentmgmt`.
3. Tải file `google-services.json` và lưu vào thư mục:
   ```text
   app/google-services.json
   ```
   *(Không đặt ở thư mục gốc repo; xem file mẫu tại [`app/google-services.json.example`](app/google-services.json.example))*.
4. Chỉ bật **Authentication → Email/Password** nếu nhóm chọn tích hợp phương án này; email không phải dữ liệu bắt buộc của tài khoản theo đặc tả.
5. Bật **Firestore Database** ở **Production mode** (chọn location `asia-southeast1`).
6. **Cloud Storage:** Kiểm tra khả năng tạo bucket và yêu cầu thanh toán đang hiển thị trong Firebase Console tại thời điểm cấu hình. Ghi lại tên bucket, location và gói thanh toán thực tế vào [`docs/team-info.md`](docs/team-info.md); không giả định dự án được miễn phí.

### Bước 3: Triển khai Security Rules (Bắt buộc trước khi chạy)
Deploy đồng thời cả Firestore Rules và Storage Rules lên Firebase:
```powershell
npm install -g firebase-tools
firebase login
firebase use --add    # Chọn project vừa tạo
firebase deploy --only firestore,storage
```
*(Hoặc sao chép nội dung [`firestore.rules`](firestore.rules) và [`storage.rules`](storage.rules) vào Firebase Console Rules rồi nhấn Publish)*.

### Bước 4: Build và Kiểm thử
- **Chạy Unit Test tự động:**
  ```powershell
  .\gradlew.bat testDebugUnitTest
  ```
- **Biên dịch Debug APK:**
  ```powershell
  .\gradlew.bat assembleDebug
  ```

### Bước 5: Cấp tài khoản demo trong môi trường tin cậy

Ứng dụng **không tự nâng quyền Admin từ client**. Ba tài khoản dưới đây là thông tin demo dự kiến; chỉ đánh dấu “đã tạo” sau khi cơ chế xác thực thật được triển khai và có bằng chứng.

- **Admin:** `admin` / `admin`
- **Manager:** `manager` / `manager`
- **Employee:** `employee` / `employee`

Email là thông tin bổ sung, có thể để trống. Khi một tài khoản có email, màn hình đăng nhập có thể chấp nhận email đó như bí danh thay cho username. Không commit thông tin đăng nhập của môi trường thật vào repository.

---

## 5. Tài khoản demo dự kiến

| Vai trò | Tên đăng nhập | Mật khẩu | Phạm vi chức năng |
|---|---|---|---|
| **Admin** | `admin` | `admin` | Quản trị tài khoản, phân quyền, xem lịch sử đăng nhập và quản lý dữ liệu. |
| **Manager** | `manager` | `manager` | Quản lý sinh viên, chứng chỉ và nhập/xuất CSV. |
| **Employee** | `employee` | `employee` | Xem thông tin sinh viên và đổi ảnh đại diện cá nhân. |

> Ba mật khẩu trên phục vụ demo theo yêu cầu môn học. Riêng Firebase Authentication Email/Password yêu cầu mật khẩu tối thiểu 6 ký tự, nên cặp `admin/admin` chưa thể cấp trực tiếp bằng provider này nếu không đổi mật khẩu hoặc dùng cơ chế xác thực khác.

---

## 6. Danh mục tài liệu dự án

- **Báo cáo học thuật:** [`docs/final-report.md`](docs/final-report.md)
- **Kịch bản quay Video Demo:** [`docs/demo-script.md`](docs/demo-script.md)
- **Thông tin nhóm & Môi trường bàn giao:** [`docs/team-info.md`](docs/team-info.md)
- **Cài đặt & Kế hoạch 27 Test Cases:** [`docs/test-and-setup.md`](docs/test-and-setup.md)
- **Cẩm nang cấu hình Firebase:** [`docs/firebase-setup.md`](docs/firebase-setup.md)
- **Đặc tả yêu cầu phần mềm (SRS):** [`docs/SRS.md`](docs/SRS.md)
- **Thiết kế cơ sở dữ liệu:** [`docs/firestore-schema.md`](docs/firestore-schema.md)
- **Kiến trúc hệ thống & UI:** [`docs/design.md`](docs/design.md)
- **Hệ thống sơ đồ UML:** [`docs/uml.md`](docs/uml.md)
- **Dữ liệu mẫu kiểm thử CSV:** [`sample-data/`](sample-data/)

---

## 7. Nhóm sinh viên thực hiện

| Vai trò dự án | Thành viên | Họ và tên | Mã số sinh viên (MSSV) | Trách nhiệm chính |
|---|---|---|---|---|
| **Trưởng nhóm** | Thành viên A | Tan (Trần Ngọc Tân) | 52400158 | Auth, User Management, Security Rules, UI Auth/User, Tổng hợp Báo cáo |
| **Thành viên** | Thành viên B | `[HỌ VÀ TÊN THÀNH VIÊN B]` | `[MSSV THÀNH VIÊN B]` | Student & Certificate CRUD, Realtime Sync, CSV Import/Export, Testing & Video |

- **Giảng viên hướng dẫn:** `[HỌ VÀ TÊN GIẢNG VIÊN PHỤ TRÁCH]`  
- **Thông tin chi tiết môi trường nộp bài:** Xem tại [`docs/team-info.md`](docs/team-info.md).
