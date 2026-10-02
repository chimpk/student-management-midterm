# Quy Chuẩn Lập Trình (cho nhóm và công cụ AI)

## 1. Kiến trúc và mã nguồn

1. **Ngôn ngữ và nền tảng:** mã Android trong `com.example.studentmgmt` viết bằng **Java 17**, `minSdk` **26** (Android 8.0+), đúng với `app/build.gradle.kts`.
2. **MVVM bắt buộc** (xem [`design.md`](design.md)):
   - Activity/Fragment chỉ hiển thị và xử lý tương tác.
   - Trạng thái nằm trong `ViewModel`, dùng `LiveData`.
   - Mọi thao tác Firebase (Auth, Firestore, Storage) nằm trong lớp `Repository`.
3. **View Binding:** dùng ViewBinding (`ActivityLoginBinding`, `ItemStudentBinding`...), không dùng `findViewById`.
4. **Build:** Kotlin DSL (`build.gradle.kts`) và Gradle Version Catalog (`gradle/libs.versions.toml`).
5. **Tên lớp và gói:** theo bảng package ở [`design.md`](design.md) mục 2.

## 2. Firebase và chất lượng dữ liệu

1. **Thời gian thực:** dùng `addSnapshotListener` trong Repository, đẩy kết quả lên `LiveData`. Gỡ listener khi ViewModel bị hủy.
2. **Xử lý lỗi:** mọi tác vụ bất đồng bộ của Firebase phải có `addOnSuccessListener` và `addOnFailureListener`. Ghi log lỗi và chuyển thông báo dễ hiểu lên UI.
3. **Phân quyền hai lớp:** kiểm tra vai trò ở giao diện (`PermissionHelper`) **và** ở `firestore.rules`.
4. **Giá trị lưu trữ:** `role` = `admin` | `manager` | `employee`; `status` = `Normal` | `Locked`. Khai báo trong `Constants`, không viết chuỗi trực tiếp.
5. **Kiểm tra dữ liệu đầu vào:** theo [`SRS.md`](SRS.md) mục 4, tập trung trong `ValidationUtils`. Dùng chung cho form nhập tay và import CSV.
6. **Trường bắt buộc:** kiểm tra trước khi ghi lên Firestore (`set`, `add`, `WriteBatch`).
