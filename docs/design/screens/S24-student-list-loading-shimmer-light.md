# S24 - Sinh viên - Khung xương tải dữ liệu Shimmer (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 24  
> **Ảnh tham chiếu:** [S24](reference/S24-student-list-loading-shimmer-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S24`
- **Tên màn:** Sinh viên - Khung xương tải dữ liệu Shimmer (Light)
- **Route:** `student_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 24
- **Ảnh tham chiếu:** `reference/S24-student-list-loading-shimmer-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giao diện danh sách sinh viên S07
- `ShimmerFrameLayout` bao bọc 3 khối khung xương item:
  - Khung xương 1: `MaterialCardView` (nền xám nhạt `~`, bo góc `@dimen/card_corner_radius`, chiều cao `~72dp`)
  - Khung xương 2: `MaterialCardView` (nền xám nhạt `~`, bo góc `@dimen/card_corner_radius`, chiều cao `~72dp`)
  - Khung xương 3: `MaterialCardView` (nền xám nhạt `~`, bo góc `@dimen/card_corner_radius`, chiều cao `~72dp`)
- `TextView` (13sp `@color/text_secondary`, copy `@string/student_loading_text`, căn giữa)

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `ShimmerFrameLayout`: Khung hiệu ứng lướt sáng khung xương
- `MaterialCardView`: Khung xương mô phỏng thẻ sinh viên

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/color_background`, `@color/color_surface_variant`, `@color/text_secondary`
- Kích thước: `@dimen/screen_margin`, `@dimen/card_corner_radius`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/student_loading_text`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị |
|:---|:---|
| **Loading Shimmer** | 3 khung xương nhấp nháy chuyển động, tạm khóa các nút bấm |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Khung xương duy trì đúng kích thước chiều cao và lề của thẻ sinh viên thật.
- [ ] Chuyển mượt mà sang danh sách dữ liệu thật khi tải xong.
