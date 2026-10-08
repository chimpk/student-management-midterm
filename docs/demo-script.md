# Kịch Bản Chi Tiết Video Demo & Hướng Dẫn Sản Xuất (20 Phút)

> **Môn học:** Phát triển ứng dụng di động – Mã HP: 503074  
> **Yêu cầu kỹ thuật:** Video có âm thanh, thời lượng tối đa **20 phút**, độ phân giải tối thiểu **720p (1280x720)**, âm thanh rõ ràng, không tạp âm.  
> **Người trình bày thuyết minh:** **Thành viên A** (Trưởng nhóm).  
> **Người chuẩn bị kỹ thuật & dữ liệu:** **Thành viên B** (Chuẩn bị 2 máy ảo/thiết bị, nạp dữ liệu, quay màn hình).

---

## 1. Chuẩn Bị Trước Khi Bắt Đầu Ghi Hình

### 1.1 Môi trường thiết bị & Phần mềm quay
1. **Thiết bị 1 (Máy chính):** Pixel 6 Pro Emulator (Android 14) – Dùng cho toàn bộ thao tác demo chính.
2. **Thiết bị 2 (Thời gian thực):** Máy ảo thứ hai hoặc Thiết bị Android thật cắm dây (Android 12+) – Đặt cạnh Thiết bị 1 trên màn hình máy tính (chia đôi màn hình).
3. **Phần mềm quay màn hình:** OBS Studio hoặc Windows Game Bar (Win + G), độ phân giải 1080p hoặc 720p, 60fps.
4. **Trình duyệt Web:** Mở sẵn tab **Firebase Console** (trang Firestore Database, Authentication, Rules, Storage).

### 1.2 Dữ liệu mẫu cần chuẩn bị sẵn
- **Tài khoản Admin:** Tên đăng nhập: `admin` | Mật khẩu: `admin`
- **Tài khoản Manager:** Tên đăng nhập: `manager` | Mật khẩu: `manager`
- **Tài khoản Employee:** Tên đăng nhập: `employee` | Mật khẩu: `employee`
- **File CSV trong bộ nhớ thiết bị:** Chép file `sample-data/students.csv`, `students-invalid.csv`, `certificates.csv` vào thư mục `Download` của máy ảo.

---

## 2. Kịch Bản Chi Tiết Từng Phút (Timeline & Lời Thoại)

### Phút 00:00 – 01:30: Mở đầu & Giới thiệu đề tài
- **Hình ảnh hiển thị:** Slide mở đầu hoặc giao diện ứng dụng kèm thông tin nhóm, môn học, giảng viên phụ trách.
- **Nội dung trình bày:**
  - Kính chào thầy/cô và các bạn. Đây là video báo cáo bài tập giữa kỳ môn Phát triển ứng dụng di động.
  - Nhóm gồm 2 thành viên: [Họ tên SV A - MSSV] và [Họ tên SV B - MSSV].
  - Đề tài: *Ứng dụng Quản lý Thông tin Sinh viên Thời gian Thực sử dụng Google Cloud Firestore*.
  - Mục tiêu dự án: Xây dựng hệ thống hoàn chỉnh theo kiến trúc MVVM, đồng bộ tức thời hai chiều, bảo mật phân quyền 3 vai trò (Admin, Manager, Employee) và khả năng làm việc ngoại tuyến.

---

### Phút 01:30 – 06:30: Báo cáo Nghiên cứu Chuyên sâu về Firebase Cloud Firestore
- **Hình ảnh hiển thị:** Chuyển sang màn hình trình duyệt Firebase Console của dự án.
- **Nội dung trình bày:**
  - **Mô hình NoSQL Document-Collection:** Mở tab Firestore Database. Giải thích cấu trúc Collection `users`, `students`, và Subcollections `certificates`, `loginHistory`. So sánh sự khác biệt và ưu thế vượt trội của Document Database so với SQLite truyền thống.
  - **Kiểu dữ liệu & Giới hạn:** Trình bày các kiểu dữ liệu phong phú (String, Number, Timestamp, Map). Lưu ý giới hạn kích thước document (tối đa 1 MiB) và giải pháp dùng subcollection để quản lý chứng chỉ 1-N.
  - **Cơ chế Snapshot Listener thời gian thực:** Giới thiệu API `addSnapshotListener()` giúp ứng dụng nhận push update tức thì qua WebSocket mà không cần HTTP Polling tốn tài nguyên.
  - **Thao tác Atomic Batch Write:** Giải thích cách nhóm xử lý import CSV số lượng lớn bằng `WriteBatch`, chia nhỏ thành các batch 400 documents để không vượt ngưỡng 500 ops của Google.
  - **Offline Persistence & Bảng giá Spark/Blaze:** Trình bày cơ chế local cache của SDK và lưu ý về chính sách Cloud Storage yêu cầu gói Blaze từ tháng 9/2024.

