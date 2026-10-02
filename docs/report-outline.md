# Đề Cương Báo Cáo

**Định dạng nộp:** PDF, theo mẫu báo cáo của Khoa/giảng viên. Nội dung gồm hai phần: (1) tất cả những gì nhóm đã học về Firebase Firestore, giúp người đọc hiểu cách nó hoạt động, (2) chi tiết cách nhóm xây dựng ứng dụng quản lý sinh viên.
**Mục tiêu độ dài (nhóm tự đặt):** 25–35 trang.

---

## Cấu trúc chương

| Chương | Nội dung | Số trang | Người viết | Mục thang điểm của đề |
|---|---|---|---|---|
| Bìa + Lời cảm ơn + Mục lục | Bìa theo mẫu, thông tin nhóm, danh sách hình/bảng | 3 | Cả hai | Cover, ToC, appendix, reference (0.75) |
| 1. Giới thiệu | Đặt vấn đề, mục tiêu, phạm vi, công nghệ | 2 | A | Content (1.0) |
| 2. Nghiên cứu Firebase Firestore | Toàn bộ kiến thức Firestore (xem bên dưới) | 7 | B | Content (1.0) |
| 3. Phân tích yêu cầu | Yêu cầu chức năng, phi chức năng, vai trò, RBAC | 3 | A | Correctness (0.75) |
| 4. Thiết kế hệ thống | MVVM, UML, schema Firestore, bảo mật, UI | 5 | A (B cung cấp UML, schema) | Pictures, tables, diagrams (0.75) |
| 5. Triển khai | A: xác thực, người dùng. B: sinh viên, chứng chỉ, import/export. Có đoạn code minh họa và screenshot | 6 | Cả hai | Correctness (0.75) |
| 6. Kiểm thử | Kế hoạch test, bảng test case, kết quả | 2 | A | Correctness (0.75) |
| 7. Đánh giá và Kết luận | Đạt/chưa đạt, khó khăn, hướng phát triển | 2 | A | Content (1.0) |
| Tài liệu tham khảo | ≥ 10 nguồn chính thức | 1 | A | Cover, ToC, appendix, reference (0.75) |
| Phụ lục | Hướng dẫn cài đặt, tài khoản Admin cho giám khảo, tài khoản demo | 1 | A | Cover, ToC, appendix, reference (0.75) |

Điểm "Presentation style and writing style" (0.75) áp dụng cho toàn bộ báo cáo. Người định dạng PDF: B.

**Lưu ý theo thang điểm của đề:**
- Bìa phải đúng tên trường, họ tên, mã số thành viên, giảng viên phụ trách. Xóa hết chú thích hướng dẫn của file mẫu. Lời cảm ơn tự viết, không chép từ Internet.
- Nội dung phải **tự viết lại theo hiểu biết của nhóm**, không chép nguyên văn từ blog.
- Mỗi hình, bảng, sơ đồ phải có chú thích. Hình tự chụp hoặc tự vẽ.

---

## Chương 2: Nghiên cứu Firebase Firestore (chi tiết)

### 2.1 Mô hình dữ liệu NoSQL
- So sánh SQL và NoSQL; Documents và Rows; Collections và Tables.
- Ưu điểm: linh hoạt schema, mở rộng tốt, phù hợp mobile.

### 2.2 Collections và Documents
- Cấu trúc cây: collection → document → subcollection.
- Document ID: tự sinh hoặc tự đặt (dự án dùng Mã SV làm ID sinh viên).
- Giới hạn: document tối đa 1 MB.

### 2.3 Kiểu dữ liệu
- string, number, boolean, timestamp, array, map, reference, null.

### 2.4 CRUD cơ bản
- `add()`, `set()`, `update()`, `delete()`, `get()`.

### 2.5 Truy vấn và giới hạn
- `where()`, `orderBy()`, `limit()`, `startAfter()` (phân trang).
- Giới hạn: không tìm theo chuỗi con. Truy vấn kết hợp `where` và `orderBy` trên các trường khác nhau cần composite index.
- Cách dự án xử lý: lọc/sắp xếp phía client (xem `firestore-schema.md` mục 5).

### 2.6 Indexes
- Single-field index: tạo tự động.
- Composite index: tạo thủ công trong Console hoặc `firestore.indexes.json`.
- Ví dụ: lọc theo khoa và sắp theo tên cần composite index.

### 2.7 Realtime Listeners
- `addSnapshotListener()` nhận dữ liệu tức thì khi có thay đổi.
- Phải gọi `ListenerRegistration.remove()` khi không dùng nữa.

### 2.8 Batch Writes và Transactions
- `WriteBatch`: ghi nhiều document cùng lúc (≤ 500 thao tác/batch), atomic.
- `Transaction`: đọc rồi ghi, đảm bảo nhất quán.

