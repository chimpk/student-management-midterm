# Đặc Tả Nhận Diện Thương Hiệu & Quy Chuẩn Đổi Brand (Branding Specification)

Tài liệu này quy định việc quản lý và thay thế nhận diện thương hiệu trong bộ tài liệu thiết kế.

> [!IMPORTANT]
> **Tên dự án và logo trong tài liệu PDF chỉ là PLACEHOLDER tạm thời.**
> Tên "UniData", biểu tượng mũ tốt nghiệp, tagline "Dữ liệu rõ ràng. Quản lý dễ dàng." và các nhận diện thương hiệu liên quan chưa phải là chính thức. Khi có nhận diện thương hiệu chính thức từ trường/USER, việc thay đổi được thực hiện theo checklist bên dưới mà **KHÔNG làm thay đổi `applicationId` hoặc package `com.example.studentmgmt`**.

---

## 1. Giá Trị Placeholder Hiện Tại

| Thành phần thương hiệu | Giá trị Placeholder hiện tại | Ghi chú |
|---|---|---|
| **Tên ứng dụng (`app_name`)** | `UniData` | Gán nhãn `<!-- BRAND-PLACEHOLDER -->` trong `strings-vi.xml` |
| **Khẩu hiệu (`app_tagline`)** | `Dữ liệu rõ ràng. Quản lý dễ dàng.` | Tagline hiển thị dưới tên app ở màn hình Login |
| **Phụ đề (`app_subtitle`)** | `Quản lý dữ liệu sinh viên` | Tiêu đề mô tả chức năng tại Login |
| **Biểu tượng (Logo)** | Mũ tốt nghiệp (Graduation Cap) | Tạm thời dùng `branding/logo-placeholder.svg` |
| **Màu chủ đạo (`color_primary`)** | `#3F51B5` (Light) / `#B6BFFF` (Dark) | Tông xanh tím học thuật (Indigo Academic) |
| **Tên trường** | `Trường Đại học Tôn Đức Thắng (TDTU)` | Khai báo trong `fixtures.json` |
| **Domain email** | `@student.tdtu.edu.vn` | Khai báo trong `fixtures.json` |

---

## 2. Quy Chuẩn Vùng Đặt Logo & Tên Thương Hiệu (Brand Slots)

Nhằm đảm bảo layout giao diện luôn giữ nguyên và độc lập hoàn toàn với nội dung thương hiệu, các vị trí thương hiệu được quy ước thành các **Slot**:

### Slot 1: Vùng Logo & Header Màn Hình Đăng Nhập (Login Screen)
- **Vị trí:** Căn giữa màn hình (`center_horizontal`), cách đỉnh màn hình `~48dp`.
- **Kích thước khung Logo:** `~72dp × 72dp`.
- **Hình dạng:** Hình vuông bo góc `radius ~18dp`, nền `@color/color_primary`, biểu tượng icon bên trong màu trắng `~40dp × 40dp`.
- **Khối chữ:** Đặt ngay dưới khung logo cách `~16dp`, gồm Tên ứng dụng (`@dimen/text_size_title`, 22sp/500) và Tagline (`@dimen/text_size_subtitle`, 14sp).

### Slot 2: Vùng Thương Hiệu Header Màn Hình Trang Chủ (Home / Dashboard)
- **Vị trí:** Góc trên bên trái AppBar, cách lề trái `@dimen/screen_margin` (16dp).
- **Kích thước icon:** `~32dp × 32dp`, bo góc `~8dp`, nền `@color/bg_primary`.
- **Khối chữ:** Nằm cạnh icon logo, hiển thị `@string/app_name` với phông `@dimen/text_size_title` (22sp/500).

### Slot 3: Biểu Tượng Ứng Dụng (Launcher Icon)
- **Kích thước chuẩn Android:** Vector adaptive icon (`108dp × 108dp` với vùng an toàn `72dp × 72dp`).
- **Nền:** `@color/color_primary`.
- **Biểu tượng tiền cảnh (Foreground):** Vector biểu tượng thương hiệu chính thức màu trắng.

---

## 3. Bảng Vị Trí Cần Thay Thế Khi Chốt Thương Hiệu Chính Thức

| Hạng mục / Tệp tin | Khóa (Key) / Vị trí | Giá trị cần cập nhật |
|---|---|---|
| `docs/design/strings-vi.xml` | `app_name` | Thay thế chuỗi tên ứng dụng chính thức |
| `docs/design/strings-vi.xml` | `app_tagline` | Thay thế khẩu hiệu chính thức |
| `docs/design/strings-vi.xml` | `app_subtitle` | Cập nhật phụ đề ứng dụng |
| `docs/design/fixtures.json` | `_brandVariables.emailDomain` | Thay thế domain email nội bộ trường |
| `docs/design/fixtures.json` | `_brandVariables.universityName`| Thay thế tên trường đại học chính thức |
| `docs/design/tokens/colors.xml` | `color_primary`, `bg_primary` | Cập nhật mã màu nhận diện thương hiệu nếu thay đổi |
| `docs/design/branding/` | `logo-placeholder.svg` | Thay thế bằng file SVG vector logo chính thức |
| `res/drawable/` (khi code) | `ic_app_logo.xml` | Cập nhật vector drawable logo chính thức |
| `res/mipmap-*/` (khi code) | `ic_launcher` & `ic_launcher_round` | Sinh bộ icon launcher đa kích thước |
| `docs/design/reference/` | Các ảnh mockup liên quan | Cập nhật ảnh chụp màn hình chứa logo mới |

---

## 4. Checklist Thực Hiện Đổi Thương Hiệu (Brand Migration Checklist)

Khi có quyết định tên và logo chính thức, thực hiện theo thứ tự sau:

- [ ] **Bước 1:** Mở `docs/design/strings-vi.xml`, cập nhật các chuỗi `app_name`, `app_tagline`, `app_subtitle`.
- [ ] **Bước 2:** Cập nhật biến `emailDomain` và `universityName` trong `docs/design/fixtures.json`.
- [ ] **Bước 3:** Thay thế tệp `docs/design/branding/logo-placeholder.svg` bằng logo chính thức.
- [ ] **Bước 4:** Nếu thương hiệu mới có bộ màu khác, cập nhật `tokens/colors.xml` và `tokens/colors-night.xml`.
- [ ] **Bước 5:** Trong mã nguồn Android, chỉ cần import lại vector drawable vào `ic_app_logo` và cập nhật mipmap launcher icon.
- [ ] **LƯU Ý CỐT LÕI:** **Tuyệt đối KHÔNG thay đổi `applicationId`** (`com.example.studentmgmt`) trong `app/build.gradle.kts` và `AndroidManifest.xml` vì việc này sẽ làm gãy cấu hình `google-services.json` và phân quyền Firebase Auth / Firestore.