---

### Phút 06:30 – 08:00: Kiến trúc Ứng dụng & Cấu trúc Mã nguồn
- **Hình ảnh hiển thị:** Mở Android Studio, lướt qua cây thư mục và sơ đồ kiến trúc MVVM.
- **Nội dung trình bày:**
  - Trình bày kiến trúc 3 tầng: View (Activity/Fragment/ViewBinding) ↔ ViewModel (`LiveData`) ↔ Repository (`FirebaseClient`).
  - Giới thiệu các package sạch: `adapter`, `data`, `model`, `service`, `ui`, `utils`, `viewmodel`.
  - Giới thiệu dịch vụ tự động khởi tạo Quản trị viên [`AdminSeedService`](../app/src/main/java/com/example/studentmgmt/service/AdminSeedService.java).

---

### Phút 08:00 – 10:00: Phân hệ Xác thực & Kiểm tra Tài khoản Bị khóa (RBAC)
- **Hình ảnh hiển thị:** Thao tác trên màn hình điện thoại (Emulator).
- **Thao tác demo:**
  1. Nhập sai mật khẩu → Hiển thị thông báo lỗi trực quan.
  2. Đăng nhập bằng tài khoản bị khóa `Locked` → Hệ thống từ chối truy cập, hiện thông báo *"Tài khoản đã bị khóa"* và giữ ở màn hình Login.
  3. Đăng nhập thành công với tài khoản Admin (`admin@student.app`).
  4. Mở tab Cá nhân → Đổi ảnh đại diện (Upload avatar lên Firebase Storage) → Tải ảnh thành công.

---

### Phút 10:00 – 12:30: Phân hệ Quản trị viên (Admin Module)
- **Hình ảnh hiển thị:** Giao diện Quản lý Người dùng của Admin.
- **Thao tác demo:**
  1. Hiển thị danh sách toàn bộ User với badge vai trò (Admin, Manager, Employee).
  2. Bấm thêm người dùng mới:
     - Nhập thử Tuổi = 17 → Báo lỗi validation "Tuổi phải từ 18 đến 60".
     - Nhập thử SĐT 9 số → Báo lỗi "Số điện thoại không hợp lệ".
     - Nhập thông tin chuẩn → Tạo thành công tài khoản Manager mới.
  3. Thực hiện thao tác Khóa (Lock) một tài khoản và Mở khóa (Unlock).
  4. Bấm vào một tài khoản → Xem **Lịch sử đăng nhập (Login History)** hiển thị chi tiết mốc thời gian và model thiết bị.

---

### Phút 12:30 – 15:30: Phân hệ Quản lý Sinh viên & Chứng chỉ (Manager Module)
- **Hình ảnh hiển thị:** Đăng xuất Admin, đăng nhập bằng tài khoản Manager.
- **Thao tác demo:**
  1. Chỉ ra thanh điều hướng của Manager: Không có mục Quản lý tài khoản (tuân thủ RBAC).
  2. Thêm mới một sinh viên: Nhập MSSV `SV088`, Họ tên tiếng Việt `Trần Thị Mai Phương`, GPA `3.75`, Khoa `CNTT`. Bấm Lưu → Xuất hiện ngay trên RecyclerView.
  3. Nhấn vào sinh viên vừa tạo để xem Chi tiết:
     - Bấm Thêm chứng chỉ: Nhập `IELTS 7.5`, ngày cấp, đơn vị cấp IDP. Lưu vào subcollection.
     - Thêm tiếp chứng chỉ thứ hai: `AWS Certified Cloud Practitioner`.
  4. Quay lại danh sách:
     - Thử nghiệm **Tìm kiếm tiếng Việt**: Gõ `Phương`, gõ có dấu `Thị` → Kết quả lọc chuẩn xác tức thì.
     - Thử nghiệm **Sắp xếp**: Chọn Sắp xếp theo tên A–Z, sắp xếp theo GPA giảm dần.

---

