# Sổ tay Công việc theo Sprint dành cho Thành viên 2 (Nguyễn Vĩnh Khâm)

> **Họ và tên:** Nguyễn Vĩnh Khâm — **MSSV:** 52400128
>
> **Vai trò:** Thành viên 2 (Member 2 / B)
>
> **Phạm vi trách nhiệm:** Quản lý sinh viên (Student CRUD), Quản lý chứng chỉ (Certificate CRUD), Nhập/Xuất CSV (CSV Import/Export), Bộ dữ liệu mẫu tiếng Việt, Kiểm thử chéo và Hỗ trợ quay video demo.
>
> **Tài liệu tham chiếu:** [`SRS.md`](SRS.md), [`firestore-schema.md`](firestore-schema.md), [`rbac-matrix.md`](rbac-matrix.md), [`firebase-setup.md`](firebase-setup.md).

---

## 1. Hiện trạng mã nguồn đã bàn giao sẵn (Scaffolded & Ready)

Trưởng nhóm (Thành viên 1) đã xây dựng sẵn toàn bộ khung (scaffold) hoàn chỉnh, giao diện Material 3, ViewBinding và nạp dữ liệu mẫu in-memory (Mock) kèm nhãn `TODO(member-2)`. Dự án hiện tại đã **vượt qua 170/170 bài kiểm thử đơn vị** (bao gồm 24 unit tests mới cho Student, Certificate, ViewModel và CSV) và build APK debug thành công.

### Danh mục tệp Thành viên 2 sở hữu và cần làm việc:
1. **Mô hình dữ liệu (Model):**
   - [`app/src/main/java/com/example/studentmgmt/model/Student.java`](../app/src/main/java/com/example/studentmgmt/model/Student.java)
   - [`app/src/main/java/com/example/studentmgmt/model/Certificate.java`](../app/src/main/java/com/example/studentmgmt/model/Certificate.java)
2. **Kho lưu trữ (Repository & Provider):**
   - [`app/src/main/java/com/example/studentmgmt/data/StudentRepository.java`](../app/src/main/java/com/example/studentmgmt/data/StudentRepository.java)
   - [`app/src/main/java/com/example/studentmgmt/data/CertificateRepository.java`](../app/src/main/java/com/example/studentmgmt/data/CertificateRepository.java)
   - [`app/src/main/java/com/example/studentmgmt/data/MockStudentRepository.java`](../app/src/main/java/com/example/studentmgmt/data/MockStudentRepository.java) (Dữ liệu mẫu in-memory)
   - [`app/src/main/java/com/example/studentmgmt/data/StudentRepositoryProvider.java`](../app/src/main/java/com/example/studentmgmt/data/StudentRepositoryProvider.java)
3. **Tiện ích CSV:**
   - [`app/src/main/java/com/example/studentmgmt/utils/StudentCsvHelper.java`](../app/src/main/java/com/example/studentmgmt/utils/StudentCsvHelper.java)
4. **Giao diện & Điều khiển (UI & ViewModel):**
   - [`app/src/main/java/com/example/studentmgmt/ui/student/StudentListViewModel.java`](../app/src/main/java/com/example/studentmgmt/ui/student/StudentListViewModel.java)
   - [`app/src/main/java/com/example/studentmgmt/ui/student/StudentDetailViewModel.java`](../app/src/main/java/com/example/studentmgmt/ui/student/StudentDetailViewModel.java)
   - [`app/src/main/java/com/example/studentmgmt/ui/student/StudentAdapter.java`](../app/src/main/java/com/example/studentmgmt/ui/student/StudentAdapter.java)
   - [`app/src/main/java/com/example/studentmgmt/ui/student/CertificateAdapter.java`](../app/src/main/java/com/example/studentmgmt/ui/student/CertificateAdapter.java)
   - [`app/src/main/java/com/example/studentmgmt/ui/student/StudentListFragment.java`](../app/src/main/java/com/example/studentmgmt/ui/student/StudentListFragment.java)
   - [`app/src/main/java/com/example/studentmgmt/ui/student/StudentDetailFragment.java`](../app/src/main/java/com/example/studentmgmt/ui/student/StudentDetailFragment.java)
5. **Giao diện XML (Layouts):**
   - `fragment_student_list.xml`, `item_student.xml`, `fragment_student_detail.xml`, `item_certificate.xml`, `dialog_student_add_edit.xml`, `dialog_certificate_add.xml`.

---

## 2. Kế hoạch công việc chi tiết qua 6 Sprint

