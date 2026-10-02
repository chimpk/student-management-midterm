# Hướng Dẫn Đóng Góp

Áp dụng cho cả nhóm. Ví dụ commit viết bằng tiếng Anh, phần `subject` viết ở thể mệnh lệnh.

## Conventional Commits

Định dạng: `<type>(<scope>): <subject>` (mẫu trong `.gitmessage`)

**Types:** `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`

**Scopes:** `auth`, `user`, `student`, `certificate`, `import-export`, `ui`, `firestore`, `rules`, `docs`, `uml`, `build`, `gradle`

**Subject:** thể mệnh lệnh, tối đa 72 ký tự. Body/footer tùy chọn (ví dụ `Closes #12`).

### Ví dụ đúng

1. `feat(auth): add login screen`
2. `fix(student): resolve crash on sort by name`
3. `docs(uml): add class diagram`
4. `refactor(ui): update color palette`
5. `perf(student): debounce search input`
6. `test(user): add cases for employee roles`
7. `build(gradle): update firebase bom version`
8. `chore(docs): format project plan`
9. `style(auth): align text in login button`
10. `feat(certificate): add certificate form`

### Ví dụ sai

1. `added login screen`
2. `fix crash`
3. `updating diagrams`
4. `refactored some code`
5. `perf improvement`
6. `added tests`
7. `build fix`
8. `formatting`
9. `styled stuff`
10. `feat: certificates` (thiếu scope)

## Chiến lược nhánh

- `master`: nhánh ổn định, sẵn sàng demo. Chỉ merge qua Pull Request.
- `develop`: nhánh tích hợp (tạo khi bắt đầu viết code).
- `feature/<scope>-<mô-tả-ngắn>`: tính năng mới, ví dụ `feature/student-crud`.
- `fix/<scope>-<mô-tả-ngắn>`: sửa lỗi.
- `docs/<mô-tả-ngắn>`: thay đổi tài liệu.

Tag phiên bản: `v0.1.0`, ...
Pull Request cần ít nhất 1 người review, squash merge. Điền đủ checklist trong template PR.

## Lưu ý bảo mật

Không commit `google-services.json` hay thông tin nhạy cảm (file đã nằm trong `.gitignore`).

Phân công nhánh theo thành viên: [`docs/team-work-distribution.md`](docs/team-work-distribution.md).
