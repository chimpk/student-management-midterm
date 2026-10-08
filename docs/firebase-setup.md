# Thiết Lập Firebase Cloud — Hướng Dẫn Từ A đến Z

> Tài liệu này là nguồn hướng dẫn vận hành Firebase chính thức của dự án. Người mới phải có thể tạo một Firebase project trống, kết nối ứng dụng, triển khai rules và xác minh toàn bộ luồng mà không cần đọc source code.

## 1. Kiến trúc dịch vụ

| Nhu cầu | Dịch vụ | Dữ liệu |
|---|---|---|
| Đăng nhập | Firebase Authentication | Email/mật khẩu và Firebase Auth UID |
| Người dùng | Cloud Firestore | `users/{uid}` |
| Lịch sử đăng nhập | Cloud Firestore | `users/{uid}/loginHistory/{logId}` |
| Sinh viên | Cloud Firestore | `students/{studentId}` |
| Chứng chỉ | Cloud Firestore | `students/{studentId}/certificates/{certId}` |
| Ảnh đại diện | Cloud Storage for Firebase | `avatars/{uid}.jpg` |
| Offline | Firestore Android SDK | Cache do SDK quản lý, không phải database local riêng |

Ứng dụng không dùng Room/SQLite và không có tài khoản hoặc database local dự phòng. Thiếu cấu hình Firebase thì ứng dụng phải báo lỗi, không được tự chuyển sang dữ liệu giả.

## 2. Điều kiện chuẩn bị

- Tài khoản Google có quyền tạo Firebase project.
- Android Studio Iguana 2023.2.1 hoặc mới hơn.
- JDK 17 và biến môi trường `JAVA_HOME` trỏ đúng JDK.
- Android SDK 34; thiết bị/emulator Android 8.0 (API 26) trở lên.
- Node.js/npm nếu muốn deploy bằng Firebase CLI.
- Một phương thức thanh toán chỉ khi cần Firebase Storage. Firestore và Email/Password Authentication có thể dùng hạn mức miễn phí.

Kiểm tra công cụ trên Windows:

```powershell
java -version
$env:JAVA_HOME
.\gradlew.bat --version
node --version
npm --version
```

Kết quả Java phải là phiên bản 17. Không dùng Java 8 cho project này.

## 3. Tạo Firebase project

1. Truy cập <https://console.firebase.google.com/>.
2. Chọn **Create a project**.
3. Đặt tên, ví dụ `student-management-midterm`.
4. Ghi lại **Project ID**; Project ID không đổi được sau khi tạo.
5. Google Analytics không bắt buộc cho bài này.
6. Không dùng chung Firebase project production cá nhân có dữ liệu quan trọng.

### Chọn location

- Location của Firestore gần như không thể đổi sau khi tạo database.
- Với bài demo, chọn một location rồi ghi lại trong bảng môi trường kiểm thử.
- Nếu dùng Storage và muốn tận dụng Google Cloud Storage Always Free, kiểm tra location được Firebase công bố hỗ trợ tại thời điểm tạo bucket.

## 4. Đăng ký Android app

Trong Firebase Console, chọn **Project overview → Add app → Android**.

| Trường | Giá trị |
|---|---|
| Android package name | `com.example.studentmgmt` |
| App nickname | Tùy chọn, ví dụ `Student Mgmt Android` |
| Debug signing SHA-1 | Không bắt buộc cho Email/Password |

Package name phải khớp tuyệt đối với `applicationId` trong `app/build.gradle.kts`.

Tải `google-services.json` và đặt đúng vị trí:

```text
<project-root>/app/google-services.json
```

Không:

- Đổi tên thành `google-services (1).json`.
- Đặt ở thư mục gốc repository.
- Thay bằng `google-services.json.example`.
- Commit file thật vào repository công khai.

File mẫu chỉ minh họa cấu trúc: `app/google-services.json.example`.

## 5. Bật Firebase Authentication

1. Firebase Console → **Build → Authentication → Get started**.
2. **Sign-in method → Email/Password**.
3. Bật **Email/Password**, không cần bật Email link.
4. Nhấn **Save**.

Ứng dụng tự seed tài khoản Admin khi chạy lần đầu:

```text
Email: admin@student.app
Password: Admin@123456
```

Manager và Employee phải được Admin tạo trong ứng dụng. Không tự tạo hai tài khoản này bằng nút Add user trong Firebase Console nếu muốn kiểm thử đúng luồng FR-USR-02.

