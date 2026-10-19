# Hướng dẫn Thiết lập và Dùng chung Firebase (Firebase Setup Guide)

> **Dự án:** Ứng dụng di động quản lý thông tin sinh viên thời gian thực
>
> **Mã học phần:** 503074 | **Học kỳ:** 1 (Năm học 2026–2027)
>
> **Firebase Project ID:** `mid-moblie` | **Gói ứng dụng (Package):** `com.example.studentmgmt`

---

## 1. Hướng dẫn dùng chung Firebase giữa hai thành viên nhóm

Để cả hai thành viên (**Trần Ngọc Tấn** và **Nguyễn Vĩnh Khâm**) cùng thao tác, chạy ứng dụng trên máy của mình và đồng bộ dữ liệu thời gian thực đến **cùng một cơ sở dữ liệu Firebase**, hãy thực hiện 2 bước đơn giản sau:

### Bước 1: Phân quyền truy cập trên Firebase Console (Dành cho người tạo Project)

1. Người tạo dự án truy cập: [https://console.firebase.google.com/project/mid-moblie/settings/iam](https://console.firebase.google.com/project/mid-moblie/settings/iam).
2. Vào biểu tượng **Bánh răng (Project Settings)** ở góc trên bên trái $\rightarrow$ Chọn tab **Users and permissions**.
3. Bấm nút **Add member** (Thêm thành viên):
   - Nhập email Google của thành viên thứ hai (ví dụ: `52400128@student.tdtu.edu.vn` hoặc tài khoản Gmail của bạn Khâm).
   - Phân vai trò (Role): Chọn **Editor** (để bạn có quyền xem/sửa dữ liệu Firestore, Authentication, Rules) hoặc **Owner**.
   - Bấm **Done** $\rightarrow$ **Add member**. Thành viên thứ hai sẽ nhận được email mời và có thể truy cập Firebase Console dự án `mid-moblie`.

### Bước 2: Chia sẻ tệp cấu hình `google-services.json` vào mã nguồn Android

- Tệp `google-services.json` chứa các khóa kết nối của ứng dụng với Firebase Project `mid-moblie`. Để đảm bảo an toàn bảo mật, tệp này **được bảo vệ bởi `.gitignore` và không commit lên kho Git công khai**.
- Do đó, khi clone mã nguồn về máy thứ hai:
  1. Người giữ tệp gửi trực tiếp tệp `google-services.json` (qua Zalo, Google Drive, hoặc USB) cho thành viên còn lại.
  2. Thành viên còn lại dán tệp vào đúng thư mục sau trong dự án:
     ```text
     <thư-mục-gốc-dự-án>/app/google-services.json
     ```
  3. Mở Android Studio và bấm **Sync Project with Gradle Files**. Cả hai máy tính giờ đây đã kết nối trực tiếp đến cùng một Firebase backend!

---

## 2. Thông tin cấu hình kỹ thuật của dự án

| Hạng mục                               | Giá trị chuẩn của dự án                                                   | Ghi chú                                                                     |
| ---------------------------------------- | ------------------------------------------------------------------------------- | ---------------------------------------------------------------------------- |
| **Firebase Project ID**            | `mid-moblie`                                                                  | Xác định trong `google-services.json`                                   |
| **Android Package Name**           | `com.example.studentmgmt`                                                     | Khớp `applicationId` trong `app/build.gradle.kts`                       |
| **Vị trí Firestore (Location)**  | `asia-southeast1 (Singapore)`                                                 | Vị trí khuyến nghị tốc độ cao cho Việt Nam                           |
| **Gói dịch vụ (Billing Plan)**  | Spark (Gói Miễn phí)                                                         | Không bắt buộc thẻ tín dụng                                            |
| **Cơ chế lưu ảnh đại diện** | Local Private Storage Fallback                                                  | Tự động lưu bộ nhớ riêng của app khi Cloud Storage chưa kích hoạt |
| **Quy tắc bảo mật**             | [`firestore.rules`](../firestore.rules) & [`storage.rules`](../storage.rules) | Đã kiểm định bằng 170 test cases tự động (21 test suites)           |

---

## 3. Các bước thiết lập dịch vụ trên Firebase Console

### 3.1. Thiết lập Authentication (Đăng nhập)

1. Trên Firebase Console, vào menu **Build** $\rightarrow$ **Authentication** $\rightarrow$ bấm **Get started**.
2. Tại tab **Sign-in method**, bấm vào nhà cung cấp **Email/Password**:
   - Gạt công tắc **Enable** ở mục *Email/Password* sang Bật.
   - Mục *Email link (passwordless sign-in)* giữ Tắt.
   - Bấm **Save**.
3. Tại tab **Users**, bấm **Add user** để tạo sẵn 3 tài khoản mẫu phục vụ demo phân quyền:
   - Tài khoản Admin:
     - Email: `admin@tdtu.edu.vn`
     - Password: `admin@123456` (hoặc mật khẩu tối thiểu 6 ký tự bạn chọn)
   - Tài khoản Manager:
     - Email: `manager@tdtu.edu.vn`
     - Password: `manager@123456`
   - Tài khoản Employee:
     - Email: `employee@tdtu.edu.vn`
     - Password: `employee@123456`
4. **Sao chép UID của từng tài khoản:** Sau khi tạo, tại bảng danh sách người dùng sẽ có cột **User UID**. Hãy sao chép 3 chuỗi UID này để dùng cho Bước 3.2.

> **Cơ chế đăng nhập trên ứng dụng Android:**
> Người dùng trên ứng dụng chỉ cần nhập **Tên đăng nhập (Username)** ngắn gọn: `admin`, `manager`, hoặc `employee` và mật khẩu tương ứng. Hệ thống Android đã có bộ phân giải tự động `DefaultUsernameAuthResolver` ánh xạ sang định danh kỹ thuật `username@tdtu.edu.vn`.

---

### 3.2. Thiết lập Cloud Firestore (Cơ sở dữ liệu)

1. Trên Firebase Console, vào **Build** $\rightarrow$ **Firestore Database** $\rightarrow$ bấm **Create database**.
2. Chọn vị trí: `asia-southeast1 (Singapore)`.
3. Chọn chế độ: **Start in production mode** (hoặc Test mode).
4. **Triển khai Rules bảo mật:**
   - Chuyển sang tab **Rules** trên trang web Firestore.
   - Mở tệp [`firestore.rules`](../firestore.rules) trong mã nguồn dự án, sao chép toàn bộ nội dung và dán đè lên ô soạn thảo trên web.
   - Bấm **Publish**.
5. **Tạo dữ liệu người dùng ban đầu (Collection `users`):**
   - Chuyển sang tab **Data**.
   - Bấm **Start collection**, nhập tên collection: `users`.
   - **Tài liệu cho Admin:**
     - *Document ID:* Nhập chính xác **UID của tài khoản `admin@tdtu.edu.vn`** vừa tạo ở phần Auth.
     - Thêm các trường sau:
       - `uid` (string): `<UID_CỦA_ADMIN>`
       - `username` (string): `admin`
       - `name` (string): `Trần Ngọc Tấn`
       - `age` (number): `22`
       - `phone` (string): `0901234567`
       - `role` (string): `admin`
       - `status` (string): `Normal`
       - `createdAt` (timestamp): Chọn ngày giờ hiện tại
     - Bấm **Save**.
   - **Tài liệu cho Manager:**
     - Bấm **Add document** trong collection `users`.
     - *Document ID:* Nhập **UID của `manager@tdtu.edu.vn`**.
     - Thêm các trường: `uid`, `username: "manager"`, `name: "Quản Lý Demo"`, `age: 28`, `phone: "0901234568"`, `role: "manager"`, `status: "Normal"`, `createdAt`.
   - **Tài liệu cho Employee:**
     - Bấm **Add document** trong collection `users`.
     - *Document ID:* Nhập **UID của `employee@tdtu.edu.vn`**.
     - Thêm các trường: `uid`, `username: "employee"`, `name: "Nhân Viên Demo"`, `age: 24`, `phone: "0901234569"`, `role: "employee"`, `status: "Normal"`, `createdAt`.

> **Tại sao cần tạo Document ID bằng UID?**
> Đây là thiết kế bảo mật bắt buộc của `firestore.rules`. Khi người dùng đăng nhập, ứng dụng đối chiếu `request.auth.uid == userId` và đọc `currentUser().data.role` để cấp quyền. Nếu không có document tương ứng trong Firestore hoặc trạng thái không phải `Normal`, hệ thống sẽ tự động từ chối truy cập (fail-closed) để chống giả mạo quyền.

---

### 3.3. Dữ liệu sinh viên mẫu (Collection `students` - Phục vụ Thành viên 2)

Không bắt buộc phải nhập tay từng sinh viên trên web vì phân hệ của Thành viên 2 đã được tích hợp sẵn các bộ dữ liệu mẫu trong `MockStudentRepository` và tính năng **Nhập CSV** ngay trong ứng dụng.

Nếu muốn tạo sẵn 1 sinh viên trực tiếp trên Firestore Console để test đồng bộ:

1. Trong tab **Data**, bấm **Start collection** với tên: `students`.
2. *Document ID:* Nhập Mã sinh viên (ví dụ: `524H0001`).
3. Các trường:
   - `studentId` (string): `524H0001`
   - `name` (string): `Nguyễn Văn An`
   - `dateOfBirth` (string): `2003-01-15`
   - `className` (string): `24H50301`
   - `faculty` (string): `Công nghệ thông tin`
   - `createdAt` (timestamp): Ngày giờ hiện tại
4. Tạo chứng chỉ con (nếu muốn):
   - Bấm vào tài liệu `524H0001` $\rightarrow$ bấm **Start collection** với tên: `certificates`.
   - Các trường: `certName: "TOEIC 750"`, `issueDate: "2024-05-10"`, `issuedBy: "IIG Vietnam"`.

---

## 4. Kiểm tra và xác minh trên thiết bị

Sau khi hoàn tất cấu hình, mở terminal tại thư mục gốc dự án và chạy:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug --rerun-tasks --console=plain
```

1. Cài đặt tệp APK vừa build (`app/build/outputs/apk/debug/app-debug.apk`) lên thiết bị Android hoặc máy ảo.
2. Mở ứng dụng, nhập:
   - **Tên đăng nhập:** `admin`
   - **Mật khẩu:** mật khẩu bạn đã tạo ở Bước 3.1
3. Ứng dụng sẽ đăng nhập thành công và tự động hiển thị thanh menu Quản lý người dùng, Danh sách sinh viên và Tài khoản cá nhân.

---

## 5. Xử lý các sự cố thường gặp (Troubleshooting)

| Hiện tượng                                                                 | Nguyên nhân                                                                                          | Cách khắc phục                                                                                                                                                                                         |
| ----------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Đăng nhập báo *"Tên đăng nhập hoặc mật khẩu không chính xác"* | 1. Chưa bật Email/Password trên Console.2. Chưa tạo tài khoản tương ứng.3. Nhầm mật khẩu. | Kiểm tra tab **Authentication** $\rightarrow$ **Users** trên web Console xem tài khoản `admin@tdtu.edu.vn` đã có chưa.                                                            |
| Đăng nhập thành công nhưng lập tức bị thoát ra màn hình Login     | Tài liệu trong collection `users` bị thiếu, sai Document ID, hoặc `status != "Normal"`.       | Kiểm tra tab **Firestore Data**: Document ID phải trùng khớp 100% với chuỗi UID trong Authentication, và trường `status` phải ghi đúng chính xác là `Normal` (viết hoa chữ N). |
| Báo lỗi `PERMISSION_DENIED`                                               | Quy tắc bảo mật chưa được Publish lên Firebase.                                                | Vào tab **Rules** trên Firestore Console, dán nội dung [`firestore.rules`](../firestore.rules) và bấm **Publish**.                                                                     |
| Thông báo lỗi lưu trữ ảnh / Cloud Storage bắt nạp tiền               | Gói cước Free không cho tạo Cloud Storage Bucket.                                                 | Không cần nạp tiền. Ứng dụng đã tự động kích hoạt chế độ **Local Private Storage Fallback**, ảnh đại diện vẫn được lưu và hiển thị bình thường trên máy.           |
| Máy của bạn cùng nhóm không kết nối được Firebase                  | Thiếu tệp `google-services.json` trong thư mục `app/` của máy bạn đó.                     | Gửi tệp `google-services.json` cho bạn cùng nhóm và đặt vào `app/google-services.json`.                                                                                                      |