### Phút 15:30 – 17:00: Thử nghiệm Nhập / Xuất dữ liệu CSV (Import / Export)
- **Hình ảnh hiển thị:** Màn hình Import/Export CSV.
- **Thao tác demo:**
  1. Bấm **Import Sinh viên**: Chọn file `sample-data/students.csv` (20 sinh viên tiếng Việt).
     - Quá trình chạy qua `WriteBatch`, hiển thị Progress Dialog.
     - Sau khi hoàn thành, màn hình danh sách sinh viên lập tức cập nhật đủ 20 sinh viên mới mà không lỗi font chữ tiếng Việt.
  2. Thử chọn file `students-invalid.csv`: Báo cáo rõ ràng số dòng thành công và số dòng lỗi bị bỏ qua.
  3. Bấm **Export CSV**: Xuất danh sách sinh viên ra bộ nhớ máy qua Storage Access Framework (SAF). Mở xem file kiểm tra định dạng UTF-8 BOM chuẩn xác.

---

### Phút 17:00 – 18:30: Thử nghiệm Đồng bộ Thời gian thực & Chế độ Ngoại tuyến (Realtime & Offline)
- **Hình ảnh hiển thị:** Đặt song song 2 thiết bị (Thiết bị 1: Manager, Thiết bị 2: Employee).
- **Thao tác demo:**
  1. **Realtime Sync:** Trên Thiết bị 1, thêm hoặc sửa điểm GPA của một sinh viên. Quan sát Thiết bị 2: Dữ liệu tự động nhảy sang giá trị mới trong vòng **dưới 1 giây** mà không cần thao tác vuốt làm mới.
  2. **Employee Read-only:** Trên Thiết bị 2 (Employee), chứng minh các nút Thêm/Sửa/Xóa sinh viên bị ẩn hoàn toàn. Thử cố can thiệp sẽ bị Security Rules chặn.
  3. **Offline Resilience:** Tắt kết nối mạng trên Thiết bị 1 → App hiển thị badge Offline. Người dùng vẫn cuộn danh sách và tìm kiếm mượt mà nhờ Firestore Local Cache. Bật mạng lại → Đồng bộ hoạt động bình thường.
  4. **Cascade Delete:** Xóa một sinh viên có chứng chỉ trên Thiết bị 1 → Mở Firebase Console chứng minh document sinh viên và toàn bộ subcollection chứng chỉ con đều bị xóa sạch.

---

### Phút 18:30 – 19:30: Kiểm tra Cloud Security Rules trên Firebase Console
- **Hình ảnh hiển thị:** Tab Rules trên Firebase Console.
- **Nội dung trình bày:**
  - Giải thích hàm `isAdmin()`, `isManager()` đọc trực tiếp từ `users/{uid}`.
  - Nhấn mạnh rằng dù có kẻ gian can thiệp sửa code client, server Firebase vẫn từ chối 100% các yêu cầu ghi trái thẩm quyền với mã lỗi `PERMISSION_DENIED`.

---

### Phút 19:30 – 20:00: Tổng kết & Kết luận
- **Hình ảnh hiển thị:** Slide kết luận và lời cảm ơn.
- **Nội dung trình bày:**
  - Tóm tắt kết quả: Ứng dụng đã hoàn thành 100% yêu cầu đề bài, vượt qua 27/27 kịch bản kiểm thử nghiệm thu.
  - Những kinh nghiệm quý giá nhóm đã học được về kiến trúc NoSQL, reactive mobile programming và bảo mật đám mây.
  - Lời cảm ơn gửi tới Quý Thầy/Cô đã hướng dẫn.

---

## 3. Checklist Kiểm Tra Chất Lượng Trước Khi Nộp Video

- [ ] Định dạng video: MP4 (H.264), độ phân giải 1080p hoặc 720p.
- [ ] Thời lượng video: **Đúng trong khoảng 18 đến 20 phút** (không vượt quá 20 phút).
- [ ] Âm lượng giọng nói to, rõ ràng, không bị rè hoặc có tiếng ồn nền.
- [ ] Cỡ chữ trên màn hình điện thoại và Firebase Console đủ lớn để người xem đọc rõ ràng.
- [ ] Đặt tên file video đúng quy chuẩn của Khoa:  
  `[MaLop]_[Nhom]_[TenDeTai]_Demo.mp4` *(Ví dụ: `503074_Nhom05_StudentMgmt_Demo.mp4`)*.
