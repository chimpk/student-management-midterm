# BÁO CÁO BÀI TẬP GIỮA KỲ
## Ứng dụng di động quản lý thông tin sinh viên với Firebase

> **Trạng thái:** BẢN NHÁP THIẾT KẾ — chưa phải báo cáo nghiệm thu
>
> **Học phần:** Phát triển ứng dụng di động (503074)
>
> **Khoa:** Công nghệ Thông tin, Trường Đại học Tôn Đức Thắng
>
> **Học kỳ:** Học kỳ 1, năm học 2026–2027
>
> **Ngày cập nhật:** 08/10/2026

## Thông tin nhóm

| Hạng mục | Nội dung |
|---|---|
| Giảng viên | `[BẮT BUỘC ĐIỀN]` |
| Lớp học phần / nhóm | `[BẮT BUỘC ĐIỀN]` |
| Thành viên A | Trần Ngọc Tấn — MSSV: 52400158 |
| Thành viên B | Nguyễn Vĩnh Khâm — MSSV: 52400128 |
| Gói ứng dụng | `com.example.studentmgmt` |

Thông tin bàn giao chi tiết được theo dõi tại [Thông tin nhóm và môi trường](team-info.md).

## Tóm tắt

Đề tài hướng tới xây dựng ứng dụng Android quản lý thông tin sinh viên, dùng username/mật khẩu để đăng nhập, Cloud Firestore để lưu dữ liệu và Cloud Storage để lưu ảnh đại diện. Email là thông tin bổ sung, chỉ dùng làm bí danh đăng nhập khi tài khoản có email. Thiết kế dự kiến áp dụng MVVM, đồng bộ thời gian thực và phân quyền ba vai trò: Admin, Manager và Employee.

Tại thời điểm lập bản nháp, kho mã vẫn chủ yếu ở mức **khung ứng dụng Android**, nhưng đã có `ValidationUtils` và unit test cho quy tắc MSSV/mã lớp. Các lớp nghiệp vụ, kho dữ liệu, ViewModel, màn hình chức năng và tích hợp Firebase còn là **thiết kế dự kiến**, chưa được xem là đã triển khai. Báo cáo không tuyên bố sản phẩm đã hoàn thành hoặc sẵn sàng vận hành. Toàn bộ 27 kịch bản kiểm thử hệ thống vẫn ở trạng thái **Chờ test**.

## 1. Bối cảnh và mục tiêu

Ứng dụng được thiết kế nhằm nghiên cứu cách quản lý hồ sơ sinh viên có phân quyền, đồng bộ nhiều thiết bị và hỗ trợ dữ liệu tiếng Việt. Đây là sản phẩm môn học, không phải hệ thống quản lý đào tạo đang vận hành.

Mục tiêu dự kiến:

1. Đăng nhập bằng username và mật khẩu; có thể dùng email thay username khi tài khoản đã khai báo email.
2. Phân quyền Admin, Manager và Employee ở giao diện và Security Rules.
3. Quản lý sinh viên và chứng chỉ.
4. Tìm kiếm, sắp xếp và đồng bộ dữ liệu theo thời gian thực.
5. Nhập, xuất CSV tiếng Việt; mỗi batch tối đa 400 thao tác ghi.
6. Xây dựng kiểm thử đơn vị, giao diện và kịch bản hệ thống có bằng chứng.

## 2. Công nghệ và kiến trúc dự kiến

| Thành phần | Lựa chọn |
|---|---|
| Ngôn ngữ | Java 17 |
| Android | `minSdk 26`, `compileSdk 34`, `targetSdk 34` |
| Giao diện | XML, ViewBinding, Material Components |
| Kiến trúc | MVVM với ViewModel, LiveData và lớp kho dữ liệu |
| Dịch vụ | Dịch vụ xác thực phù hợp, Cloud Firestore, Cloud Storage |
| Kiểm thử dự kiến | JUnit 4, AndroidX Test, Espresso |

Các thành phần MVVM chỉ được chuyển sang trạng thái “đã triển khai” sau khi lớp tương ứng tồn tại trong mã nguồn và có kiểm thử.

## 3. Vai trò và dữ liệu

- **Admin:** dự kiến quản lý tài khoản, trạng thái, lịch sử đăng nhập, sinh viên và chứng chỉ.
- **Manager:** dự kiến quản lý sinh viên, chứng chỉ và CSV; không quản lý tài khoản.
- **Employee:** dự kiến chỉ xem sinh viên/chứng chỉ và cập nhật ảnh đại diện của mình.

Ma trận quyền được mô tả tại [Ma trận phân quyền](rbac-matrix.md).

### 3.1 MSSV TDTU

MSSV có đúng 8 ký tự theo cấu trúc `KYYTSSSS`:

- `K`: mã khoa; Khoa CNTT dùng mã `5`.
- `YY`: hai chữ số cuối của năm tuyển sinh.
- `T`: hệ đào tạo, hiện ghi nhận `0` hoặc `H`.
- `SSSS`: số thứ tự từ `0000` đến `9999`.

Ví dụ: `524H0123`. Mẫu toàn trường dự kiến: `^[0-9A-H][0-9]{2}[0H][0-9]{4}$`.

### 3.2 Mã lớp

Mã lớp được lưu dưới dạng chuỗi đúng 8 ký tự chữ hoa hoặc chữ số, mẫu kiểm tra an toàn: `^[0-9A-Z]{8}$`. Ý nghĩa chi tiết của `H`, `K` và quy tắc đánh số lớp chưa được xác minh bằng văn bản chính thức.

