# Hướng Dẫn Cài Đặt, Vận Hành và Kế Hoạch Kiểm Thử Hệ Thống

> **Dự án:** Ứng dụng Quản lý Thông tin Sinh viên Thời gian Thực (Realtime Student Information Management App)  
> **Package name:** `com.example.studentmgmt`  
> **Mục đích tài liệu:** Hướng dẫn kỹ thuật và phân công kịch bản kiểm thử **để giao việc** cho các thành viên trong nhóm.

---

## 1. Môi Trường Yêu Cầu & Cấu Hình Công Cụ

### 1.1 Yêu cầu hệ thống
- **Hệ điều hành:** Windows 10/11, macOS, hoặc Linux.
- **Android Studio:** Bản Iguana (2023.2.1) trở lên (hỗ trợ Android Gradle Plugin 8.3.2).
- **JDK:** **Java 17 (bắt buộc)**. Dự án biên dịch ở cấp độ Java 17 (`sourceCompatibility` & `targetCompatibility = JavaVersion.VERSION_17`).
- **Android SDK:**
  - `compileSdk`: **34** (Android 14)
  - `targetSdk`: **34**
  - `minSdk`: **26** (Android 8.0 Oreo)
- **Thiết bị chạy:** Thiết bị thật hoặc Máy ảo Android (AVD) chạy Android 8.0 (API 26) đến Android 14 (API 34), có cài đặt Google Play Services.
- **Node.js & npm (Tùy chọn cho CLI):** Node.js LTS (v18+) để sử dụng Firebase CLI.

---

### 1.2 Hướng dẫn cấu hình `JAVA_HOME` về JDK 17 trên Windows

Nếu gặp lỗi `JAVA_HOME is not set` hoặc Java đang trỏ phiên bản cũ (Java 8/11):

#### Cách 1: Thiết lập tạm thời trong PowerShell (phiên làm việc hiện tại)
```powershell
# Trỏ tới JDK 17 tích hợp sẵn trong Android Studio:
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
# Hoặc trỏ tới JDK 17 trong thư mục người dùng:
# $env:JAVA_HOME = "$env:USERPROFILE\.jdks\jbr-17.0.14"

$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# Kiểm tra lại phiên bản:
java -version
```

#### Cách 2: Thiết lập vĩnh viễn trên Windows
1. Nhấn phím `Windows + R`, gõ `sysdm.cpl` và nhấn Enter.
2. Chuyển sang thẻ **Advanced** → Bấm nút **Environment Variables...**.
3. Tại mục **System variables**, nhấn **New...** (hoặc chọn `JAVA_HOME` rồi bấm **Edit...**):
   - **Variable name:** `JAVA_HOME`
   - **Variable value:** `C:\Program Files\Android\Android Studio\jbr` (hoặc đường dẫn cài đặt JDK 17 của máy).
4. Tìm biến `Path` trong **System variables**, bấm **Edit...** → Thêm `%JAVA_HOME%\bin` lên đầu danh sách.
5. Mở cửa sổ Terminal/PowerShell mới và gõ `java -version` để xác nhận.

---

## 2. Thiết Lập Firebase Chi Tiết Từ A đến Z