```
Sprint 1 ──> Sprint 2 ──> Sprint 3 ──> Sprint 4 ──> Sprint 5 ──> Sprint 6
Realtime     CRUD Sinh    CRUD Chứng   Sắp xếp &   Nhập CSV     Kiểm thử &
Danh sách    viên         chỉ & Tìm    Xuất CSV    & Viết BC    Video Demo
                          kiếm
```

---

### 🟢 SPRINT 1: Nền tảng & Hiển thị danh sách sinh viên Realtime
*Thời gian dự kiến: Tuần 1 (CN 11/10 – T4 14/10)*

- **Mục tiêu:** Mở ứng dụng thấy danh sách sinh viên cập nhật theo thời gian thực (Realtime sync) từ Cloud Firestore.
- **Mã Task cần làm:**
  - `T04`: Danh sách sinh viên + RecyclerView realtime.
  - `T09`: Màn hình xem chi tiết sinh viên cơ bản.
- **Các tệp cần chỉnh sửa / tạo mới:**
  1. Tạo `FirestoreStudentRepository.java` trong gói `com.example.studentmgmt.data`:
     - Sử dụng `FirebaseFirestore.getInstance().collection("students")`.
     - Lắng nghe realtime bằng `addSnapshotListener((snapshots, e) -> ...)`.
     - Ánh xạ Document Snapshot thành đối tượng `Student.class`.
  2. Cập nhật `StudentRepositoryProvider.java`:
     - Chuyển `instance` mặc định từ `MockStudentRepository` sang `FirestoreStudentRepository`.
  3. Kiểm tra hiển thị trên `StudentListFragment.java` và `StudentAdapter.java`.
- **Tiêu chí nghiệm thu (Acceptance Criteria):**
  - Mở ứng dụng trên máy Android, thêm 1 document vào collection `students` trên web Firebase Console, màn hình app lập tức cập nhật sinh viên mới trong vòng dưới 2 giây.

---

### 🟢 SPRINT 2: Phân quyền & Thêm / Sửa / Xóa sinh viên
*Thời gian dự kiến: Tuần 1 (T5 15/10 – T7 17/10)*

- **Mục tiêu:** Thực hiện thêm, sửa thông tin sinh viên và xóa sinh viên kèm toàn bộ chứng chỉ con; áp dụng phân quyền RBAC (chỉ Admin và Manager được ghi, Employee chỉ xem).
- **Mã Task cần làm:**
  - `T08`: Thêm/sửa/xóa sinh viên + xóa kèm chứng chỉ (Cascade delete).
- **Các tệp cần chỉnh sửa / tạo mới:**
  1. Trong `FirestoreStudentRepository.java`:
     - `addStudent(Student, callback)`: Ghi document vào `students/{studentId}` với `set()`. Kiểm tra trùng MSSV trước khi ghi.
     - `updateStudent(Student, callback)`: Cập nhật thông tin (Họ tên, ngày sinh, lớp, khoa). **Khóa không cho đổi trường `studentId`**.
     - `deleteStudent(studentId, callback)`: Xóa document `students/{studentId}` đồng thời truy vấn subcollection `certificates` và xóa toàn bộ chứng chỉ bên trong bằng `WriteBatch`.
  2. Trong `StudentListFragment.java`:
     - Kiểm tra vai trò của người dùng hiện tại (`AuthRepositoryProvider.getInstance().getCurrentUser().getRole()`):
       - Nếu vai trò là `employee`: Ẩn FloatingActionButton (nút thêm) và ẩn nút "Sửa", "Xóa" trên từng thẻ sinh viên.
       - Nếu vai trò là `admin` hoặc `manager`: Hiển thị đầy đủ các nút chức năng.
  3. Kiểm tra validation theo [`SRS.md`](SRS.md):
     - MSSV: Bắt buộc đúng 8 ký tự theo định dạng `^[0-9A-H][0-9]{2}[0H][0-9]{4}$`.
     - Mã lớp: Bắt buộc đúng 8 ký tự `^[0-9A-Z]{8}$`.
     - Họ tên: 2 – 100 ký tự.
- **Tiêu chí nghiệm thu:**
  - Đăng nhập tài khoản `employee`: Chỉ xem được danh sách, không có nút thêm/sửa/xóa.
  - Đăng nhập tài khoản `manager` hoặc `admin`: Thêm/sửa/xóa thành công. Xóa sinh viên làm sạch cả subcollection chứng chỉ.

---

### 🟢 SPRINT 3: Nghiệp vụ Chứng chỉ & Tìm kiếm đa tiêu chí
*Thời gian dự kiến: Tuần 2 (CN 18/10 – T4 21/10)*

