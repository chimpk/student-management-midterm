# Kịch Bản Demo Video và Checklist Nộp Bài

**Yêu cầu của đề:** video có âm thanh, tối đa **20 phút**, chuẩn **720p**. Video do **một thành viên đại diện nhóm** trình bày (chọn người nói to, rõ). Không bắt buộc dùng slide. Nội dung cần nêu rõ vấn đề, kết quả nghiên cứu của nhóm về Firestore và thông tin hữu ích cho người xem. Nếu nội dung dài, có thể tua nhanh các đoạn không quan trọng.

**Người trình bày:** THÀNH VIÊN A (xem `team-work-distribution.md`).

---

## Kịch bản theo phút

| Phút | Nội dung |
|---|---|
| 0:00–1:00 | **Giới thiệu:** tên nhóm, môn học, bài toán |
| 1:00–6:30 | **Nghiên cứu Firestore:** mở Firebase Console, giải thích collection/document/subcollection, kiểu dữ liệu, CRUD, truy vấn và giới hạn, index, listener thời gian thực, batch, offline, giá/hạn ngạch |
| 6:30–8:00 | **Kiến trúc ứng dụng:** MVVM, cấu trúc dữ liệu Firestore của dự án |
| 8:00–9:30 | **Đăng nhập:** Admin, Manager, Employee, và tài khoản `Locked` bị từ chối |
| 9:30–11:30 | **Admin, quản lý người dùng:** thêm Manager, sửa, khóa/mở khóa, xem lịch sử đăng nhập |
| 11:30–14:00 | **Manager, sinh viên và chứng chỉ:** thêm, sửa, xem chi tiết, thêm chứng chỉ; tìm tên có dấu tiếng Việt; sắp xếp đa tiêu chí |
| 14:00–15:30 | **Import/Export CSV:** import 20 sinh viên, import chứng chỉ, export ra CSV |
| 15:30–17:00 | **Thời gian thực (2 thiết bị):** thêm sinh viên trên thiết bị 1, thiết bị 2 thấy ngay |
| 17:00–18:00 | **Employee bị giới hạn:** thử thêm sinh viên, bị từ chối |
| 18:00–19:00 | **Security Rules:** mở Firebase Console, giải thích rules |
| 19:00–20:00 | **Kết luận:** tóm tắt tính năng, điều học được về Firestore, hướng phát triển |

---

## Checklist trước khi quay

- [ ] Thiết bị đã đăng nhập đúng tài khoản demo
- [ ] Dữ liệu mẫu đã nạp sẵn (ít nhất 10 sinh viên)
- [ ] File CSV import sẵn sàng (`sample-data/students.csv`, 20 dòng, tên tiếng Việt)
- [ ] 2 thiết bị/máy ảo sẵn sàng cho demo thời gian thực
- [ ] Không có thông báo pop-up gây gián đoạn
- [ ] Âm thanh rõ, không tạp âm
- [ ] Độ phân giải ≥ 720p
- [ ] Thời lượng ≤ 20 phút

---

## Checklist nộp bài

- [ ] File PDF báo cáo, đủ bìa (theo mẫu của Khoa), mục lục, lời cảm ơn, tài liệu tham khảo, phụ lục
- [ ] Video demo (≤ 20 phút, 720p, có âm thanh, tên file rõ ràng)
- [ ] Toàn bộ source code, thư viện và database (gói nộp có `google-services.json` của project Firebase demo, không đưa lên repo công khai)
- [ ] Hướng dẫn cài đặt và chạy (`README.md`, `docs/test-and-setup.md`)
- [ ] **Tài khoản Admin cho giám khảo** (ghi trong phụ lục báo cáo hoặc file riêng)
- [ ] Thông tin thành viên nhóm đầy đủ, đúng
- [ ] Nộp đúng hạn, đúng kênh quy định, **không nộp qua email cá nhân**
- [ ] Công việc chia đều giữa các thành viên (xem sheet "Phân Bổ Khối Lượng")
