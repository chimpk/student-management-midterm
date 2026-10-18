# S26 - Sinh viên - Thanh cảnh báo ngoại tuyến (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** PDF trang 26  
> **Ảnh tham chiếu:** [S26](reference/S26-student-list-offline-banner-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S26`
- **Tên màn:** Sinh viên - Thanh cảnh báo ngoại tuyến (Light)
- **Route:** `student_list`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** PDF trang 26
- **Ảnh tham chiếu:** `reference/S26-student-list-offline-banner-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Giao diện danh sách sinh viên S07
- Ngay dưới AppBar, chèn thanh thông báo `OfflineBannerLayout`:
  - `MaterialCardView` (nền `@color/bg_offline`, viền `@color/border_offline`, bo góc `@dimen/input_corner_radius`, padding `~12dp`, marginHorizontal `@dimen/screen_margin`)
    - `LinearLayout` (horizontal, căn giữa dọc)
      - `ImageView` (`~20dp`, icon `ic_wifi_off`, tint `@color/text_offline`)
      - `LinearLayout` (vertical, trọng số 1, marginStart `~10dp`)
        - `TextView` (13sp/500 `@color/text_offline`, copy `@string/student_offline_banner`)
        - `TextView` (12sp `@color/text_offline`, format `@string/student_cached_time_format`)
- Dải đếm số lượng hiển thị nhãn ngoại tuyến: `@string/student_count_sort_offline_format`

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView`: Thanh banner cảnh báo mất mạng
- Icons: `ic_wifi_off` (Material Symbols `wifi_off`)

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- Màu sắc: `@color/bg_offline`, `@color/border_offline`, `@color/text_offline`, `@color/color_background`
- Kích thước: `@dimen/screen_margin`, `@dimen/input_corner_radius`

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- `@string/student_offline_banner`
- `@string/student_cached_time_format`
- `@string/student_count_sort_offline_format`

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Banner ngoại tuyến | Dữ liệu sinh viên |
|:---|:---|:---|
| **Offline** | Xuất hiện thanh màu vàng cam `@color/bg_offline` | Đọc dữ liệu đã lưu trong bộ đệm thiết bị |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Màu chữ `@color/text_offline` trên nền `@color/bg_offline` đạt tỷ lệ tương phản tối thiểu 4.5:1.
- [ ] Tự động ẩn thanh banner khi thiết bị kết nối mạng trở lại.
