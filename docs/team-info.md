# Thông Tin Nhóm và Môi Trường Bàn Giao Sản Phẩm

> **Lưu ý trước khi bắt đầu:** Đây là nơi tập trung toàn bộ thông tin định danh của nhóm để phân công công việc. Trước khi nộp gói bài tập cuối kỳ, hãy hoàn tất các trường `[BẮT BUỘC ĐIỀN]` và đồng bộ sang bìa báo cáo học thuật, slide và video demo.

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
| **Tên đề tài** | Realtime Student Information Management Mobile Application with Firebase Firestore |

---

## 2. Thông tin thành viên nhóm & Phân công trách nhiệm

| Mã nội bộ | Họ và tên sinh viên | Mã số sinh viên (MSSV) | Email sinh viên | Vai trò dự án & Trách nhiệm |
|:---:|---|---|---|---|
| **A** | Tan (Trần Ngọc Tân) | 52400158 | 52400158@student.tdtu.edu.vn | **Trưởng nhóm:** Phụ trách Module Auth, User Management, Security Rules, UI Auth/User, Tổng hợp Báo cáo, Thuyết minh video |
| **B** | `[HỌ VÀ TÊN THÀNH VIÊN B]` | `[MSSV THÀNH VIÊN B]` | `[EMAIL THÀNH VIÊN B]` | **Thành viên:** Phụ trách Module Student & Certificate CRUD, Realtime Sync, CSV Import/Export, Bộ dữ liệu mẫu, Quay video |

---

## 3. Môi trường Firebase Demo phục vụ chấm bài

> *Lưu ý an toàn: Không đưa khóa bí mật Service Account hoặc thông tin thanh toán cá nhân vào repository công khai.*

| Hạng mục | Giá trị cấu hình | Ghi chú |
|---|---|---|
| **Firebase Project ID** | `[BẮT BUỘC ĐIỀN - Điền Project ID khi tạo Firebase]` | Project trên Firebase Console |
| **Firestore Location** | `asia-southeast1 (Singapore)` | Vị trí máy chủ cơ sở dữ liệu |
| **Storage Bucket** | `[BẮT BUỘC ĐIỀN - Ví dụ: student-mgmt-midterm.appspot.com]` | Dùng lưu trữ avatar |
| **Pricing Plan** | `Blaze (Pay-as-you-go)` | Kích hoạt hạn mức miễn phí Always Free Tier |
| **Trạng thái Security Rules** | `Sẵn sàng triển khai` | Áp dụng từ `firestore.rules` & `storage.rules` |

---

## 4. Danh sách tài khoản kiểm thử cho Giảng viên (Test Credentials Dự kiến)

| Vai trò | Tên đăng nhập (Username) | Mật khẩu mặc định | Ghi chú kiểm thử chức năng |
|---|---|---|---|
| **Admin** | `admin` | `admin` | Tài khoản Quản trị viên tự động seed khi chạy lần đầu. Toàn quyền quản trị user, SV, xuất dữ liệu, xem log. |
| **Manager** | `manager` | `manager` | Quản lý sinh viên, chứng chỉ, tìm kiếm tiếng Việt, Import/Export CSV. Bị ẩn chức năng User. |
| **Employee** | `employee` | `employee` | Chế độ chỉ xem (Read-only) sinh viên/chứng chỉ; đổi ảnh đại diện cá nhân; các thao tác ghi bị chặn. |

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
| **Mục tiêu kiểm thử tự động** | 5 Unit Tests (Validation, CSV, Roles, Vietnamese Sort) |
| **Mục tiêu kiểm thử hệ thống** | 27 System Test Cases (100% Passed) |
