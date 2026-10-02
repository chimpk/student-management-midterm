# Phân Công Công Việc Nhóm

> **Dự án:** Ứng dụng Quản lý Thông tin Sinh viên Thời gian Thực
> **Môn học:** Phát triển ứng dụng di động – 503074
> **Thời gian:** 6 tuần (04/10/2026 → 14/11/2026)

Chi tiết từng công việc, giờ ước tính và trạng thái: [`../project-progress.xlsx`](../project-progress.xlsx). Kế hoạch tổng thể: [`project-plan.md`](project-plan.md).

---

## Kế hoạch 6 tuần

| Tuần | Thời gian | THÀNH VIÊN A | THÀNH VIÊN B |
|---|---|---|---|
| 1 | 04–10/10 | Cài đặt Firebase, Đăng nhập, Điều hướng vai trò, Giới thiệu báo cáo | Thiết kế schema, Danh sách sinh viên |
| 2 | 11–17/10 | CRUD người dùng, Khóa/mở khóa, Đổi avatar | CRUD sinh viên, Trang chi tiết, Biểu đồ UML |
| 3 | 18–24/10 | Lịch sử đăng nhập, Rules bảo mật, Viết báo cáo (Xác thực/Người dùng) | Tìm kiếm, Sắp xếp, CRUD chứng chỉ |
| 4 | 25–31/10 | Hoàn thiện UI, Xử lý lỗi, Báo cáo (Bảo mật, Thiết kế) | Import/Export CSV (sinh viên + chứng chỉ) |
| 5 | 01–07/11 | Kiểm thử Auth/Người dùng/vai trò, Trường hợp biên | Viết chương Firestore, Kiểm thử Sinh viên/Chứng chỉ/Import-Export |
| 6 | 08–14/11 | Kịch bản demo, Quay video, Báo cáo (Kiểm thử, Kết luận, Tài liệu tham khảo) | Định dạng PDF, Checklist nộp bài, Vá lỗi cuối |

---

## Phân công chi tiết

### THÀNH VIÊN A – Phụ trách

- Xác thực (đăng nhập, kiểm tra tài khoản `Locked`, điều hướng theo vai trò) và seed tài khoản Admin
- Đổi ảnh đại diện (upload avatar)
- Lịch sử đăng nhập (ghi lại + hiển thị)
- CRUD người dùng (chỉ Admin), trạng thái Normal/Locked
- Firestore security rules
- Hoàn thiện UI + xử lý lỗi
- Báo cáo: Giới thiệu, Xác thực, Quản lý người dùng, Bảo mật, Thiết kế hệ thống, Kiểm thử, Kết luận, Tài liệu tham khảo
- Quay và trình bày video demo

### THÀNH VIÊN B – Phụ trách

- Thiết kế schema Firestore
- Danh sách, CRUD, tìm kiếm, sắp xếp, trang chi tiết sinh viên (tìm kiếm/sắp xếp xử lý phía client)
- CRUD chứng chỉ (subcollection)
- Import/Export CSV (sinh viên + chứng chỉ)
- Báo cáo: Chương Firestore, Sinh viên, Import/Export, Triển khai
- Định dạng PDF + checklist nộp bài

### Chung

- Xem lại kiến trúc và UML
- Kiểm thử cuối cùng
- Tập luyện demo

---

## Quy tắc nhánh và commit

Quy ước đầy đủ ở [`../CONTRIBUTING.md`](../CONTRIBUTING.md). Nhánh theo dạng `feature/<scope>-<mô-tả-ngắn>`:

```
feature/auth-login              ← THÀNH VIÊN A
feature/user-management        ← THÀNH VIÊN A
feature/student-crud            ← THÀNH VIÊN B
feature/certificate-crud        ← THÀNH VIÊN B
feature/import-export-csv       ← THÀNH VIÊN B
docs/report                     ← cả hai
```

Ví dụ commit: `feat(auth): add login screen`, `fix(auth): block locked accounts`, `docs(docs): write Firestore chapter`

---

## Quy tắc trung thực

Chỉ đánh dấu **HOÀN THÀNH** khi tính năng đã **lập trình và kiểm thử xong**.
Chi tiết tiêu chuẩn kiểm tra & nghiệm thu: xem [`definition-of-done.md`](definition-of-done.md).
Cập nhật `project-progress.xlsx` khi hoàn thành từng công việc.