- **Mục tiêu:** Quản lý chứng chỉ con của từng sinh viên và tìm kiếm sinh viên tức thì bằng tiếng Việt.
- **Mã Task cần làm:**
  - `T14`: CRUD chứng chỉ trong màn hình chi tiết sinh viên.
  - `T12`: Tìm kiếm đa tiêu chí thời gian thực.
- **Các tệp cần chỉnh sửa / tạo mới:**
  1. Tạo `FirestoreCertificateRepository.java`:
     - Trỏ vào subcollection `students/{studentId}/certificates`.
     - `listenCertificates`: Lắng nghe realtime danh sách chứng chỉ của sinh viên được chọn.
     - `addCertificate`, `updateCertificate`, `deleteCertificate`: Thao tác document chứng chỉ (`certName`, `issueDate`, `issuedBy`).
  2. Trong `StudentDetailFragment.java` & `StudentDetailViewModel.java`:
     - Kết nối repository Firestore chứng chỉ vào ViewModel.
     - Xử lý mở hộp thoại thêm chứng chỉ (`dialog_certificate_add.xml`).
     - Có hộp thoại AlertDialog xác nhận trước khi xóa chứng chỉ.
  3. Trong `StudentListViewModel.java`:
     - Nâng cấp hàm `applyFilters()`: Hỗ trợ tìm kiếm tiếng Việt không phân biệt dấu (ví dụ gõ "nguyen van an" hay "Nguyễn Văn An" đều ra kết quả) bằng cách chuẩn hóa qua `java.text.Normalizer`.
     - Cho phép tìm kiếm kết hợp: MSSV, Họ tên, Mã lớp.
- **Tiêu chí nghiệm thu:**
  - Bấm vào một sinh viên, danh sách chứng chỉ tải lên chính xác. Bấm thêm chứng chỉ hiển thị ngay lập tức.
  - Gõ từ khóa tìm kiếm trên thanh search, danh sách lọc realtime không bị giật lag.

---

### 🟢 SPRINT 4: Sắp xếp dữ liệu & Xuất tệp CSV
*Thời gian dự kiến: Tuần 2 (T5 22/10 – T7 24/10)*

- **Mục tiêu:** Sắp xếp danh sách sinh viên chuẩn tiếng Việt và xuất danh sách sinh viên/chứng chỉ ra tệp CSV mở bằng Microsoft Excel không lỗi font.
- **Mã Task cần làm:**
  - `T13`: Sắp xếp theo tên, ngày sinh, lớp.
  - `T18`: Export danh sách sinh viên ra CSV (UTF-8 BOM, Storage Access Framework).
  - `T26`: Export danh sách chứng chỉ ra CSV.
- **Các tệp cần chỉnh sửa / tạo mới:**
  1. Trong `StudentListViewModel.java`:
     - Bổ sung chức năng sắp xếp sử dụng `java.text.Collator.getInstance(new Locale("vi", "VN"))`:
       - Sắp xếp theo Họ tên (A $\rightarrow$ Z và Z $\rightarrow$ A).
       - Sắp xếp theo Ngày sinh.
       - Sắp xếp theo Lớp.
  2. Trong `StudentCsvHelper.java`:
     - Bổ sung ký tự **BOM (Byte Order Mark: `\uFEFF`)** ở đầu tệp CSV khi xuất:
       ```java
       public static final String UTF8_BOM = "\uFEFF";
       ```
       *(Điều này giúp Microsoft Excel trên Windows tự động nhận diện tiếng Việt có dấu mà không bị lỗi font ô vuông).*
  3. Trong `StudentListFragment.java`:
     - Bấm nút "Xuất CSV" $\rightarrow$ Kích hoạt `ActivityResultContracts.CreateDocument("text/csv")` để người dùng chọn thư mục lưu tệp trên điện thoại/máy ảo.
- **Tiêu chí nghiệm thu:**
  - Xuất tệp CSV từ ứng dụng, mở trực tiếp bằng Excel trên máy tính: Các ký tự tiếng Việt "Nguyễn Văn An", "Công nghệ thông tin" hiển thị hoàn hảo, không bị biến thành ký tự lạ.

---

### 🟢 SPRINT 5: Nhập tệp CSV & Hoàn thiện phần Báo cáo
*Thời gian dự kiến: Tuần 3 (CN 25/10 – T4 28/10)*

- **Mục tiêu:** Nhập dữ liệu sinh viên/chứng chỉ hàng loạt từ tệp CSV có kiểm tra hợp lệ từng dòng và chia lô (batching); viết phần báo cáo của Thành viên 2.
- **Mã Task cần làm:**
  - `T17`: Import sinh viên từ CSV (chia batch $\le 400$ thao tác ghi mỗi lần).
  - `T19`: Import chứng chỉ theo MSSV từ CSV.
  - `T27`: Viết nội dung phần Thành viên 2 trong báo cáo môn học.
