# Checklist Bàn Giao và Nghiệm Thu Sản Phẩm Cuối Kỳ

> **Nguyên tắc nghiệm thu:** Chỉ đánh dấu mục hoàn thành khi có bằng chứng thực nghiệm rõ ràng. Người phát triển (Owner) và người kiểm thử nghiệm thu (Reviewer) phải là hai người khác nhau theo ma trận phân công giao việc.

---

## 1. Các Cổng Kiểm Soát Chất Lượng Bàn Giao (Quality Release Gates)

| Mã Gate | Tiêu chuẩn nghiệm thu | Bằng chứng cần đạt | Owner | Reviewer | Trạng thái nghiệm thu |
|:---:|---|---|:---:|:---:|:---:|
| **G1 – Source & Build** | Biên dịch Debug APK thành công từ máy sạch không có lỗi | Lệnh `.\gradlew.bat assembleDebug` exit code 0; file APK sinh tại `app/build/outputs/apk/debug/app-debug.apk` | **A** | **B** | **Chờ nghiệm thu** |
| **G2 – Unit Testing** | Bộ kiểm thử tự động đạt 100% không có lỗi | Lệnh `.\gradlew.bat testDebugUnitTest` vượt qua 5 bài test (CSV, Validation, Roles, Tiếng Việt) | **B** | **A** | **Chờ nghiệm thu** |
| **G3 – Firebase Infrastructure** | Auth, Firestore, Storage và Rules triển khai đúng cấu hình | `firebase.json`, `firestore.rules`, `storage.rules`, `AdminSeedService` tạo Admin tự động | **A** | **B** | **Chờ nghiệm thu** |
| **G4 – Phân quyền RBAC** | Đúng ma trận quyền 3 vai trò (Admin, Manager, Employee) | Test cases TC-01 đến TC-08, TC-10, TC-15, TC-20 đều đạt trạng thái Pass | **B** | **A** | **Chờ nghiệm thu** |
| **G5 – Realtime & Offline** | Đồng bộ 2 thiết bị < 2s và cơ chế lưu đệm offline hoạt động | Test case TC-21 (Realtime sync < 2s) và kiểm tra Firestore SDK cache khi ngắt mạng | **B** | **A** | **Chờ nghiệm thu** |
| **G6 – Import / Export CSV** | Xử lý CSV tiếng Việt UTF-8 BOM, chia nhỏ batching > 500 dòng | Test cases TC-16, TC-17, TC-18, TC-19, TC-24, TC-27 đều đạt trạng thái Pass | **B** | **A** | **Chờ nghiệm thu** |
| **G7 – Báo cáo học thuật** | Báo cáo hoàn chỉnh theo chuẩn cấu trúc của Khoa | Tài liệu báo cáo gồm các chương học thuật hoàn chỉnh | **A** | **B** | **Chờ nghiệm thu** |
| **G8 – Video Demo** | Đủ âm thanh, hình ảnh 720p+, thời lượng ≤ 20 phút | Kịch bản chi tiết [`docs/demo-script.md`](demo-script.md) với timeline từng phút | **A** | **B** | **Chờ thực hiện** |
| **G9 – Bộ gói nộp bài** | Đầy đủ mã nguồn, cấu hình demo, tài khoản Admin, hướng dẫn | Hướng dẫn [`docs/test-and-setup.md`](test-and-setup.md), tài khoản `admin@student.app` | **B** | **A** | **Chờ đóng gói** |

---

## 2. Kiểm Soát Mã Nguồn & Tệp Tin Nhạy Cảm

- [ ] Cây thư mục sạch, không có file rác, file `.class` hoặc `.DS_Store`.
- [ ] `firebase.json`, `firestore.rules`, `storage.rules`, `firestore.indexes.json` đã có mặt đầy đủ trong dự án.
- [ ] File cấu hình mẫu [`app/google-services.json.example`](../app/google-services.json.example) và [`local.properties.example`](../local.properties.example) được cung cấp sẵn cho người clone mới.
- [ ] File nhạy cảm thật (`google-services.json`, `local.properties`, keystore bí mật) nằm trong `.gitignore` không bị lộ lên repository công khai.
- [ ] Dự án biên dịch trơn tru với JDK 17 và Android SDK API 34.

Lệnh kiểm tra tính toàn vẹn:
```powershell
git status --short
git ls-files app/google-services.json
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

---

## 3. Môi Trường Firebase Demo & Tài Khoản Kiểm Thử

- [ ] Package Android đăng ký trên Firebase Console: `com.example.studentmgmt`.
- [ ] Đã kích hoạt Email/Password Authentication trong Firebase Console.
- [ ] Đã tạo Cloud Firestore ở Production Mode tại region `asia-southeast1`.
- [ ] Đã triển khai `firestore.rules` và `storage.rules`.
- [ ] Tài khoản Admin mặc định để chấm bài:
  - **Email:** `admin@student.app`
  - **Mật khẩu:** `Admin@123456`
- [ ] Tài khoản Manager & Employee được tạo hợp lệ thông qua giao diện Quản lý người dùng của Admin.
- [ ] Dự án được ghi chú rõ ràng về yêu cầu gói Blaze/Billing của Cloud Storage theo chính sách tháng 9/2024.

---

## 4. Dữ Liệu Mẫu & Kiểm Thử Toàn Diện

- [ ] `sample-data/students.csv`: 20 sinh viên hợp lệ có đầy đủ dấu tiếng Việt.
- [ ] `sample-data/students-invalid.csv`: Chứa dòng thiếu dữ liệu để kiểm tra cơ chế báo lỗi dòng.
- [ ] `sample-data/certificates.csv`: Danh sách chứng chỉ mẫu ánh xạ đúng MSSV.
- [ ] Bảng kết quả 27/27 Test Cases trong [`docs/test-and-setup.md`](test-and-setup.md) được phân công rõ ràng người test và hạn chót.
- [ ] Cascade deletion: Kiểm tra xóa sinh viên sẽ tự động xóa sạch toàn bộ subcollection chứng chỉ liên quan.

---

## 5. Báo Cáo Học Thuật & Video Thuyết Minh

- [ ] Báo cáo gồm:
  - Trang thông tin nhóm và giảng viên hướng dẫn.
  - Tóm tắt đề tài (Abstract) & Mục lục.
  - Nghiên cứu chuyên sâu về NoSQL Firestore, snapshot listener, subcollections, transactions, batch writes, offline persistence, Security Rules và phân tích chi phí.
  - Đặc tả yêu cầu, ma trận RBAC, kiến trúc MVVM, ERD và Sequence Diagrams.
  - Chi tiết triển khai code và kết quả kiểm thử.
  - Tài liệu tham khảo chuẩn mực với hơn 10 nguồn tài liệu chính thức từ Google và Android Developers.
- [ ] Kịch bản video demo [`docs/demo-script.md`](demo-script.md) chi tiết 20 phút với phân đoạn rõ ràng cho cả 2 thiết bị và Firebase Console.

---

## 6. Ký Xác Nhận Nghiệm Thu Bàn Giao

| Vai trò nghiệm thu | Họ và tên | Chữ ký xác nhận | Ngày nghiệm thu | Đánh giá tổng thể |
|---|---|:---:|:---:|:---:|
| **Trưởng nhóm (Thành viên A)** | Tan (Trần Ngọc Tân) | *Chờ ký* | `[NGÀY]` | *Chưa nghiệm thu* |
| **Thành viên (Thành viên B)** | `[HỌ VÀ TÊN THÀNH VIÊN B]` | *Chờ ký* | `[NGÀY]` | *Chưa nghiệm thu* |
