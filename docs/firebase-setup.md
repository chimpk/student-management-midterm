# Hướng dẫn thiết lập Firebase

> **Trạng thái:** Tài liệu thiết lập dự kiến cho dự án môn học. Chỉ đánh dấu hoàn tất sau khi đã thao tác và lưu bằng chứng trên đúng Firebase Project.

## 1. Dịch vụ dự kiến

| Nhu cầu | Dịch vụ | Đường dẫn |
|---|---|---|
| Đăng nhập | Cơ chế xác thực do nhóm chọn | Username, mật khẩu, UID; email tùy chọn |
| Người dùng | Cloud Firestore | `users/{uid}` |
| Lịch sử đăng nhập | Cloud Firestore | `users/{uid}/loginHistory/{logId}` |
| Sinh viên | Cloud Firestore | `students/{studentId}` |
| Chứng chỉ | Cloud Firestore | `students/{studentId}/certificates/{certId}` |
| Ảnh đại diện | Cloud Storage | `avatars/{uid}.jpg` |

Ứng dụng không dùng Room/SQLite làm dữ liệu thay thế. Nếu Firebase chưa cấu hình, ứng dụng phải báo lỗi rõ ràng.

## 2. Điều kiện chuẩn bị

- Android Studio Iguana 2023.2.1 hoặc mới hơn.
- JDK 17, Android SDK 34 và thiết bị Android API 26 trở lên.
- Tài khoản Google có quyền quản lý Firebase Project.
- Node.js/npm nếu triển khai bằng Firebase CLI.
- Billing chỉ khi tính năng Storage tại thời điểm thiết lập yêu cầu.

Kiểm tra:

```powershell
java -version
$env:JAVA_HOME
.\gradlew.bat --version
node --version
npm --version
```

## 3. Tạo và ghi nhận Firebase Project

1. Truy cập <https://console.firebase.google.com/>.
2. Chọn **Tạo dự án**, nhập tên dự án và quyết định có bật Analytics hay không.
3. Chọn vị trí Firestore phù hợp; vị trí khó thay đổi sau khi tạo.
4. Ghi Project ID, location và Storage Bucket thực tế vào [Thông tin nhóm](team-info.md).
5. Không dùng dự án đang chứa dữ liệu quan trọng hoặc dữ liệu cá nhân thật.

## 4. Đăng ký ứng dụng Android

| Trường | Giá trị |
|---|---|
| Tên gói Android | `com.example.studentmgmt` |
| Tên gợi nhớ | Tùy chọn |
| SHA-1 debug | Tùy cơ chế xác thực được chọn |

Tải `google-services.json` và đặt tại:

```text
<project-root>/app/google-services.json
```

Không đổi tên, không đặt ở thư mục gốc và không commit file cấu hình thật lên repository công khai. File `app/google-services.json.example` chỉ là mẫu.

## 5. Chọn Authentication và cấp tài khoản demo an toàn

1. Giao diện ứng dụng nhận **username/mật khẩu**. Email chỉ là thông tin tùy chọn và có thể dùng làm bí danh đăng nhập khi tồn tại.
2. Chọn cơ chế xác thực có thể phát hành UID/token tin cậy. Firebase Email/Password là một lựa chọn nhưng không hỗ trợ username trực tiếp và yêu cầu mật khẩu tối thiểu 6 ký tự.
3. Chuẩn bị ba tài khoản từ **môi trường máy chủ đáng tin cậy**:

```text
Admin: admin / admin
Manager: manager / manager
Employee: employee / employee
```

4. Sao chép từng UID được cấp.
5. Tạo document `users/{uid}` tương ứng trong Firestore:

```text
uid: <trùng document ID>
username: admin
email: <tùy chọn>
name: Quản trị viên
age: 30
phone: 0901234567
status: Normal
role: admin
createdAt: <timestamp máy chủ>
```

**Không tạo hoặc seed quyền Admin từ ứng dụng Android.** Client không phải môi trường tin cậy để cấp vai trò cao nhất. Ba tài khoản demo (`admin/admin@123456`, `manager/manager@123456`, `employee/employee@123456`) phải được cấp từ Firebase Console hoặc môi trường tin cậy khác.

## 6. Tạo Firestore và triển khai rules

1. Vào **Firestore Database → Tạo cơ sở dữ liệu**.
2. Chọn chế độ production và location đã thống nhất.
3. Kiểm tra nội dung `firestore.rules` trước khi publish.
4. Triển khai bằng Console hoặc CLI.