- **Các tệp cần chỉnh sửa / tạo mới:**
  1. Trong `StudentCsvHelper.java` & `FirestoreStudentRepository.java`:
     - Đọc tệp CSV được người dùng chọn qua `ActivityResultContracts.GetContent()`.
     - Phân tích cú pháp từng dòng:
       - Dòng nào sai định dạng MSSV (không khớp 8 ký tự TDTU) hoặc thiếu họ tên $\rightarrow$ Bỏ qua và gom vào danh sách lỗi kèm số dòng.
       - Dòng nào hợp lệ $\rightarrow$ Gom vào danh sách thêm.
     - Ghi vào Firestore bằng `firestore.batch()`: Firestore giới hạn tối đa 500 thao tác/batch, do đó hãy chia nhỏ thành từng đợt **tối đa 400 sinh viên mỗi batch**.
     - Trả về thông báo tổng kết: *"Đã nhập thành công X sinh viên, bỏ qua Y dòng lỗi."*
  2. Trong `docs/final-report.md`:
     - Cập nhật Mục 3 (Phân định phạm vi Thành viên 2) và Mục 4 (Kết quả kiểm thử của Thành viên 2): Mô tả kiến trúc repository, màn hình danh sách, chi tiết, chứng chỉ và xử lý CSV.
- **Tiêu chí nghiệm thu:**
  - Chọn 1 file CSV có 50 sinh viên hợp lệ và 2 dòng cố tình viết sai MSSV: Hệ thống nạp đúng 50 sinh viên lên Firestore và hiển thị thông báo chi tiết 2 dòng bị bỏ qua.

---

### 🟢 SPRINT 6: Kiểm thử chéo, Dữ liệu mẫu & Quay video Demo
*Thời gian dự kiến: Tuần 3 (T5 29/10 – T7 31/10)*

- **Mục tiêu:** Kiểm thử chéo phân hệ của Thành viên 1, hoàn thiện bộ dữ liệu mẫu nộp bài và phối hợp quay video thuyết minh demo ứng dụng.
- **Mã Task cần làm:**
  - `T20`: Kiểm thử chéo phần Auth / Quản lý người dùng của Thành viên 1.
  - `T21`: Kiểm thử thâm nhập biên & Security Rules (dùng Employee cố tình ghi sinh viên).
  - `T23`: Chuẩn bị tệp dữ liệu mẫu `sample-data/students_sample.csv` và kịch bản demo.
- **Các việc cần làm:**
  1. Tạo thư mục `sample-data/` chứa tệp mẫu:
     - `students_sample.csv`: Chứa khoảng 20 sinh viên mẫu TDTU thuộc các khoa CNTT, KHMT, KTPM.
     - `certificates_sample.csv`: Chứa các chứng chỉ mẫu TOEIC, IELTS, MOS.
  2. Mở tệp [`docs/test-and-setup.md`](test-and-setup.md):
     - Ghi nhận kết quả test chéo các tính năng của Thành viên 1 (Đăng nhập, khóa tài khoản, đổi avatar).
  3. Phối hợp với Trưởng nhóm (Tấn) quay video demo ($\le 20$ phút, độ phân giải 720p trở lên):
     - Mem 1 demo phần: Đăng nhập, Admin khóa user, đổi avatar, xem lịch sử đăng nhập.
     - Mem 2 demo phần: Xem danh sách sinh viên realtime, tìm kiếm, thêm/sửa/xóa sinh viên, thêm chứng chỉ, xuất CSV ra Excel và nạp CSV vào hệ thống.
- **Tiêu chí nghiệm thu:**
  - Toàn bộ 27 kịch bản kiểm thử trong `test-and-setup.md` đều có kết quả `PASS`.
  - Có đường link video demo hoàn chỉnh nộp cùng mã nguồn.

---

## 3. Lệnh kiểm tra bắt buộc trước khi bàn giao mã nguồn

Mỗi khi hoàn thành xong một chức năng hoặc trước khi báo cáo cho Trưởng nhóm, Thành viên 2 mở terminal tại thư mục dự án và chạy lệnh sau:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug --rerun-tasks --console=plain
```

- **Yêu cầu bắt buộc:**
  - Kết quả phải hiện `BUILD SUCCESSFUL`.
  - Toàn bộ bài kiểm thử đơn vị (`testDebugUnitTest`) phải đạt **PASS 100%**, không có failure hay compilation error.
  - Tệp APK debug được sinh ra thành công tại: `app/build/outputs/apk/debug/app-debug.apk`.