### 3.3 Schema thống nhất

- `users/{uid}`: `uid`, `username`, `email` (tùy chọn), `name`, `age`, `phone`, `status`, `role`, `avatarUrl`, `createdAt`.
- `users/{uid}/loginHistory/{logId}`: `timestamp`, `device`.
- `students/{studentId}`: `studentId`, `name`, `dateOfBirth`, `className`, `faculty`, `createdAt`.
- `students/{studentId}/certificates/{certId}`: `certName`, `issueDate`, `issuedBy`.

Thiết kế hiện tại **không có trường GPA**. Không dùng GPA trong giao diện, CSV, demo hoặc kiểm thử nếu schema chưa được thay đổi chính thức.

## 4. Thiết kế chức năng và bảo mật

Ba tài khoản demo dự kiến thống nhất:

```text
Admin: admin / admin
Manager: manager / manager
Employee: employee / employee
```

Các tài khoản phải được cấp trong **môi trường máy chủ đáng tin cậy**. Ứng dụng Android không được tự seed hoặc tự nâng quyền Admin từ client. Email có thể để trống; nếu có, email được dùng làm bí danh đăng nhập.

Mật khẩu demo có dạng `<username>@123456` nên đáp ứng giới hạn tối thiểu 6 ký tự của Firebase Authentication Email/Password.

Security Rules dự kiến kiểm tra xác thực, vai trò, trạng thái, kiểu dữ liệu, trường được phép thay đổi và độ dài chuỗi. Có file rules không đồng nghĩa rules đã được triển khai hoặc kiểm thử.

Import CSV dự kiến đọc UTF-8, báo lỗi theo dòng và chia dữ liệu thành từng nhóm tối đa **400 thao tác ghi**. Xóa sinh viên phải xử lý chứng chỉ con trước vì Firestore không tự xóa subcollection.

## 5. Kế hoạch kiểm thử và bằng chứng

Danh sách 27 kịch bản nằm tại [Hướng dẫn cài đặt và kế hoạch kiểm thử](test-and-setup.md).

| Bằng chứng | Trạng thái |
|---|---|
| Kiểm thử đơn vị `ValidationUtils` | Đã chạy đạt 6 phương thức test cho username, MSSV và mã lớp |
| Kiểm thử giao diện Espresso | Chưa có kết quả |
| 27 kịch bản hệ thống | Chờ test |
| Ảnh ứng dụng/Firebase | Chưa bổ sung |
| Video demo | Chưa quay |

Không đổi “Chờ test” thành “Đạt” nếu chưa ghi ngày, thiết bị, người test và bằng chứng.

## 6. Trạng thái hiện tại

Kho mã hiện có khung Android, `ValidationUtils` và `ValidationUtilsTest`. Sáu phương thức unit test kiểm tra username, MSSV hợp lệ/không hợp lệ, mã lớp 8 ký tự và nhận diện nhóm mã lớp CNTT đã chạy đạt. Kết quả này chỉ xác nhận phạm vi validation nói trên, không đại diện cho 27 kịch bản hệ thống. Những tên như `StudentRepository`, `CsvHelper`, ViewModel hoặc màn hình nghiệp vụ trong UML vẫn là **thành phần dự kiến**.

## 7. Công việc trước khi nộp

1. Hoàn thiện chức năng tối thiểu theo yêu cầu môn học.
2. Đồng bộ schema, UI, CSV, Rules và tài liệu.
3. Kiểm thử ma trận quyền trên Firebase.
4. Mở rộng unit test và thêm Espresso test.
5. Chạy đủ 27 kịch bản, lưu ảnh/video và điền kết quả.
6. Điền giảng viên, lớp, thành viên B, Firebase Project ID và Storage Bucket.
7. Chỉ xuất PDF sau khi báo cáo phản ánh đúng sản phẩm thực tế.

## 8. Kết luận tạm thời

Bản nháp đã xác định phạm vi, kiến trúc, dữ liệu và kế hoạch kiểm thử. Chưa đủ căn cứ để kết luận ứng dụng hoàn thành hoặc đạt nghiệm thu. Kết luận cuối cùng phải dựa trên mã nguồn, kết quả test và minh chứng thực tế.

## Tài liệu tham khảo

1. Firebase, *Cloud Firestore*: <https://firebase.google.com/docs/firestore>
2. Firebase, *Authentication cho Android*: <https://firebase.google.com/docs/auth/android/start>
3. Firebase, *Security Rules*: <https://firebase.google.com/docs/rules>
4. Android Developers, *Kiến trúc ứng dụng*: <https://developer.android.com/topic/architecture>

## Checklist trước khi nộp

- [ ] Điền giảng viên, lớp/nhóm và thành viên B.
- [ ] Điền Firebase Project ID và Storage Bucket.
- [ ] Hoàn thiện mã nguồn theo phạm vi cam kết.
- [ ] Không còn liên kết tới lớp chưa tồn tại.
- [ ] Chạy và lưu bằng chứng unit test, Espresso và 27 test case.
- [ ] Chụp ảnh ứng dụng, Authentication, Firestore, Rules và Storage.
- [ ] Quay video demo theo chức năng thực tế.
- [ ] Cập nhật kết luận và xuất PDF cuối cùng.
