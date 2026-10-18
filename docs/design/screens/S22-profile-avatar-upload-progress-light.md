# S22 - Hồ sơ cá nhân - Đang tải ảnh đại diện (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 1  
> **Nguồn:** PDF trang 22  
> **Ảnh tham chiếu:** [S22](reference/S22-profile-avatar-upload-progress-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S22`
- **Tên màn:** Hồ sơ cá nhân - Đang tải ảnh đại diện (Light)
- **Route:** `profile`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 22
- **Ảnh tham chiếu:** `reference/S22-profile-avatar-upload-progress-light.png`
- **Phạm vi sở hữu:** Thành viên 1

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giao diện hồ sơ cá nhân S17
- Vùng Avatar:
  - Khung ảnh đại diện hiển thị lớp phủ mờ
  - `LinearProgressIndicator` (progress=65, màu `@color/color_primary`, track `@color/color_outline_variant`)
  - `TextView` (12sp/500 `@color/color_primary`, format `@string/uploading_avatar_format`)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `LinearProgressIndicator`: Thanh tiến độ tải tệp
- `TextView`: Nhãn phần trăm tiến độ

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_primary`, `@color/color_outline_variant`, `@color/color_surface`
- Kích thước: `@dimen/card_corner_radius`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/uploading_avatar_format`
- `@string/toast_avatar_success`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Tiến độ |
|:---|:---|
| **Uploading** | Hiển thị tiến trình 65%, khóa nút chọn ảnh để tránh upload đè |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Thanh tiến trình cập nhật mượt mà từ 0% đến 100%.
- [ ] Hoàn tất hiển thị Toast `@string/toast_avatar_success` và làm mới ảnh đại diện.
