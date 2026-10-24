# S65 - Import chứng chỉ - Không tìm thấy MSSV (Light)

> [!NOTE]
> **Chủ sở hữu:** Thành viên 2  
> **Nguồn:** `docs/Untitled (1).pdf`, trang 55  
> **Ảnh tham chiếu:** [S65](../reference/S65-certificate-import-missing-student-light.png)

---

## 1. Định danh & Điều hướng
- **ID:** `S65`
- **Tên màn:** Import chứng chỉ - Không tìm thấy MSSV (Light)
- **Route:** `import_export`
- **Chế độ hiển thị:** Light
- **Nguồn thiết kế:** `docs/Untitled (1).pdf`, trang 55
- **Ảnh tham chiếu:** `reference/S65-certificate-import-missing-student-light.png`
- **Phạm vi sở hữu:** Thành viên 2

---

## 2. Cây cấu trúc giao diện (Layout Tree)
- Card tệp giữ nguyên để người dùng biết nguồn lỗi.
  - Error card hiển thị dòng CSV và MSSV không có trong hệ thống.
  - Hướng dẫn sửa MSSV rồi chọn lại tệp.

---

## 3. Thành phần giao diện (Components & Material Catalog)
- `MaterialCardView` error state, `MaterialButton`
- Dùng Material Components và token của repo; không yêu cầu `Theme.Material3` hoặc `Widget.Material3`.

---

## 4. Hệ thống Token thiết kế (Design Tokens)
- @color/color_surface, @color/bg_card, @color/color_primary, @color/color_error, @color/text_primary, @color/text_secondary, @color/color_outline_variant; @dimen/screen_margin, @dimen/card_gap, @dimen/radius_card, @dimen/radius_input, @dimen/min_touch_target
- Theme Light lấy giá trị tương ứng từ `tokens/colors.xml` hoặc `tokens/colors-night.xml`; không hardcode màu trong layout.

---

## 5. Danh mục chuỗi văn bản (Copy & String Keys)
- @string/import_missing_student_title, @string/import_missing_student_desc_format, @string/btn_choose_other_file

---

## 6. Ma trận trạng thái (UI States)
| Trạng thái | Hiển thị | Tương tác |
|:---|:---|:---|
| **Reference error** | Theo đúng ảnh S65 | Tuân thủ RBAC, validation và quy tắc retry/disabled tương ứng |

---

## 7. Tiêu chuẩn nghiệm thu (Acceptance Criteria)
- [ ] Không tự tạo sinh viên khi MSSV không tồn tại; báo đúng dòng lỗi.
- [ ] Vùng chạm tương tác tối thiểu 48dp; nội dung hỗ trợ TalkBack và cỡ chữ lớn không bị cắt.
- [ ] Bố cục, copy và theme đối chiếu được với `reference/S65-certificate-import-missing-student-light.png`.