## 6. Tạo Cloud Firestore

1. Firebase Console → **Build → Firestore Database**.
2. Chọn **Create database**.
3. Chọn **Production mode**.
4. Chọn location đã thống nhất.
5. Hoàn tất tạo database mặc định `(default)`.

Không cần tạo collection thủ công. Ứng dụng tạo collection/document khi seed hoặc thao tác CRUD.

## 7. Triển khai Firestore Rules

Phải triển khai rules **trước lần chạy đầu tiên** để luồng seed Admin được phép tạo đúng document và các role bị giới hạn đúng thiết kế.

### Cách A — Firebase Console

1. Firestore Database → **Rules**.
2. Mở `firestore.rules` trong repository.
3. Sao chép toàn bộ nội dung vào editor.
4. Nhấn **Publish**.

### Cách B — Firebase CLI

Cài và đăng nhập:

```powershell
npm install -g firebase-tools
firebase login
firebase projects:list
```

Tại thư mục gốc project:

```powershell
firebase use --add
```

Chọn Project ID vừa tạo và đặt alias `default`. Lệnh này sinh `.firebaserc`. Trước khi deploy luôn kiểm tra:

```powershell
firebase use
```

Deploy Firestore rules và index:

```powershell
firebase deploy --only firestore
```

`firebase.json`, `firestore.rules` và `firestore.indexes.json` đã nằm trong repository. CLI deploy sẽ ghi đè rules trên Console bằng bản trong source code.

## 8. Bật Cloud Storage cho avatar

Cloud Storage for Firebase hiện yêu cầu project dùng gói **Blaze – pay as you go** và liên kết Cloud Billing. Hạn mức miễn phí vẫn có thể áp dụng; với demo nhỏ chi phí thường bằng 0 nếu không vượt quota, nhưng budget alert không phải hard spending cap.

1. Firebase Console → **Build → Storage → Get started**.
2. Nâng cấp Blaze khi Console yêu cầu.
3. Liên kết Cloud Billing account.
4. Thiết lập budget alert ở Google Cloud Billing.
5. Tạo default bucket và ghi lại tên bucket.
6. Storage → **Rules** → publish nội dung `storage.rules`.

Deploy bằng CLI:

```powershell
firebase deploy --only storage
```

Deploy cả Firestore và Storage:

```powershell
firebase deploy --only firestore,storage
```

Nếu chưa bật billing, ứng dụng vẫn dùng được Authentication và Firestore; riêng đổi avatar sẽ nhận lỗi Storage 402/403.

## 9. Build và chạy lần đầu

Đặt JDK 17 cho phiên PowerShell hiện tại nếu cần:

```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

Sau đó:

```powershell
.\gradlew.bat clean
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

Chạy ứng dụng bằng Android Studio hoặc cài APK debug. Trong lần mở đầu:

1. `LoginActivity` gọi `AdminSeedService`.
2. Firebase Authentication tạo `admin@student.app` nếu chưa tồn tại.
3. Firestore tạo `users/{adminUid}` với `role=admin`, `status=Normal`.
4. App đăng xuất phiên seed và hiển thị màn hình đăng nhập.
5. Đăng nhập bằng tài khoản Admin mặc định.

## 10. Xác minh trên Firebase Console

### Authentication

Authentication → Users phải có:

```text
admin@student.app
```

### Firestore

Document Admin phải có tối thiểu:

```text
users/{uid}
  uid: <trùng document ID>
  email: admin@student.app
  name: Quản trị viên
  age: 30
  phone: 0901234567
  status: Normal
  role: admin
  createdAt: timestamp
```

Sau một lần đăng nhập thành công phải có:

```text
users/{uid}/loginHistory/{logId}
  timestamp: server timestamp
  device: <hãng và model thiết bị>
```

Sau khi thêm sinh viên/chứng chỉ:

```text
students/{studentId}
students/{studentId}/certificates/{certId}
```

Certificate document chỉ chứa `certName`, `issueDate`, `issuedBy`; `certId` và `studentId` được suy ra từ đường dẫn, không lưu thừa.

### Storage

Sau khi đổi avatar phải có:

```text
avatars/{uid}.jpg
```

và `users/{uid}.avatarUrl` phải là download URL của file vừa tải lên.

## 11. Nạp dữ liệu demo

