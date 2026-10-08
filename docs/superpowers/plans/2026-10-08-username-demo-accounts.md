# Username Demo Accounts Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Chuẩn hóa ba tài khoản demo dùng tên đăng nhập/mật khẩu theo vai trò, với email là thông tin tùy chọn và có thể dùng làm bí danh đăng nhập khi tồn tại.

**Architecture:** `username` là định danh đăng nhập bắt buộc, duy nhất; `email` là thuộc tính tùy chọn. Tài liệu mô tả đây là thiết kế dự kiến vì ứng dụng chưa có luồng xác thực hoàn chỉnh; không tuyên bố đã cấp tài khoản Firebase.

**Tech Stack:** Android Java, JUnit 4, Cloud Firestore Rules, Markdown.

**Spec:** `docs/SRS.md`

**Execution status:** Completed on 2026-10-08; verification results are recorded in the final handoff.

## Global Constraints

- Ba thông tin demo: `admin/admin`, `manager/manager`, `employee/employee`.
- Email không bắt buộc; chỉ dùng làm bí danh đăng nhập khi tài khoản có email.
- Không tuyên bố tài khoản đã được tạo trên Firebase khi chưa có Project ID và bằng chứng.
- Nêu rõ Firebase Email/Password yêu cầu mật khẩu tối thiểu 6 ký tự nếu nhóm chọn tích hợp cơ chế đó.

## Review Focus

- Username rỗng, có khoảng trắng hoặc ký tự không hợp lệ phải bị từ chối.
- Username demo phải được chấp nhận và so khớp phân biệt chữ hoa/thường theo quy ước.
- Email vắng mặt không làm document người dùng sai schema.
- Email có mặt phải là chuỗi có độ dài hợp lý.
- Tài liệu không được mô tả ba tài khoản là đã provision trên Firebase.

---

### Task 1: Quy tắc tên đăng nhập

**Files:**
- Modify: `app/src/test/java/com/example/studentmgmt/utils/ValidationUtilsTest.java`
- Modify: `app/src/main/java/com/example/studentmgmt/utils/ValidationUtils.java`

**Interfaces:**
- Produces: `ValidationUtils.isValidUsername(String): boolean`

- [ ] Viết test RED cho `admin`, `manager`, `employee` và các username sai.
- [ ] Chạy test, xác nhận lỗi vì chưa có API.
- [ ] Cài đặt regex username tối thiểu 3, tối đa 32 ký tự.
- [ ] Chạy test và xác nhận GREEN.

### Task 2: Schema và Security Rules

**Files:**
- Modify: `firestore.rules`
- Modify: `docs/firestore-schema.md`
- Modify: `docs/design.md`
- Modify: `docs/uml.md`

**Interfaces:**
- Consumes: `username` bắt buộc; `email` tùy chọn.

- [ ] Bắt buộc `username` trong user document và kiểm tra định dạng.
- [ ] Chuyển `email` thành trường tùy chọn có kiểm tra kiểu/độ dài.
- [ ] Đồng bộ schema, UML và thiết kế đăng nhập bằng username hoặc email alias.

### Task 3: Tài khoản demo và tài liệu tiếng Việt

**Files:**
- Modify: `README.md`
- Modify: `docs/SRS.md`
- Modify: `docs/final-report.md`
- Modify: `docs/firebase-setup.md`
- Modify: `docs/test-and-setup.md`
- Modify: `docs/team-info.md`
- Modify: `docs/demo-script.md`

**Interfaces:**
- Produces: bảng demo thống nhất cho cả ba vai trò.

- [ ] Thay mọi bảng demo bằng ba cặp username/password theo yêu cầu.
- [ ] Mô tả email là tùy chọn và là bí danh đăng nhập khi có.
- [ ] Giữ trạng thái tài khoản là “dự kiến/chờ cấp”, không ghi sai là đã tạo.
- [ ] Ghi chú giới hạn mật khẩu của Firebase Email/Password.

### Task 4: Xác minh

**Files:**
- Test: `app/src/test/java/com/example/studentmgmt/utils/ValidationUtilsTest.java`

- [ ] Chạy `gradlew.bat testDebugUnitTest assembleDebug --rerun-tasks`.
- [ ] Rà thông tin đăng nhập cũ, yêu cầu email bắt buộc và liên kết hỏng.
- [ ] Chạy `git diff --check`.