### 2.9 Offline Persistence
- SDK tự cache dữ liệu cục bộ, đồng bộ lại khi có mạng.

### 2.10 Security Rules
- Viết trong `firestore.rules`, kiểm tra `request.auth` và dữ liệu trong document `users`.
- Ví dụ rule kiểm tra vai trò admin.

### 2.11 Giá và hạn ngạch (gói Spark miễn phí)
Kiểm tra lại số liệu trên trang Pricing chính thức trước khi đưa vào báo cáo.

| Tài nguyên | Giới hạn |
|---|---|
| Document reads | 50.000 / ngày |
| Document writes | 20.000 / ngày |
| Document deletes | 20.000 / ngày |
| Network egress | 10 GB / tháng |

### 2.12 So sánh Firestore, Realtime Database và SQLite

| Tiêu chí | Firestore | Realtime Database | SQLite |
|---|---|---|---|
| Mô hình | Document/Collection | JSON Tree | Quan hệ (SQL) |
| Truy vấn | Mạnh, composite query | Hạn chế | SQL đầy đủ |
| Realtime | ✔ | ✔ | ✖ |
| Offline | ✔ | ✔ | ✔ |
| Mở rộng | Tự động | Cần tối ưu thủ công | Giới hạn trong máy |
| Phù hợp | App mobile nhiều người dùng | Realtime đơn giản | App cục bộ |

---

## Danh sách hình ảnh dự kiến

Các biểu đồ Mermaid trong `docs/` cần xuất thành ảnh (hoặc vẽ lại) khi đưa vào báo cáo.

| Mã | Tiêu đề | Chương | Nguồn |
|---|---|---|---|
| H1 | Kiến trúc MVVM | 4 | `design.md` mục 1 |
| H2 | Use case | 3 | `uml.md` Hình 1 |
| H3 | Lớp chính | 4 | `uml.md` Hình 2 |
| H4 | Sơ đồ dữ liệu Firestore (ER) | 4 | `uml.md` Hình 3 |
| H5 | Tuần tự đăng nhập | 4 | `uml.md` Hình 4 |
| H6 | Tuần tự thêm sinh viên và đồng bộ | 4 | `uml.md` Hình 5 |
| H7 | Tuần tự tìm kiếm, sắp xếp | 4 | `uml.md` Hình 6 |
| H8 | Tuần tự import CSV | 4 | `uml.md` Hình 7 |
| H9 | Tuần tự Admin thêm người dùng | 4 | `uml.md` Hình 8 |
| H10 | Sơ đồ điều hướng màn hình | 4 | `design.md` mục 3 |
| H11 | Screenshot đăng nhập | 5 | Chụp màn hình thật |
| H12 | Screenshot danh sách sinh viên | 5 | Chụp màn hình thật |
| H13 | Screenshot thêm sinh viên | 5 | Chụp màn hình thật |
| H14 | Screenshot import CSV | 5 | Chụp màn hình thật |
| H15 | Screenshot quản lý người dùng | 5 | Chụp màn hình thật |
| H16 | Firestore Console, cấu trúc dữ liệu | 2 | Chụp màn hình thật |
| H17 | Firestore Console, Security Rules | 2 | Chụp màn hình thật |

> **Tất cả hình phải do nhóm tự chụp hoặc tự vẽ. Không sao chép từ blog hay tài liệu khác.**

---

## Tài liệu tham khảo (mẫu)

1. Google. *Firebase Firestore Documentation*. https://firebase.google.com/docs/firestore
2. Google. *Firebase Authentication Documentation*. https://firebase.google.com/docs/auth
3. Google. *Firestore Security Rules*. https://firebase.google.com/docs/firestore/security/get-started
4. Android Developers. *Guide to App Architecture*. https://developer.android.com/topic/architecture
5. Android Developers. *Storage Access Framework*. https://developer.android.com/guide/topics/providers/document-provider
6. Android Developers. *RecyclerView*. https://developer.android.com/develop/ui/views/layout/recyclerview
7. Google. *Firestore Pricing*. https://firebase.google.com/docs/firestore/pricing
8. Google. *Firestore Indexes*. https://firebase.google.com/docs/firestore/query-data/indexing
9. Google. *Transactions and Batched Writes*. https://firebase.google.com/docs/firestore/manage-data/transactions
10. Google. *Firestore Offline Data*. https://firebase.google.com/docs/firestore/manage-data/enable-offline

> **Lưu ý:** đọc tài liệu gốc, **tự diễn đạt lại bằng lời của nhóm**, không sao chép nguyên văn. Ghi năm truy cập khi đưa vào báo cáo.