1. Đăng nhập Admin hoặc Manager.
2. Mở Import/Export.
3. Import `sample-data/students.csv` trước.
4. Import `sample-data/certificates.csv` sau.
5. Dùng `students-invalid.csv` để kiểm thử báo lỗi từng dòng.

Không import certificate trước student vì certificate phải tham chiếu một `studentId` đang tồn tại.

## 12. Khôi phục các lỗi setup thường gặp

| Triệu chứng | Nguyên nhân thường gặp | Cách xử lý |
|---|---|---|
| `Firebase chưa được cấu hình` | Thiếu/sai vị trí JSON | Đặt file thật ở `app/google-services.json`, Sync Gradle và rebuild |
| `No matching client found for package name` | Package trong JSON không khớp | Đăng ký Android app với `com.example.studentmgmt`, tải lại JSON |
| `PERMISSION_DENIED` khi seed | Chưa deploy rules hoặc nhầm project | Chạy `firebase use`, deploy `firestore.rules`, xóa cài đặt app rồi thử lại |
| Auth có Admin nhưng Firestore thiếu document | Lần seed trước tạo Auth thành công nhưng ghi Firestore thất bại | Trong Console, tạo document `users/{uid}` đúng schema ở mục 10 hoặc xóa Auth Admin rồi chạy seed lại trên project demo sạch |
| Đăng nhập báo hồ sơ không tồn tại | Có Auth user nhưng thiếu `users/{uid}` | Tạo document tương ứng hoặc xóa/tạo lại user qua đúng luồng ứng dụng |
| Avatar trả 402/403 | Storage chưa có Blaze/bucket/rules | Bật Blaze, tạo bucket và deploy `storage.rules` |
| Storage `unauthorized` | UID/tên file hoặc rules sai | Kiểm tra file phải là `avatars/{uid}.jpg`, user đang đăng nhập và rules đã publish |
| CLI deploy nhầm project | Alias đang trỏ project khác | Chạy `firebase use` trước mọi lần deploy |
| Gradle không chạy | Không có JDK 17/JAVA_HOME | Cấu hình JDK 17, mở terminal mới và chạy `java -version` |
| Emulator không có Google Play services | Image emulator không hỗ trợ Firebase SDK đầy đủ | Tạo AVD có nhãn Google APIs/Google Play |

## 13. Bảo mật và kiểm soát chi phí

- Không commit `google-services.json`, service-account key hoặc file billing.
- Không dùng Test mode cho Firestore/Storage khi demo chính thức.
- Luôn deploy rules từ source code đã review.
- Storage rules hiện giới hạn file avatar dưới 5 MB và MIME `image/*`.
- Bật budget alert nếu dùng Blaze.
- Không để Firebase project demo chứa dữ liệu cá nhân thật.
- Sau khi nộp bài, đổi mật khẩu Admin hoặc xóa Firebase project nếu không còn dùng.

## 14. Checklist Firebase hoàn tất

- [ ] Android app đã đăng ký đúng package `com.example.studentmgmt`.
- [ ] `app/google-services.json` tồn tại trên máy chạy nhưng không bị commit công khai.
- [ ] Email/Password Authentication đã bật.
- [ ] Firestore `(default)` đã tạo ở Production mode.
- [ ] `firestore.rules` đã deploy đúng project.
- [ ] Storage bucket đã tạo và `storage.rules` đã deploy, hoặc đã ghi rõ phạm vi chưa bật Storage.
- [ ] Admin seed thành công trong Auth và Firestore.
- [ ] Login history ghi được server timestamp.
- [ ] CRUD student/certificate chạy đúng role.
- [ ] Avatar upload đúng `avatars/{uid}.jpg`.
- [ ] `firebase use` trả đúng Project ID.
- [ ] Budget alert đã bật nếu project dùng Blaze.

## 15. Tài liệu chính thức

- Android setup: <https://firebase.google.com/docs/android/setup>
- Email/password Auth: <https://firebase.google.com/docs/auth/android/password-auth>
- Firestore quickstart: <https://firebase.google.com/docs/firestore/quickstart>
- Firebase CLI: <https://firebase.google.com/docs/cli>
- Deploy Security Rules: <https://firebase.google.com/docs/rules/manage-deploy>
- Firestore pricing: <https://firebase.google.com/docs/firestore/pricing>
- Storage billing requirement: <https://firebase.google.com/docs/storage/faqs-storage-changes-announced-sept-2024>
