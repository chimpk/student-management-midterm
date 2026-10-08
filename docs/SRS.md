# Đặc Tả Yêu Cầu Phần Mềm (SRS)

**Dự án:** Ứng dụng Quản lý Thông tin Sinh viên Thời gian Thực
**Môn:** Phát triển ứng dụng di động – 503074 | **Học kỳ:** Giữa kỳ
**Đề bài gốc:** [`Mid-term.pdf`](Mid-term.pdf) (Version 1.0, 13/09/2023)

> Tài liệu này là nguồn chính cho **yêu cầu**. Cấu trúc dữ liệu nằm ở [`firestore-schema.md`](firestore-schema.md), phân quyền chi tiết ở [`rbac-matrix.md`](rbac-matrix.md).
>
> **Trạng thái:** đặc tả mục tiêu cho khung ứng dụng đang phát triển; không phải tuyên bố các chức năng đã hoàn thành.

---

## 1. Mục đích và phạm vi

Ứng dụng Android quản lý thông tin sinh viên theo thời gian thực bằng Firebase Firestore. Hỗ trợ 3 vai trò: Admin, Manager, Employee. Dữ liệu đồng bộ tức thì giữa các thiết bị.

**Ngoài phạm vi:** thanh toán, thông báo push, AI/ML.

---

## 2. Tác nhân

| Tác nhân | Mô tả |
|---|---|
| **Admin** | Tài khoản quản trị đầu tiên được cấp trong Firebase Console hoặc môi trường máy chủ tin cậy, không được tạo từ ứng dụng client. Toàn quyền. Sau đó chỉ Admin mới tạo được tài khoản mới. |
| **Manager** | Thực hiện mọi chức năng liên quan đến sinh viên và chứng chỉ. |
| **Employee** | Chỉ xem nội dung; được phép đổi ảnh đại diện của chính mình. |

---

## 3. Yêu cầu chức năng

### 3.1 Tài khoản

| Mã | Mô tả | Vai trò | Tiêu chí chấp nhận |
|---|---|---|---|
| FR-ACC-01 | Đăng nhập bằng username/mật khẩu; email là bí danh tùy chọn | Tất cả | Chấp nhận username. Chỉ chấp nhận email khi tài khoản có email. Tài khoản `Locked` → từ chối. Sai thông tin → hiện lỗi. Không có document `users/{uid}` → từ chối. |
| FR-ACC-02 | Đổi ảnh đại diện | Tất cả | Chọn ảnh từ thư viện, lưu lên Firebase Storage, cập nhật `avatarUrl` trong Firestore. |
| FR-ACC-03 | Xem lịch sử đăng nhập của chính mình | Tất cả | Danh sách thời gian đăng nhập, mới nhất trước. *(Mở rộng thêm so với đề.)* |
| FR-ACC-04 | Cấp ba tài khoản demo ban đầu | Người quản trị dự án | Tạo trong môi trường tin cậy; ứng dụng client không có luồng tự nâng quyền. Tài khoản dự kiến: `admin/admin`, `manager/manager`, `employee/employee`. |

### 3.2 Quản lý người dùng

| Mã | Mô tả | Vai trò | Tiêu chí chấp nhận |
|---|---|---|---|
| FR-USR-01 | Xem danh sách người dùng | Admin | Hiển thị tên, tuổi, SĐT, trạng thái. |
| FR-USR-02 | Thêm người dùng | Admin | Theo đề: Tên, Tuổi, SĐT, Trạng thái (`Normal`/`Locked`). Thêm để tạo được tài khoản đăng nhập: Username, Mật khẩu, Vai trò. Email là tùy chọn. |
| FR-USR-03 | Sửa thông tin người dùng | Admin | Cập nhật Firestore tức thì. |
| FR-USR-04 | Xóa người dùng | Admin | Xác nhận trước khi xóa. Xóa document `users/{uid}` (xem ghi chú ở `firestore-schema.md`). |
| FR-USR-05 | Khóa/mở khóa tài khoản | Admin | Đổi `status` giữa `Normal` và `Locked`. Tài khoản `Locked` không đăng nhập được. |
| FR-USR-06 | Xem lịch sử đăng nhập của người dùng bất kỳ | Admin | Danh sách thời gian + thiết bị. |

### 3.3 Quản lý sinh viên

