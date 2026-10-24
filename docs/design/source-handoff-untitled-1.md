# Đối chiếu nguồn `Untitled (1).pdf`

## 1. Phạm vi nhập vào catalog

- Trang 1–81 là frame giao diện; trang 82 là ghi chú bàn giao.
- 36 frame trùng nội dung với catalog S01–S36 nên được giữ nguyên ID, không tạo bản sao.
- 45 frame khác biệt được thêm thành S37–S81, gồm màn theo vai trò, CRUD người dùng/chứng chỉ, import/export chứng chỉ và các trạng thái loading/empty/error/offline/xác thực.
- Dòng “24 màn mới” trên trang 82 không khớp với số frame khác biệt thực tế khi áp dụng quy ước hiện tại “mỗi trạng thái/theme là một S-ID”. Catalog lấy số frame thực tế làm chuẩn.

## 2. Mapping các frame đã có

| Trang PDF mới | ID hiện có | Trang PDF mới | ID hiện có | Trang PDF mới | ID hiện có |
|:---:|:---:|:---:|:---:|:---:|:---:|
| 1 | S01 | 2 | S02 | 3 | S03 |
| 4 | S04 | 5 | S05 | 6 | S06 |
| 7 | S07 | 8 | S08 | 9 | S09 |
| 10 | S10 | 11 | S11 | 15 | S12 |
| 16 | S13 | 17 | S14 | 18 | S17 |
| 19 | S18 | 23 | S15 | 24 | S16 |
| 25 | S19 | 26 | S20 | 33 | S21 |
| 34 | S22 | 41 | S23 | 42 | S24 |
| 49 | S25 | 50 | S26 | 57 | S27 |
| 58 | S28 | 65 | S29 | 66 | S30 |
| 73 | S31 | 74 | S32 | 78 | S33 |
| 79 | S34 | 80 | S35 | 81 | S36 |

## 3. Quyết định chuẩn hóa

1. **Hệ giao diện:** Cụm “Clean Academic Material 3” được hiểu là định hướng thị giác của file thiết kế. Đặc tả triển khai vẫn dùng Material Components và token hiện có; không yêu cầu `Theme.Material3`/`Widget.Material3`.
2. **Tài khoản:** Không có tự đăng ký. Admin tạo tài khoản; email là tùy chọn và chấp nhận mọi tên miền hợp lệ.
3. **RBAC:** Admin quản trị toàn bộ; Manager CRUD sinh viên/chứng chỉ và dùng import/export; Employee chỉ đọc sinh viên/chứng chỉ và quản lý hồ sơ cá nhân.
4. **Dữ liệu mở rộng:** `unit`, vị trí đăng nhập và kết quả chứng chỉ chỉ là nội dung trình bày/demo nếu schema không hỗ trợ; không tự mở rộng backend chỉ vì xuất hiện trong mockup.
5. **Trạng thái:** Form validate khi blur, giữ dữ liệu hợp lệ, khóa Lưu khi còn lỗi; danh sách phải có loading/empty/error/offline phù hợp; terminal auth state không cho Back vào nội dung được bảo vệ.

## 4. Token và tương tác từ trang bàn giao

- Khung thiết kế 412 × 892dp, lưới cơ sở 8dp, touch target tối thiểu 48dp.
- Roboto; body tối thiểu 14sp, caption 12sp; hỗ trợ font scale 1.3× và TalkBack.
- Chuyển cảnh card → chi tiết 250–300ms và phải tôn trọng Reduce Motion.
- Footer Lưu ghim đáy nhưng nội dung cuộn phải có inset, không bị footer hoặc bàn phím che.
- Search/filter ghim ở đầu vùng danh sách; retry giữ đúng ngữ cảnh và dữ liệu đã nhập/chọn.
