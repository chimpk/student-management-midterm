# Sổ Tay & Checklist Nộp Bài Giữa Kỳ (Mobile App Development)

> **Môn học:** Phát triển ứng dụng di động (503074) – Học kỳ 1, Năm học 2026–2027  
> **Trường:** Đại học Tôn Đức Thắng (TDTU) – Khoa Công nghệ Thông tin  
> **Đề tài:** Ứng dụng quản lý thông tin sinh viên thời gian thực (Realtime Student Management App)  
> **Nhóm thực hiện:**
> - Thành viên 1 (Trưởng nhóm): **Trần Ngọc Tấn** – MSSV: **52400158**
> - Thành viên 2: **Nguyễn Vĩnh Khâm** – MSSV: **52400128**

---

## 1. Yêu Cầu Hồ Sơ Nộp Bài (Theo Đề Bài Giữa Kỳ)

Hồ sơ nộp bài đóng gói thành 1 tệp nén (`.zip`) gồm đầy đủ 5 phần bắt buộc sau:

| STT | Thành phần | Định dạng / Vị trí | Lưu ý quan trọng |
|:---:|---|---|---|
| **1** | **Báo cáo học thuật** | File PDF (`BaoCao_GiuaKy_NhomXX.pdf`) | Bìa, mục lục, bảng phân công, kiến trúc, sơ đồ UML, ảnh chụp minh chứng, tài liệu tham khảo. |
| **2** | **Video Demo** | Link YouTube / Google Drive (Unlisted) hoặc file MP4 | **Tối đa 20 phút, độ phân giải $\ge$ 720p, có âm thanh/thuyết minh**. *(Thiếu video bị trừ tới 2.0 điểm)*. |
| **3** | **Tệp cài đặt APK** | File APK (`app-debug.apk`) | Đặt ngay thư mục gốc để thầy không cần máy dev vẫn cài và test được trên điện thoại/máy ảo. |
| **4** | **Source Code & Cấu hình** | Toàn bộ source Android Studio | **BẮT BUỘC có `app/google-services.json`** đi kèm trong file nộp (dù file này bị gitignore). |
| **5** | **Tài khoản Admin & Hướng dẫn** | Ghi rõ trong trang đầu báo cáo và file `README.txt` | Cung cấp tài khoản Admin cho giám khảo đăng nhập ngay. |

---

## 2. Thông Tin Tài Khoản Kiểm Thử Dành Cho Giám Khảo

Giám khảo chỉ cần mở ứng dụng và nhập các thông tin sau:

| Vai trò | Tên đăng nhập (Username) | Mật khẩu | Email kỹ thuật trên Firebase | Mục đích kiểm thử |
|---|---|---|---|---|
| **Admin (Quản trị viên)** | **`admin`** | **`admin@123456`** | `admin@tdtu.edu.vn` | **Dành cho thầy chấm bài:** Quản lý tài khoản (thêm/sửa/khóa/mở khóa), xem lịch sử đăng nhập, toàn quyền sinh viên & chứng chỉ. |
| **Manager (Quản lý)** | `manager` | `manager@123456` | `manager@tdtu.edu.vn` | Kiểm thử chức năng sinh viên, chứng chỉ, tìm kiếm tiếng Việt, Import/Export CSV. Bị chặn trang quản lý tài khoản. |
| **Employee (Nhân viên)** | `employee` | `employee@123456` | `employee@tdtu.edu.vn` | Kiểm thử quyền chỉ xem (Read-only), ẩn các nút Thêm/Sửa/Xóa, đổi ảnh đại diện cá nhân (Avatar). |

---

## 3. Cấu Trúc Thư Mục Đóng Gói File ZIP Nộp Bài

```text
[MSSV_TrưởngNhóm]_[HọVàTên_TrưởngNhóm]_GiuaKy.zip
│
├── app-debug.apk                       <-- File APK build sẵn để thầy cài trực tiếp
├── BaoCao_GiuaKy_Nhom.pdf               <-- File báo cáo PDF chính thức
├── HUONG_DAN_CHAY_VA_TAI_KHOAN.txt     <-- Hướng dẫn nhanh 1 trang dành cho thầy
│
└── source-code/                        <-- Thư mục chứa toàn bộ mã nguồn
    ├── app/
    │   ├── google-services.json        <-- PHẢI CÓ FILE NÀY TRONG SOURCE NỘP!
    │   ├── src/
    │   └── build.gradle.kts
    ├── gradle/
    ├── gradlew
    ├── gradlew.bat
    ├── build.gradle.kts
    ├── settings.gradle.kts
    ├── firestore.rules
    ├── storage.rules
    └── sample-data/                    <-- File CSV mẫu để thầy test Import/Export
        ├── students.csv
        ├── certificates.csv
        └── students-invalid.csv
```

---

## 4. Lệnh Build APK Trước Khi Nộp

Mở terminal trong thư mục dự án và chạy lệnh:

```powershell
# 1. Chạy toàn bộ 170 unit tests tự động để đảm bảo không có lỗi:
.\gradlew.bat testDebugUnitTest

# 2. Build file APK Debug:
.\gradlew.bat assembleDebug
```
File APK sau khi build xong sẽ nằm tại:
```text
app/build/outputs/apk/debug/app-debug.apk
```
Copy file này ra thư mục nộp bài và đổi tên thành `app-debug.apk`.

---

## 5. Checklist Kiểm Tra Cuối Cùng Trước Khi Nhấn Nộp

- [ ] **Tài khoản Firebase live:** Đã kiểm tra 3 tài khoản `admin`, `manager`, `employee` đăng nhập thành công 100%.
- [ ] **Rules bảo mật:** Đã publish `firestore.rules` và `storage.rules` lên Firebase Console.
- [ ] **Báo cáo PDF:**
  - [ ] Đã điền đầy đủ họ tên Giảng viên hướng dẫn, Mã lớp học phần, Nhóm.
  - [ ] Đã có bảng phân công công việc cân bằng giữa 2 thành viên (tránh bị trừ điểm chia việc không đều).
  - [ ] Đã chụp đầy đủ hình ảnh minh chứng màn hình sáng/tối và Firebase Console.
- [ ] **Video Demo:**
  - [ ] Đã upload lên YouTube (chế độ Không công khai - Unlisted) hoặc Google Drive (bật quyền "Bất kỳ ai có đường liên kết").
  - [ ] Đã dán link video vào trang bìa/mục lục của Báo cáo PDF.
  - [ ] Video có tiếng thuyết minh rõ ràng, thời lượng dưới 20 phút.
- [ ] **Gói file ZIP:**
  - [ ] Đã đính kèm file `app/google-services.json`.
  - [ ] Đã đính kèm file `app-debug.apk`.
  - [ ] Đã đính kèm thư mục dữ liệu mẫu `sample-data/`.
  - [ ] Đã nộp đúng hạn trên cổng nộp bài theo quy định (không nộp qua email riêng).