| Mã | Mô tả | Vai trò | Tiêu chí chấp nhận |
|---|---|---|---|
| FR-STU-01 | Xem danh sách sinh viên | Tất cả | Tải thời gian thực bằng `addSnapshotListener`. |
| FR-STU-02 | Thêm sinh viên | Admin, Manager | Bắt buộc: Mã SV, Họ tên, Ngày sinh, Lớp, Khoa. |
| FR-STU-03 | Sửa thông tin sinh viên | Admin, Manager | Cập nhật ngay lên Firestore. Không đổi được Mã SV. |
| FR-STU-04 | Xóa sinh viên | Admin, Manager | Xóa kèm tất cả chứng chỉ liên quan. |
| FR-STU-05 | Tìm kiếm đa tiêu chí | Tất cả | Tìm theo tên, mã SV, lớp, khoa (kết hợp). |
| FR-STU-06 | Sắp xếp đa tiêu chí | Tất cả | Sắp xếp theo tên, ngày sinh, lớp (tăng/giảm). |
| FR-STU-07 | Trang chi tiết sinh viên | Tất cả | Xem đầy đủ thông tin + danh sách chứng chỉ. |

### 3.4 Quản lý chứng chỉ

| Mã | Mô tả | Vai trò | Tiêu chí chấp nhận |
|---|---|---|---|
| FR-CER-01 | Xem danh sách chứng chỉ của sinh viên | Tất cả | Hiện trong trang chi tiết sinh viên. |
| FR-CER-02 | Thêm chứng chỉ | Admin, Manager | Bắt buộc: Tên chứng chỉ, Ngày cấp, Đơn vị cấp. |
| FR-CER-03 | Sửa chứng chỉ | Admin, Manager | Cập nhật ngay lên Firestore. |
| FR-CER-04 | Xóa chứng chỉ | Admin, Manager | Xác nhận trước khi xóa. |

### 3.5 Nhập/xuất dữ liệu

| Mã | Mô tả | Vai trò | Tiêu chí chấp nhận |
|---|---|---|---|
| FR-IO-01 | Import sinh viên từ file CSV | Admin, Manager | Đọc file qua SAF, chấp nhận UTF-8 có hoặc không có BOM, ghi tối đa 400 document mỗi batch và báo lỗi từng dòng sai. |
| FR-IO-02 | Export sinh viên ra CSV | Admin, Manager | Ghi file qua SAF, UTF-8 BOM, đủ các cột. |
| FR-IO-03 | Import chứng chỉ từ file CSV | Admin, Manager | Ánh xạ theo Mã SV, bỏ qua và báo cáo dòng không hợp lệ. |
| FR-IO-04 | Export chứng chỉ ra CSV | Admin, Manager | Ghi file, bao gồm Mã SV. |

---

## 4. Quy tắc kiểm tra dữ liệu

Đề bài không quy định các quy tắc này, đây là quy tắc do nhóm đặt.

| Trường | Quy tắc |
|---|---|
| Tên / Họ tên | Bắt buộc, 2–100 ký tự |
| Tuổi (người dùng) | Số nguyên, 18–60 |
| Số điện thoại | 10 chữ số, bắt đầu bằng 0 |
| Trạng thái (người dùng) | Chỉ nhận `Normal` hoặc `Locked` (giao diện hiển thị "Bình thường" / "Khóa") |
| Username | Bắt buộc, duy nhất, 3–32 ký tự; bắt đầu bằng chữ thường; chỉ gồm `a-z`, `0-9`, `.`, `_`, `-` |
| Email | Không bắt buộc; nếu có phải đúng định dạng, duy nhất và có thể dùng làm bí danh đăng nhập |
| Mật khẩu demo | Theo đề bài: `admin`, `manager`, `employee`; chỉ dùng trong phạm vi demo môn học |
| Mã sinh viên | Đúng 8 ký tự theo cấu trúc TDTU `KYYTSSSS`; biểu thức `^[0-9A-H][0-9]{2}[0H][0-9]{4}$`; bắt buộc, duy nhất và không đổi sau khi tạo |
| Mã lớp | Đúng 8 ký tự chữ hoa hoặc chữ số; biểu thức `^[0-9A-Z]{8}$` |
| Ngày sinh | Định dạng `YYYY-MM-DD`, không ở tương lai |
| Ngày cấp chứng chỉ | Định dạng `YYYY-MM-DD`, không ở tương lai |

Trong MSSV, `K` là mã khoa (`0`–`9`, `A`–`H`), `YY` là hai chữ số cuối của năm tuyển sinh, `T` là hệ đào tạo (`0` tiêu chuẩn hoặc `H` chất lượng cao), và `SSSS` là số thứ tự `0000`–`9999`. Ứng dụng chỉ **kiểm tra** MSSV do người dùng nhập hoặc từ CSV, không tự sinh MSSV và không suy diễn số thứ tự từ họ tên.

