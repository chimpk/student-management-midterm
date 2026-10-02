# Ma Trận Phân Quyền (RBAC)

Giá trị lưu trong Firestore: `role` = `admin` | `manager` | `employee`. Xem [`firestore-schema.md`](firestore-schema.md).

| Chức năng | Admin | Manager | Employee |
|---|:---:|:---:|:---:|
| **TÀI KHOẢN** | | | |
| Đăng nhập | ✔ | ✔ | ✔ |
| Đổi ảnh đại diện (của chính mình) | ✔ | ✔ | ✔ |
| Xem lịch sử đăng nhập của mình | ✔ | ✔ | ✔ |
| **QUẢN LÝ NGƯỜI DÙNG** | | | |
| Xem danh sách người dùng | ✔ | ✖ | ✖ |
| Thêm người dùng (tạo tài khoản) | ✔ | ✖ | ✖ |
| Sửa người dùng | ✔ | ✖ | ✖ |
| Xóa người dùng | ✔ | ✖ | ✖ |
| Khóa/mở khóa tài khoản | ✔ | ✖ | ✖ |
| Xem lịch sử đăng nhập của người khác | ✔ | ✖ | ✖ |
| **QUẢN LÝ SINH VIÊN** | | | |
| Xem danh sách sinh viên | ✔ | ✔ | ✔ |
| Tìm kiếm / Sắp xếp sinh viên | ✔ | ✔ | ✔ |
| Xem chi tiết sinh viên | ✔ | ✔ | ✔ |
| Thêm sinh viên | ✔ | ✔ | ✖ |
| Sửa sinh viên | ✔ | ✔ | ✖ |
| Xóa sinh viên | ✔ | ✔ | ✖ |
| **QUẢN LÝ CHỨNG CHỈ** | | | |
| Xem chứng chỉ | ✔ | ✔ | ✔ |
| Thêm / Sửa / Xóa chứng chỉ | ✔ | ✔ | ✖ |
| **IMPORT / EXPORT** | | | |
| Import sinh viên & chứng chỉ từ CSV | ✔ | ✔ | ✖ |
| Export sinh viên & chứng chỉ ra CSV | ✔ | ✔ | ✖ |

> **Ghi chú:**
> - Tài khoản `Locked` → không được đăng nhập dù mật khẩu đúng.
> - Employee **chỉ** được đổi ảnh đại diện của **chính mình**, không chỉnh sửa gì khác.
> - Chỉ Admin mới tạo được tài khoản mới (Manager và Employee không thể).
> - Admin là tài khoản tích hợp sẵn (FR-ACC-04), không cần tạo.
> - Đề bài nói Employee "chỉ xem nội dung". Nhóm hiểu "nội dung" là sinh viên và chứng chỉ, nên danh sách người dùng chỉ Admin xem (giả định, xem SRS mục 6).
