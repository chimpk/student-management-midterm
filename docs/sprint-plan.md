# Kế hoạch chia việc theo Sprint

> **Thời gian:** 3 tuần, từ **Chủ nhật 11/10/2026** đến **Thứ Bảy 31/10/2026**.
> **Nhịp sprint:** mỗi tuần chia 2 sprint (CN–T4 và T5–T7), tổng cộng **6 sprint**.
> **Thành viên:** **A** – Trần Ngọc Tấn (trưởng nhóm), **B** – Nguyễn Vĩnh Khâm.
> Mã task giữ nguyên theo `project-progress.xlsx`; các task mới được đánh số T00, T26–T28.

---

## 1. Tổng quan lịch

| Sprint | Thời gian | Mục tiêu sprint |
|---|---|---|
| **S1** | CN 11/10 – T4 14/10 | Nền móng: sửa khung dự án, kết nối Firebase, đăng nhập, danh sách sinh viên realtime |
| **S2** | T5 15/10 – T7 17/10 | Phân quyền: điều hướng theo vai trò, Security Rules, CRUD sinh viên |
| **S3** | CN 18/10 – T4 21/10 | Nghiệp vụ chính: quản lý người dùng, khóa/mở khóa, chứng chỉ, tìm kiếm |
| **S4** | T5 22/10 – T7 24/10 | Hoàn thiện tài khoản & bắt đầu dữ liệu: avatar, lịch sử đăng nhập, sắp xếp, export |
| **S5** | CN 25/10 – T4 28/10 | Import CSV, UI/offline, viết báo cáo |
| **S6** | T5 29/10 – T7 31/10 | Kiểm thử chéo, quay video demo, đóng gói nộp bài |

---

## 2. Chi tiết từng sprint

### S1 · CN 11/10 – T4 14/10 · Nền móng

| Task | Công việc | Người làm | Giờ | Sản phẩm |
|---|---|---|---|---|
| T00 | Sửa khung: thêm `gradlew`, sửa `pathData` icon, bỏ màu chữ cứng ở dark mode, bỏ nút back ở màn hình chính | A | 1 | Build được trên Windows/macOS/Linux |
| T01 | Cấu hình Firebase (Auth, Firestore, Storage), tạo 3 tài khoản demo | A | 3 | `FirebaseClient.java`, `AuthRepository.java` |
| T02 | Màn hình đăng nhập bằng **username**/mật khẩu (email là bí danh tùy chọn) | A | 4 | `LoginActivity.java`, `activity_login.xml` |
| T04 | Danh sách sinh viên + RecyclerView realtime | B | 5 | `StudentListFragment.java`, `StudentAdapter.java` |
| T09 | Màn hình chi tiết sinh viên | B | 3 | `StudentDetailFragment.java`, `fragment_student_detail.xml` |

**Kết quả cuối sprint:** đăng nhập được bằng `admin/admin@123456`, thấy danh sách sinh viên cập nhật realtime.

### S2 · T5 15/10 – T7 17/10 · Phân quyền

| Task | Công việc | Người làm | Giờ | Sản phẩm |
|---|---|---|---|---|
| T03 | Điều hướng theo vai trò sau đăng nhập (ẩn menu theo role) | A | 3 | `MainActivity.java`, `nav_graph.xml` |
| T11 | Firestore & Storage Rules cho 3 vai trò, chặn tài khoản `Locked` | A | 4 | `firestore.rules`, `storage.rules` (B review chéo) |
| T08 | Thêm/sửa/xóa sinh viên + xóa kèm chứng chỉ | B | 5 | `StudentRepository.java`, `StudentEditFragment.java` |

**Kết quả cuối sprint:** Employee không thấy nút Thêm/Sửa/Xóa; Manager/Admin CRUD được sinh viên.

### S3 · CN 18/10 – T4 21/10 · Nghiệp vụ chính

| Task | Công việc | Người làm | Giờ | Sản phẩm |
|---|---|---|---|---|
| T05 | Danh sách người dùng + thêm/sửa/xóa (Admin) | A | 5 | `UserListFragment.java`, `UserEditFragment.java` |
| T06 | Khóa/mở khóa người dùng + chặn đăng nhập | A | 3 | `UserRepository.java` |
| T14 | CRUD chứng chỉ trong chi tiết sinh viên | B | 4 | `CertificateRepository.java`, `CertificateEditFragment.java` |
| T12 | Tìm kiếm đa tiêu chí realtime (tiếng Việt có dấu) | B | 4 | `StudentViewModel.java` |

