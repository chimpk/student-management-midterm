# Dữ liệu mẫu

Các file CSV mẫu phục vụ phát triển và kiểm thử nhập/xuất. Định dạng cột xem tại [`../docs/firestore-schema.md`](../docs/firestore-schema.md) mục 4. Tất cả dùng mã hóa UTF-8.

| File | Nội dung | Dùng cho test case |
|---|---|---|
| `students.csv` | 20 sinh viên hợp lệ; MSSV theo `KYYTSSSS`; mã lớp gồm đúng 8 chữ hoa/số | TC-16, TC-24 |
| `students-invalid.csv` | 9 dòng lỗi độc lập để kiểm tra báo lỗi theo dòng | TC-17 |
| `certificates.csv` | 12 chứng chỉ, tham chiếu MSSV trong `students.csv` | TC-27 |

Nhập `students.csv` **trước** `certificates.csv` vì chứng chỉ được gắn theo MSSV.

## Các lỗi trong `students-invalid.csv`

| Dòng dữ liệu | Giá trị nhận diện | Lỗi mong đợi |
|---:|---|---|
| 1 | `Z2400001` | Mã khoa ngoài miền `0`–`9`, `A`–`H` |
| 2 | `52A00002` | Hai ký tự năm tuyển sinh không phải chữ số |
| 3 | `524K0003` | Mã hệ đào tạo không phải `0` hoặc `H` |
| 4 | `5240004` | MSSV không đủ 8 ký tự |
| 5 | `24h50401` | Mã lớp chứa chữ thường |
| 6 | `2405041` | Mã lớp không đủ 8 ký tự |
| 7 | Trường `name` rỗng | Thiếu họ tên bắt buộc |
| 8 | `2004/02/14` | Ngày sinh sai định dạng `YYYY-MM-DD` |
| 9 | Trường `faculty` rỗng | Thiếu khoa bắt buộc |

Dữ liệu mẫu chỉ minh họa định dạng. Ứng dụng kiểm tra MSSV nhập vào, không tự sinh MSSV hoặc số thứ tự theo tên. Các mã ngành CNTT `02`, `03`, `04` và nhóm `H5` là mẫu quan sát, chưa được trình bày như quy tắc chính thức của TDTU.