Mã khoa được dùng: `0` Ngoại ngữ; `1` Mỹ thuật công nghiệp; `2` Kế toán; `3` Khoa học xã hội và Nhân văn; `4` Điện - Điện tử; `5` Công nghệ thông tin; `6` Khoa học ứng dụng; `7` Quản trị kinh doanh; `8` Kỹ thuật công trình; `9` Môi trường và Bảo hộ lao động; `A` Lao động Công đoàn; `B` Tài chính - Ngân hàng; `C` Toán - Thống kê; `D` Khoa học thể thao; `E` Luật; `F`, `G` Giáo dục quốc tế; `H` Dược.

Các dạng mã lớp CNTT như `YY05(02|03|04)NN` hoặc `YYH5(02|03|04)NN` mới chỉ được quan sát từ dữ liệu thực tế; ý nghĩa đầy đủ của `H`, `K` và cách đánh số lớp chưa có văn bản chính thức. Vì vậy hệ thống chỉ áp dụng quy tắc an toàn gồm đúng 8 chữ hoa hoặc chữ số.

---

## 5. Yêu cầu phi chức năng

| Mã | Yêu cầu |
|---|---|
| NFR-01 | **Thời gian thực:** Dữ liệu cập nhật < 2 giây qua `addSnapshotListener`. |
| NFR-02 | **Bảo mật:** Firestore rules kiểm tra vai trò trước mọi thao tác ghi. |
| NFR-03 | **Offline:** Firestore offline persistence bật sẵn, hiện badge khi mất mạng. |
| NFR-04 | **Khả năng sử dụng:** Thông báo lỗi rõ ràng, không trắng màn hình khi tải. |
| NFR-05 | **Tương thích:** `minSdk` 26 (Android 8.0+), theo `app/build.gradle.kts`. |
| NFR-06 | **Ổn định:** Không crash trong suốt buổi demo. |

---

## 6. Đối chiếu với đề bài

| Đề bài (`Mid-term.pdf`) | Cách nhóm thực hiện |
|---|---|
| Dùng Firebase Authentication "không bắt buộc nhưng là lựa chọn tốt". | Username/mật khẩu là giao diện đăng nhập chính. Email là thông tin tùy chọn và chỉ là bí danh đăng nhập khi có. Nếu dùng Firebase Email/Password, phải đổi mật khẩu `admin` vì provider này yêu cầu tối thiểu 6 ký tự, hoặc triển khai cơ chế xác thực khác trong môi trường tin cậy. |
| Thêm người dùng gồm: Name, Age, Phone Number, Status (Normal/Locked). | Bốn trường này là bắt buộc. Nhóm thêm Username, Mật khẩu và Vai trò; Email không bắt buộc. |
| Tài khoản admin tích hợp sẵn, không cần tạo trong giao diện. Các tài khoản khác do admin tạo. | FR-ACC-04 và FR-USR-02. Bộ ba demo được chuẩn bị từ môi trường tin cậy; client không có đường tự nâng quyền. |
| Export ra Excel/CSV. | Chọn CSV. |
| Import "từ một file". | Chọn CSV. |
| Employee chỉ xem nội dung, chỉ được đổi ảnh đại diện. | Employee xem sinh viên và chứng chỉ. *Giả định của nhóm:* danh sách người dùng chỉ Admin xem được. |
| Manager làm mọi chức năng liên quan đến sinh viên. | Manager: sinh viên, chứng chỉ, import/export. Không quản lý người dùng. |
| Xem lịch sử đăng nhập của một người dùng. | FR-USR-06 (Admin xem của bất kỳ ai). FR-ACC-03 là phần mở rộng. |
| Đề không nêu danh sách trường của sinh viên và chứng chỉ. | Nhóm chọn các trường ở FR-STU-02 và FR-CER-02. |

**Yêu cầu nộp bài theo đề:** báo cáo PDF; video demo (có âm thanh, tối đa 20 phút, 720p); toàn bộ source code, thư viện, database và hướng dẫn chạy; **tài khoản Admin** để giám khảo truy cập ứng dụng.

**Điểm trừ theo đề:** thiếu video demo (tối đa 2.0 điểm), chia việc không đều, nộp muộn hoặc nộp qua email cá nhân, sai định dạng (thiếu thông tin thành viên, thiếu hướng dẫn chạy, thiếu tài khoản Admin).

**Thang điểm sản phẩm theo đề (6.0):** Account Management 1.5, User Management 2.0, Data Import/Export 1.0, User Interface 1.0, Stability 0.5. **Báo cáo (4.0):** Content 1.0 và bốn mục 0.75 (Correctness, Presentation/writing style, Pictures-tables-diagrams, Cover-ToC-appendix-reference).