**Kết quả cuối sprint:** Admin quản lý được tài khoản; tài khoản bị khóa không đăng nhập được.

### S4 · T5 22/10 – T7 24/10 · Tài khoản & dữ liệu

| Task | Công việc | Người làm | Giờ | Sản phẩm |
|---|---|---|---|---|
| T07 | Đổi ảnh đại diện (mọi vai trò) | A | 3 | `ProfileFragment.java`, Storage `avatars/{uid}` |
| T10 | Ghi và xem lịch sử đăng nhập theo người dùng | A | 3 | `LoginHistoryFragment.java`, `LoginRecordAdapter.java` |
| T13 | Sắp xếp theo tên, ngày sinh, lớp | B | 3 | `StudentViewModel.java` (Collator tiếng Việt) |
| T18 | Export danh sách sinh viên ra CSV (UTF-8 BOM, SAF) | B | 3 | `CsvHelper.java` |
| T26 | Export danh sách chứng chỉ ra CSV | B | 2 | `CsvHelper.java` |

**Kết quả cuối sprint:** đủ toàn bộ chức năng tài khoản; xuất được CSV mở bằng Excel không lỗi font.

### S5 · CN 25/10 – T4 28/10 · Import, UI, báo cáo

| Task | Công việc | Người làm | Giờ | Sản phẩm |
|---|---|---|---|---|
| T15 | Hoàn thiện UI (Material, dark mode, loading) | A | 4 | Theme, layout |
| T16 | Xử lý lỗi, mất mạng, thông báo | A | 3 | Firestore offline cache, Snackbar |
| T27 | Báo cáo (tiếng Anh): chương Firestore + phần Auth/User/Rules | A | 3 | `docs/final-report.md` |
| T17 | Import sinh viên từ CSV (chia batch ≤ 500) | B | 5 | `CsvHelper.java`, `ImportExportFragment.java` |
| T19 | Import chứng chỉ theo MSSV | B | 3 | `CsvHelper.java` |
| T27 | Báo cáo (tiếng Anh): phần Student/Certificate/CSV | B | 3 | `docs/final-report.md` |

**Kết quả cuối sprint:** đủ 100% chức năng theo đề; bản nháp báo cáo.

### S6 · T5 29/10 – T7 31/10 · Kiểm thử & nộp bài

| Task | Công việc | Người làm | Giờ | Sản phẩm |
|---|---|---|---|---|
| T22 | Kiểm thử chéo phần Student/CSV của B | A | 4 | Bảng kết quả trong `docs/test-and-setup.md` |
| T24 | Thuyết minh & quay video demo (≤ 20 phút, 720p) | A | 3 | Video MP4 |
| T28 | Hoàn thiện báo cáo PDF (bìa, mục lục, hình có chú thích, tài liệu tham khảo) | A | 2 | Báo cáo PDF |
| T25 | Vá lỗi cuối, chạy thử trên máy sạch, đóng gói nộp | A | 2 | Gói nộp bài |
| T20 | Kiểm thử chéo phần Auth/User của A | B | 4 | Bảng kết quả trong `docs/test-and-setup.md` |
| T21 | Kiểm thử biên & Security Rules (truy cập trái quyền) | B | 3 | Ảnh chụp Rules Playground |
| T23 | Chuẩn bị dữ liệu demo, thiết bị quay | B | 3 | `sample-data/`, `docs/demo-script.md` |

**Kết quả cuối sprint:** nộp đủ mã nguồn, báo cáo PDF và video.

---

## 3. Cân bằng khối lượng

| Sprint | A (giờ) | B (giờ) |
|---|---|---|
| S1 | 8 | 8 |
| S2 | 7 | 5 |
| S3 | 8 | 8 |
| S4 | 6 | 8 |
| S5 | 10 | 11 |
| S6 | 11 | 10 |
| **Tổng** | **50** | **50** |

---

## 4. Quy ước làm việc

- **Đầu sprint:** chốt task, chuyển trạng thái trong `project-progress.xlsx` sang "ĐANG LÀM".
- **Cuối sprint:** mỗi người demo phần mình cho người kia, merge vào `master`, cập nhật trạng thái "HOÀN THÀNH".
- **Nhánh:** A làm trên `dev/member-1`, B làm trên `dev/member-2`; merge vào `master` khi build và unit test đều qua.
- **Task trễ:** chuyển sang sprint kế tiếp và ghi lý do; không dời kiểm thử chéo ở S6.
