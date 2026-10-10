# Thông tin nhóm và môi trường bàn giao sản phẩm

> **Trạng thái:** BẢN NHÁP. Đây là nơi tập trung thông tin định danh và môi trường dự kiến. Trước khi nộp, phải hoàn tất các trường `[BẮT BUỘC ĐIỀN]` và đối chiếu với báo cáo, slide, video.

---

## 1. Thông tin học phần & Giảng viên

| Hạng mục | Thông tin chi tiết |
|---|---|
| **Trường / Viện** | Trường Đại học Tôn Đức Thắng (TDTU) |
| **Khoa / Bộ môn** | Khoa Công nghệ Thông tin |
| **Tên học phần** | Phát triển ứng dụng di động (Mobile Application Development) |
| **Mã học phần** | 503074 |
| **Học kỳ / Năm học** | Học kỳ 1 – Năm học 2026–2027 |
| **Giảng viên hướng dẫn** | `[HỌ VÀ TÊN GIẢNG VIÊN PHỤ TRÁCH]` |
| **Lớp học phần / Nhóm** | `[LỚP HỌC PHẦN - NHÓM BÀI TẬP]` |
| **Tên đề tài** | Ứng dụng di động quản lý thông tin sinh viên thời gian thực với Firebase Firestore |

---

## 2. Thông tin thành viên nhóm & Phân công trách nhiệm

| Mã nội bộ | Họ và tên sinh viên | Mã số sinh viên (MSSV) | Email sinh viên | Vai trò dự án & Trách nhiệm |
|:---:|---|---|---|---|
| **A** | Trần Ngọc Tấn | 52400158 | 52400158@student.tdtu.edu.vn | **Trưởng nhóm:** Phụ trách Module Auth, User Management, Security Rules, UI Auth/User, Tổng hợp Báo cáo, Thuyết minh video |
| **B** | `[HỌ VÀ TÊN THÀNH VIÊN B]` | `[MSSV THÀNH VIÊN B]` | `[EMAIL THÀNH VIÊN B]` | **Thành viên:** Phụ trách Module Student & Certificate CRUD, Realtime Sync, CSV Import/Export, Bộ dữ liệu mẫu, Quay video |

---

## 3. Môi trường Firebase Demo phục vụ chấm bài

> *Lưu ý an toàn: Không đưa khóa bí mật Service Account hoặc thông tin thanh toán cá nhân vào repository công khai.*

| Hạng mục | Giá trị cấu hình | Ghi chú |
|---|---|---|
| **Firebase Project ID** | `[BẮT BUỘC ĐIỀN - Điền Project ID khi tạo Firebase]` | Project trên Firebase Console |
| **Firestore Location** | `asia-southeast1 (Singapore)` | Vị trí máy chủ cơ sở dữ liệu |
| **Storage Bucket** | `[BẮT BUỘC ĐIỀN - Ví dụ: student-mgmt-midterm.appspot.com]` | Dùng lưu trữ avatar |
| **Gói dịch vụ** | `[BẮT BUỘC ĐIỀN: Spark hoặc Blaze]` | Ghi theo cấu hình thực tế |
| **Trạng thái Security Rules** | `[BẮT BUỘC ĐIỀN: chưa triển khai/đã triển khai]` | Đối chiếu `firestore.rules` và `storage.rules` |

---

## 4. Danh sách tài khoản kiểm thử dự kiến

| Vai trò | Tên đăng nhập | Mật khẩu demo | Email tùy chọn | Ghi chú |
|---|---|---|---|---|
| **Admin** | `admin` | `admin@123456` | Không bắt buộc | Dự kiến; cấp ngoài client và không tự nâng quyền. |
| **Manager** | `manager` | `manager@123456` | Không bắt buộc | Dự kiến; chỉ đánh dấu đã tạo khi có bằng chứng. |
| **Employee** | `employee` | `employee@123456` | Không bắt buộc | Dự kiến; chỉ đánh dấu đã tạo khi có bằng chứng. |

Khi tài khoản có email, người dùng có thể nhập email thay username. Mật khẩu demo đáp ứng giới hạn tối thiểu 6 ký tự của Firebase Email/Password.

---

## 5. Môi trường kiểm thử nghiệm thu dự kiến

| Thông số | Giá trị kế hoạch |
|---|---|
| **Hệ điều hành máy phát triển** | Windows 10/11 64-bit |
| **Android Studio** | Android Studio Iguana (2023.2.1) trở lên |
| **Java Development Kit (JDK)** | OpenJDK 17 (JDK 17) |
| **Gradle / AGP** | Gradle 8.4 / Android Gradle Plugin 8.3.2 |
| **Thiết bị thử nghiệm 1** | Google Pixel Emulator – Android 14.0 (API 34) |
| **Thiết bị thử nghiệm 2** | Android Device / Tablet – Android 12.0 (API 31) |
| **Kiểm thử tự động hiện có** | 6 Unit Tests trong `ValidationUtilsTest` |
| **Mục tiêu kiểm thử hệ thống** | 27 kịch bản; trạng thái hiện tại: Chờ test |

## 6. Checklist thông tin bắt buộc trước khi nộp

- [ ] Họ tên giảng viên.
- [ ] Lớp học phần và nhóm bài tập.
- [ ] Họ tên, MSSV và email thành viên B.
- [ ] Firebase Project ID và Firestore Location thực tế.
- [ ] Storage Bucket và gói dịch vụ thực tế, hoặc ghi rõ chưa sử dụng Storage.
- [ ] Trạng thái triển khai Security Rules.
- [ ] Xác nhận ba username demo đã được cấp; ghi email chỉ với tài khoản có sử dụng email.
- [ ] Thiết bị, ngày chạy và kết quả kiểm thử thực tế.
