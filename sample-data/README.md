# Dữ Liệu Mẫu

File CSV mẫu để thử import/export. Định dạng cột: xem [`../docs/firestore-schema.md`](../docs/firestore-schema.md) mục 4. Mã hóa UTF-8.

| File | Nội dung | Dùng cho test case |
|---|---|---|
| `students.csv` | 20 sinh viên hợp lệ, tên có dấu tiếng Việt | TC-16, TC-24 |
| `students-invalid.csv` | 5 dòng, trong đó dòng 3 thiếu Mã SV | TC-17 |
| `certificates.csv` | 12 chứng chỉ, tham chiếu Mã SV trong `students.csv` | TC-27 |

Import `students.csv` **trước** `certificates.csv` vì chứng chỉ được gắn theo Mã SV.