```powershell
npm install -g firebase-tools
firebase login
firebase use --add
firebase use
firebase deploy --only firestore
```

Lệnh `firebase use` phải trả đúng Project ID trước mỗi lần deploy. Việc có file rules trong repository chưa chứng minh rules đã được triển khai thành công.

## 7. Thiết lập Storage nếu cần

1. Kiểm tra yêu cầu billing hiện hành trên Firebase Console.
2. Tạo bucket, ghi chính xác tên bucket vào `team-info.md`.
3. Thiết lập cảnh báo ngân sách nếu dùng gói Blaze.
4. Kiểm tra và publish `storage.rules`.

```powershell
firebase deploy --only storage
```

Nếu chưa bật Storage, phải ghi rõ tính năng ảnh đại diện chưa kiểm thử; không tuyên bố ứng dụng vẫn hoạt động đầy đủ.

## 8. Build và chạy kiểm tra

```powershell
.\gradlew.bat clean
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

Nếu tác vụ test báo `NO-SOURCE`, điều đó có nghĩa chưa có test để chạy, không phải test đã đạt. Chỉ ghi build thành công khi lệnh vừa chạy trả mã thoát 0.

## 9. Xác minh thủ công

- Hệ thống xác thực có đủ `admin`, `manager`, `employee` và UID khớp document.
- Mỗi `users/{uid}` có `username`, `role`, `status = Normal`; email có thể vắng mặt.
- Rules đã publish trên đúng Project ID.
- Sau khi chức năng tồn tại, kiểm tra login history, sinh viên, chứng chỉ và avatar đúng đường dẫn.
- Lưu ảnh chụp màn hình có ngày kiểm thử; che thông tin nhạy cảm.

## 10. Nạp dữ liệu demo dự kiến

1. Kiểm tra MSSV đúng cấu trúc `KYYTSSSS`, ví dụ `524H0123`.
2. Kiểm tra mã lớp gồm đúng 8 ký tự chữ hoa hoặc số.
3. Nhập sinh viên trước, chứng chỉ sau.
4. Chia mỗi batch tối đa **400 thao tác ghi**.
5. Dùng file dữ liệu sai để kiểm tra báo lỗi theo dòng.

Chỉ thực hiện các bước này sau khi chức năng import đã có trong mã nguồn.

## 11. Xử lý lỗi thường gặp

| Triệu chứng | Nguyên nhân thường gặp | Cách xử lý |
|---|---|---|
| Thiếu cấu hình Firebase | JSON thiếu hoặc sai vị trí | Đặt file thật tại `app/google-services.json` và build lại |
| Không khớp tên gói | JSON thuộc app khác | Đăng ký lại `com.example.studentmgmt` |
| `PERMISSION_DENIED` | Rules chưa deploy, sai project hoặc sai quyền | Kiểm tra `firebase use`, UID và document người dùng |
| Auth có Admin nhưng thiếu hồ sơ | Chưa tạo `users/{uid}` | Tạo document từ Console/môi trường tin cậy |
| Storage trả 402/403 | Chưa có bucket/billing/rules | Kiểm tra cấu hình Storage hiện hành |
| Gradle không chạy | JDK/JAVA_HOME sai | Cấu hình JDK 17 và mở terminal mới |

## 12. Checklist hoàn tất

- [ ] Điền Firebase Project ID, location và Storage Bucket.
- [ ] `google-services.json` đúng vị trí và không bị commit.
- [ ] Cơ chế xác thực username/password đã được chọn và mô tả đúng.
- [ ] Đủ ba tài khoản `admin`, `manager`, `employee` được cấp ngoài client.
- [ ] Firestore được tạo ở production mode.
- [ ] Rules đã deploy trên đúng Project ID và có bằng chứng.
- [ ] Storage đã cấu hình hoặc ghi rõ chưa sử dụng.
- [ ] Test thực tế có kết quả; không nhầm `NO-SOURCE` với đạt.
- [ ] Dữ liệu demo không chứa thông tin cá nhân thật.

## 13. Tài liệu chính thức

- Thiết lập Android: <https://firebase.google.com/docs/android/setup>
- Firebase Email/Password (chỉ dùng khi phù hợp): <https://firebase.google.com/docs/auth/android/password-auth>
- Firestore: <https://firebase.google.com/docs/firestore/quickstart>
- Firebase CLI: <https://firebase.google.com/docs/cli>
- Triển khai Rules: <https://firebase.google.com/docs/rules/manage-deploy>
- Thay đổi Storage: <https://firebase.google.com/docs/storage/faqs-storage-changes-announced-sept-2024>