### 2.1 Tạo Firebase Project
1. Truy cập [Firebase Console](https://console.firebase.google.com/) bằng tài khoản Google.
2. Bấm **Add project** (Tạo dự án mới).
3. Nhập tên dự án (ví dụ: `student-mgmt-midterm`).
4. Bỏ chọn Google Analytics (không bắt buộc) → Nhấn **Create project**.
5. Ghi nhớ **Project ID** hiển thị dưới tên dự án.

---

### 2.2 Đăng ký Android App
1. Tại trang **Project Overview**, bấm vào biểu tượng **Android** để thêm app.
2. Điền thông tin bắt buộc:
   - **Android package name (chính xác tuyệt đối):** `com.example.studentmgmt`
   - **App nickname:** `Student Management`
   - **Debug signing certificate SHA-1:** Không bắt buộc cho Email/Password auth (có thể bỏ trống).
3. Nhấn **Register app**.

---

### 2.3 Tải và đặt file cấu hình `google-services.json`
1. Tải file `google-services.json` vừa được tạo từ Firebase Console.
2. Di chuyển và lưu file vào đúng thư mục sau:
   ```text
   <project-root>/app/google-services.json
   ```
3. **Lưu ý tối quan trọng:**
   - Tuyệt đối không đặt file tại thư mục gốc repository (`<project-root>/google-services.json`).
   - Tuyệt đối không đổi tên file thành `google-services (1).json`.
   - File này chứa thông tin định danh Firebase của bạn và đã được đưa vào `.gitignore` để tránh rò rỉ lên repo công khai.
   - Thư mục `app/` đã có sẵn file mẫu cấu trúc tham khảo: [app/google-services.json.example](../app/google-services.json.example).

---

### 2.4 Bật Firebase Authentication
1. Tại menu bên trái Firebase Console, vào **Build** → **Authentication** → Nhấn **Get started**.
2. Tại thẻ **Sign-in method**, chọn nhà cung cấp **Email/Password**.
3. Gạt công tắc **Enable** ở mục đầu tiên (Email/Password). *Không cần bật Email link (passwordless sign-in)*.
4. Nhấn **Save**.

---

### 2.5 Tạo Cloud Firestore Database ở Production Mode
1. Vào **Build** → **Firestore Database** → Nhấn **Create database**.
2. Chọn vị trí lưu trữ database (Location): Ưu tiên khu vực gần như `asia-southeast1` (Singapore) hoặc `asia-east1`.
3. Tại bước **Secure rules**, chọn **Start in production mode** (chế độ bảo vệ mặc định từ chối mọi truy cập khi chưa áp dụng rules).
4. Nhấn **Create**.
5. Cơ sở dữ liệu mặc định `(default)` được tạo thành công. *Không cần tạo collection bằng tay, ứng dụng sẽ tự động sinh khi chạy.*

---

### 2.6 Triển khai Security Rules (Bắt buộc trước lần chạy đầu tiên)

Dự án áp dụng cơ chế bảo mật phân quyền Role-Based Access Control (RBAC) nghiêm ngặt tại tầng đám mây. Bạn **phải deploy rules trước khi khởi chạy ứng dụng lần đầu** để tính năng seed tài khoản Admin có thể cấp quyền hợp lệ.

#### Cách 1: Sử dụng Firebase CLI (Khuyên dùng)
1. Cài đặt Firebase CLI qua npm:
   ```powershell
   npm install -g firebase-tools
   ```
2. Đăng nhập tài khoản Google:
   ```powershell
   firebase login
   ```
3. Xem danh sách các project trên tài khoản:
   ```powershell
   firebase projects:list
   ```
4. Liên kết dự án tại thư mục gốc:
   ```powershell
   firebase use --add
   ```
   *(Chọn đúng Project ID của bạn và đặt alias là `default`)*.
5. Kiểm tra lại dự án hiện hành:
   ```powershell
   firebase use
   ```
6. Triển khai cả Firestore Rules và Storage Rules lên Firebase Cloud:
   ```powershell
   firebase deploy --only firestore,storage
   ```

#### Cách 2: Sao chép thủ công trên Firebase Console
- **Firestore Rules:**
  1. Mở file [firestore.rules](../firestore.rules) trong dự án.
  2. Vào Firebase Console → **Firestore Database** → Thẻ **Rules**.
  3. Xóa nội dung mặc định, dán toàn bộ nội dung từ file `firestore.rules` vào.
  4. Nhấn nút **Publish**.
- **Storage Rules:**
  1. Mở file [storage.rules](../storage.rules) trong dự án.
  2. Vào Firebase Console → **Storage** → Thẻ **Rules**.
  3. Dán toàn bộ nội dung từ file `storage.rules` vào.
  4. Nhấn nút **Publish**.

---

### 2.7 Lưu ý đặc biệt về Firebase Cloud Storage & Gói cước Blaze

> [!WARNING]
> Theo **[Thông báo chính thức từ Google Firebase (Áp dụng từ tháng 09/2024)](https://firebase.google.com/docs/storage/faqs-storage-changes-announced-sept-2024)**:
> Dịch vụ Cloud Storage for Firebase hiện yêu cầu dự án phải được nâng cấp lên gói **Blaze (Pay-as-you-go)** và liên kết tài khoản Cloud Billing để kích hoạt bucket lưu trữ.
> - **Chi phí thực tế:** Vẫn áp dụng **Hạn mức miễn phí hàng tháng (Always Free Tier)** bao gồm 5 GB dung lượng lưu trữ, 1 GB tải xuống/ngày và 20.000 thao tác tải lên/ngày. Đối với việc chạy demo và chấm bài môn học, chi phí phát sinh là **0 VNĐ**.
> - **Khuyến nghị an toàn:** Hãy đặt **Budget Alert** (ví dụ 1 USD) trên Google Cloud Console để kiểm soát.
> - **Trường hợp chưa kích hoạt Blaze:** Toàn bộ tính năng Quản lý sinh viên, Chứng chỉ, Đăng nhập, Phân quyền RBAC, Import/Export CSV trên Firestore vẫn hoạt động 100% bình thường; chỉ riêng tính năng tải ảnh đại diện cá nhân (Avatar upload) sẽ báo lỗi quyền lưu trữ.

---

## 3. Lệnh Build, Chạy & Kiểm Tra Khởi Tạo

### 3.1 Lệnh Build & Chạy qua Command Line (CLI)

Mở PowerShell tại thư mục gốc của dự án:

1. **Chạy kiểm thử đơn vị tự động (Unit Tests):**
   ```powershell
   .\gradlew.bat testDebugUnitTest
   ```

2. **Biên dịch bộ cài đặt Debug APK:**
   ```powershell
   .\gradlew.bat assembleDebug
   ```

3. **Cài đặt trực tiếp lên thiết bị/máy ảo đang kết nối:**
   ```powershell
   .\gradlew.bat installDebug
   ```

---

### 3.2 Khởi chạy lần đầu và Cơ chế Tự động Khởi tạo Admin (Seed Admin)

1. Mở ứng dụng lần đầu trên thiết bị/máy ảo.
2. Dịch vụ ngầm `AdminSeedService` sẽ tự động kiểm tra xem tài khoản Quản trị viên hệ thống đã tồn tại hay chưa.
3. Nếu chưa có, hệ thống tự động:
   - Đăng ký tài khoản trên Firebase Authentication với Email: `admin@student.app` | Mật khẩu: `Admin@123456`.
   - Tạo hồ sơ Quản trị viên trong Firestore collection `users/{adminUid}` với `role: "admin"`, `status: "Normal"`, `name: "Quản trị viên"`.
   - Đăng xuất phiên seed an toàn để người dùng đăng nhập bằng giao diện chính thức.

---

### 3.3 Cách kiểm tra xác minh Admin trên Firebase Console

Để chắc chắn hệ thống backend đã sẵn sàng:
1. **Kiểm tra Authentication:**
   - Vào **Firebase Console** → **Authentication** → Thẻ **Users**.
   - Phải thấy xuất hiện tài khoản `admin@student.app` với User UID tương ứng.
2. **Kiểm tra Cloud Firestore:**
   - Vào **Firestore Database** → Xem collection `users`.
   - Phải có 1 document với Document ID chính là UID của Admin:
     ```json
     {
       "uid": "<admin_uid>",
       "email": "admin@student.app",
       "name": "Quản trị viên",
       "role": "admin",
       "status": "Normal",
       "phone": "0901234567",
       "age": 30,
       "createdAt": "<Timestamp>"
     }
     ```

---

## 4. Tài Khoản Kiểm Thử Dự Kiến

| Vai trò | Tên đăng nhập (Username) | Mật khẩu | Mục đích kiểm thử |
|---|---|---|---|
| **Admin** | `admin` | `admin` | Quản lý người dùng, khóa/mở khóa tài khoản, xem lịch sử đăng nhập, toàn quyền sinh viên. |
| **Manager** | `manager` | `manager` | Quản lý sinh viên, chứng chỉ, tìm kiếm, sắp xếp, import/export CSV. Không vào được Quản lý tài khoản. |
| **Employee** | `employee` | `employee` | Chỉ xem danh sách sinh viên & chi tiết chứng chỉ; chỉnh sửa ảnh đại diện cá nhân; các nút thêm/sửa/xóa bị ẩn/chặn. |

---

## 5. Bảng Phân Công 27 Kịch Bản Kiểm Thử (Test Cases Plan)

> **Ghi chú phân công giao việc:** Cột "Người test" được chỉ định theo nguyên tắc kiểm thử chéo độc lập (Thành viên B test code Auth của A; Thành viên A test code Student của B). Kết quả thực tế ban đầu được để trống `– (Chờ test)` để thành viên thực hiện khi hoàn thành tính năng.

| Mã TC | Yêu cầu (FR) | Vai trò | Bước thực hiện | Kết quả mong đợi | Kết quả thực tế | Người test | Ngày test | Ghi chú / Tiêu chí nghiệm thu |
|---|---|---|---|---|:---:|:---:|:---:|---|
| **TC-01** | FR-ACC-01 | Tất cả | Nhập email/mật khẩu đúng → Nhấn Đăng nhập | Vào màn hình chính, thanh điều hướng hiện đúng mục theo vai trò | – (Chờ test) | Thành viên B | – | Chuyển hướng tức thì, lưu phiên Firebase Auth |
| **TC-02** | FR-ACC-01 | Tất cả | Nhập mật khẩu sai | Báo lỗi "Sai thông tin đăng nhập", giữ nguyên màn hình | – (Chờ test) | Thành viên B | – | Toast & TextInputLayout báo lỗi trực quan |
| **TC-03** | FR-ACC-01 | Employee | Đăng nhập bằng tài khoản có trạng thái `Locked` | Từ chối truy cập, hiển thị thông báo "Tài khoản của bạn đã bị khóa" | – (Chờ test) | Thành viên B | – | Đăng xuất Auth ngay, không nạp MainActivity |
| **TC-04** | FR-ACC-02 | Employee | Vào Cá nhân → Chọn ảnh từ thư viện → Lưu | Tải lên Firebase Storage, cập nhật avatarUrl và hiển thị ngay | – (Chờ test) | Thành viên B | – | File lưu tại `avatars/{uid}.jpg`, Glide load mượt |
| **TC-05** | FR-USR-02 | Admin | Thêm người dùng mới với Tuổi = 17 hoặc 65 | Báo lỗi validation "Tuổi người dùng phải từ 18 đến 60" | – (Chờ test) | Thành viên B | – | Chặn tại Client và Security Rules bảo vệ |
| **TC-06** | FR-USR-02 | Admin | Thêm người dùng với SĐT 9 chữ số hoặc có chữ cái | Báo lỗi "Số điện thoại phải gồm 10 chữ số hợp lệ" | – (Chờ test) | Thành viên B | – | Regex `^0[0-9]{9}$` kiểm tra chuẩn |
| **TC-07** | FR-USR-02 | Manager | Cố gắng gọi chức năng hoặc API thêm người dùng | Bị chặn: Giao diện không có nút; Rules trả `PERMISSION_DENIED` | – (Chờ test) | Thành viên B | – | Kiểm thử chéo: Rules từ chối ghi vào `users` |
| **TC-08** | FR-USR-05 | Admin | Chuyển trạng thái Manager sang `Locked` | Tài khoản Manager bị đăng xuất hoặc không thể đăng nhập lại | – (Chờ test) | Thành viên B | – | Kiểm tra status thời gian thực chặn đăng nhập |
| **TC-09** | FR-STU-02 | Manager | Thêm sinh viên mới đầy đủ thông tin hợp lệ | Lưu thành công vào Firestore, xuất hiện ngay trên danh sách | – (Chờ test) | Thành viên A | – | ID sinh viên dùng chính MSSV làm Document ID |
| **TC-10** | FR-STU-02 | Employee | Mở màn hình danh sách sinh viên | Nút FAB "Thêm sinh viên" bị ẩn; thao tác sửa/xóa bị khóa | – (Chờ test) | Thành viên A | – | Kiểm thử chéo: RBAC ẩn các nút chức năng ghi |
| **TC-11** | FR-STU-05 | Tất cả | Gõ từ khóa tìm kiếm "Nguyễn" vào ô tìm kiếm | Lọc tức thì tất cả sinh viên có họ/tên chứa "Nguyễn" | – (Chờ test) | Thành viên A | – | Lọc realtime trên client theo danh sách snapshot |
| **TC-12** | FR-STU-05 | Tất cả | Tìm kiếm tên có dấu tiếng Việt "Thị" | Lọc chính xác các sinh viên có chữ "Thị", không lỗi font | – (Chờ test) | Thành viên A | – | Hỗ trợ Unicode Tiếng Việt chuẩn xác |
| **TC-13** | FR-STU-06 | Tất cả | Chọn sắp xếp theo tên từ A–Z | Danh sách sắp xếp đúng bảng chữ cái tiếng Việt | – (Chờ test) | Thành viên A | – | Sử dụng Collator tiếng Việt chuẩn |
| **TC-14** | FR-CER-02 | Manager | Mở chi tiết SV → Bấm Thêm chứng chỉ → Nhập thông tin | Lưu thành công vào subcollection `students/{id}/certificates` | – (Chờ test) | Thành viên A | – | Quan hệ 1-N đúng thiết kế kiến trúc |
| **TC-15** | FR-CER-04 | Employee | Truy cập chi tiết sinh viên xem danh sách chứng chỉ | Chỉ xem được danh sách chứng chỉ, không có nút Xóa/Sửa | – (Chờ test) | Thành viên A | – | Kiểm thử chéo: Employee không có quyền sửa chứng chỉ |
| **TC-16** | FR-IO-01 | Manager | Import file mẫu `sample-data/students.csv` (20 SV) | Toàn bộ 20 sinh viên được thêm vào Firestore nguyên vẹn | – (Chờ test) | Thành viên A | – | Sử dụng WriteBatch atomic, hiển thị tiến độ |
| **TC-17** | FR-IO-01 | Manager | Import file `sample-data/students-invalid.csv` | Bỏ qua dòng lỗi (thiếu MSSV), import dòng đúng, có báo cáo | – (Chờ test) | Thành viên A | – | Thông báo chi tiết số dòng thành công và số dòng lỗi |
| **TC-18** | FR-IO-01 | Manager | Chọn file không có đuôi `.csv` (ví dụ `.txt`, `.jpg`) | Báo lỗi định dạng không hỗ trợ, không thực hiện import | – (Chờ test) | Thành viên A | – | Kiểm tra MIME type và extension trước khi đọc |
| **TC-19** | FR-IO-02 | Admin | Bấm nút Export danh sách sinh viên ra file CSV | Xuất file CSV qua Storage Access Framework, đủ cột và dòng | – (Chờ test) | Thành viên B | – | Tệp CSV mã hóa UTF-8 kèm BOM mở Excel không lỗi font |
| **TC-20** | FR-IO-02 | Employee | Kiểm tra màn hình Sinh viên với tài khoản Employee | Mục Import/Export bị ẩn hoàn toàn khỏi menu | – (Chờ test) | Thành viên B | – | Employee không thấy giao diện Import/Export |
| **TC-21** | FR-STU-01 | Manager | Thao tác trên TB1 (Thêm/Sửa SV) → Quan sát TB2 | TB2 tự động cập nhật danh sách hiển thị trong < 2 giây | – (Chờ test) | Cả hai | – | Firestore `addSnapshotListener` đồng bộ tức thì |
| **TC-22** | FR-ACC-01 | Admin | Đăng nhập thành công vào hệ thống | Tạo bản ghi mới trong subcollection `loginHistory` của Admin | – (Chờ test) | Thành viên B | – | Ghi nhận đúng Server Timestamp và tên model thiết bị |
| **TC-23** | FR-USR-06 | Admin | Chọn xem lịch sử đăng nhập của một tài khoản Manager | Hiển thị danh sách các mốc thời gian và thiết bị đã đăng nhập | – (Chờ test) | Thành viên B | – | Hiển thị bằng BottomSheet / Dialog RecyclerView |
| **TC-24** | FR-IO-01 | Manager | Import CSV chứa tên có dấu tiếng Việt phức tạp | Dữ liệu trên Firestore và giao diện hiển thị chuẩn xác 100% | – (Chờ test) | Thành viên A | – | Hỗ trợ UTF-8 chuẩn xác, không bị lỗi mã hóa |
| **TC-25** | FR-STU-04 | Manager | Xóa một sinh viên đã có 3 chứng chỉ | Cả document sinh viên và 3 chứng chỉ con đều bị xóa sạch | – (Chờ test) | Thành viên A | – | Xóa đệ quy subcollection trước khi xóa cha |
| **TC-26** | FR-ACC-04 | – | Cài mới ứng dụng hoàn toàn, chạy lần đầu | Tự động tạo Admin `admin@student.app`, đăng nhập thành công | – (Chờ test) | Cả hai | – | AdminSeedService hoàn thành khởi tạo trong 1.5s |
| **TC-27** | FR-IO-03 | Manager | Import `sample-data/certificates.csv` sau khi có SV | Chứng chỉ tự động gán đúng vào từng sinh viên theo MSSV | – (Chờ test) | Thành viên A | – | Tra cứu MSSV hợp lệ trước khi ghi subcollection |

---

## 6. Tiêu Chí Nghiệm Thu Nâng Cao (Acceptance Criteria)

### 6.1 Nghiệm thu 1: Cơ chế Seed Admin & Phân quyền Role-Based
- **Mô tả:** Lần đầu chạy app, `AdminSeedService` kích hoạt `checkAndSeedAdmin()`.
- **Tiêu chí đạt:**
  1. Firebase Authentication có user `admin@student.app`.
  2. Document `users/{adminUid}` có trường `role: "admin"`.
  3. Giao diện Admin hiển thị đầy đủ 4 tab/mục: *Trang chủ, Quản lý tài khoản, Quản lý sinh viên, Cá nhân*.

### 6.2 Nghiệm thu 2: Chặn tài khoản bị khóa (Locked Account)
- **Mô tả:** Admin chọn một User → Bấm đổi trạng thái thành `Locked`.
- **Tiêu chí đạt:**
  1. Người dùng đó khi mở app nhập đúng mật khẩu, hệ thống xác thực Auth thành công nhưng lập tức đọc `users/{uid}.status`.
  2. Do `status == "Locked"`, app lập tức gọi `FirebaseAuth.getInstance().signOut()`.
  3. Hiển thị Dialog cảnh báo: *"Tài khoản đã bị khóa. Vui lòng liên hệ Quản trị viên."* và giữ tại màn hình Login.

### 6.3 Nghiệm thu 3: Đồng bộ thời gian thực trên 2 thiết bị (Realtime Sync)
- **Mô tả:** Thiết bị A (Pixel Emulator) và Thiết bị B (Android Tablet/Device) cùng mở màn hình Danh sách Sinh viên.
- **Tiêu chí đạt:**
  1. Trên Thiết bị A, thêm hoặc sửa một sinh viên.
  2. Trong vòng **dưới 2 giây**, danh sách trên Thiết bị B tự động cập nhật mà không cần vuốt màn hình để Refresh.

### 6.4 Nghiệm thu 4: Import dữ liệu số lượng lớn (Batch Write > 500 dòng)
- **Mô tả:** Thử nghiệm import danh sách dữ liệu sinh viên quy mô lớn.
- **Tiêu chí đạt:**
  1. Xử lý chia nhỏ thành từng chunk 400 records/batch để không vượt ngưỡng 500 của Firestore.
  2. Tiến trình import chạy atomic, mượt mà, có thanh tiến độ (Progress Dialog).

### 6.5 Nghiệm thu 5: Xóa sinh viên kèm xóa tầng chứng chỉ (Cascade Deletion)
- **Mô tả:** Xóa sinh viên đang sở hữu nhiều chứng chỉ trong subcollection.
- **Tiêu chí đạt:**
  1. Xóa sạch toàn bộ documents trong subcollection `certificates` trước khi xóa document sinh viên cha.
  2. Không để lại tài liệu mồ côi (orphan documents) trên Firestore.

### 6.6 Nghiệm thu 6: Cơ chế Offline Persistence & Kết nối mạng
- **Mô tả:** Tắt kết nối mạng khi đang xem danh sách sinh viên.
- **Tiêu chí đạt:**
  1. Người dùng vẫn xem được danh sách và tìm kiếm bình thường nhờ bộ nhớ đệm Firestore SDK Cache.
  2. Giao diện hiển thị biểu tượng thông báo trạng thái ngoại tuyến ("Offline").
  3. Khi có mạng trở lại, mọi thay đổi tự động đồng bộ lên máy chủ đám mây.
