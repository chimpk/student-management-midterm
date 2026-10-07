# Cấu Trúc Thư Mục & Tổ Chức Mã Nguồn Dự Án

> **Trạng thái:** **Đã thiết lập khung dự án, các package kiến trúc và file cấu hình chuẩn**. Các module đang được phân công triển khai cho các thành viên theo [`team-work-distribution.md`](team-work-distribution.md).

---

## 1. Cây thư mục tổng thể dự án

```text
student-management-midterm/
├── .github/                             # Cấu hình GitHub Issue và Pull Request templates
├── app/
│   ├── build.gradle.kts                 # Cấu hình build module app, compileSdk 34, minSdk 26, ViewBinding
│   ├── proguard-rules.pro               # Quy tắc tối ưu và bảo vệ mã nguồn
│   ├── google-services.json.example     # File mẫu cấu hình Firebase (file thật google-services.json bị .gitignore)
│   └── src/
│       ├── test/java/com/example/studentmgmt/
│       │   └── ValidationAndCsvTest.java # Bộ kiểm thử đơn vị tự động (Validation, CSV, Roles, Tiếng Việt)
│       └── main/
│           ├── AndroidManifest.xml      # Khai báo quyền, Activity, Application metadata
│           ├── java/com/example/studentmgmt/
│           │   ├── MainActivity.java    # Màn hình chính, điều hướng Navigation Drawer/BottomNav theo Role
│           │   ├── adapter/             # Adapter hiển thị RecyclerView (User, Student, Certificate, LoginRecord)
│           │   ├── data/
│           │   │   ├── firebase/        # FirebaseClient singleton kết nối Auth, Firestore, Storage
│           │   │   └── repository/      # Repository trung gian (Auth, Student, User, Certificate)
│           │   ├── model/               # Thực thể POJO (User, Student, Certificate, LoginRecord)
│           │   ├── service/             # Dịch vụ ngầm (AdminSeedService tự động tạo tài khoản Admin)
│           │   ├── ui/                  # Giao diện người dùng theo module (auth, common, student, certificate, user, profile)
│           │   ├── utils/               # Tiện ích chung (CsvHelper, ValidationUtils, DateUtils, HapticFeedbackHelper)
│           │   └── viewmodel/           # ViewModel quản lý state (Auth, Student, User, Certificate)
│           └── res/                     # Tài nguyên giao diện (drawable, layout, menu, values, values-night, xml)
├── docs/                                # Tài liệu kỹ thuật dự án (xem mục 2)
├── sample-data/                         # Dữ liệu thử nghiệm CSV mẫu
│   ├── students.csv                     # 20 sinh viên hợp lệ có dấu tiếng Việt
│   ├── students-invalid.csv             # File mẫu có dòng dữ liệu lỗi phục vụ test validation
│   └── certificates.csv                 # Danh sách chứng chỉ mẫu liên kết MSSV
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar           # Binary thực thi Gradle Wrapper
│   │   └── gradle-wrapper.properties    # Cấu hình Gradle distribution URL (8.4)
│   └── libs.versions.toml               # Gradle Version Catalog (Dependencies & Plugins)
├── build.gradle.kts                     # Root build file
├── settings.gradle.kts                  # Cấu hình repositories và module settings
├── gradle.properties                    # Cấu hình JVM arguments và AndroidX flags
├── gradlew.bat                          # Script chạy Gradle trên Windows
├── firebase.json                        # Cấu hình triển khai Firebase CLI (Firestore + Storage)
├── firestore.rules                      # Cloud Firestore Security Rules (RBAC bảo vệ đa tầng)
├── firestore.indexes.json               # Cấu hình Composite Indexes của Firestore
├── storage.rules                        # Firebase Cloud Storage Security Rules (bảo vệ avatar)
├── local.properties.example             # Mẫu cấu hình đường dẫn Android SDK máy cá nhân
├── project-progress.xlsx                # Bảng Excel phân bổ công việc, tiến độ và giờ thực tế
├── README.md                            # Giới thiệu tổng quan, hướng dẫn nhanh và thông tin nhóm
├── CONTRIBUTING.md                      # Quy ước đóng góp, nhánh git và định dạng commit
├── CHANGELOG.md                         # Nhật ký các mốc phát triển tính năng
├── .editorconfig                        # Quy chuẩn định dạng mã nguồn nhất quán
├── .gitignore                           # Bỏ qua file nhạy cảm và build artifacts
└── .gitmessage                          # Mẫu thông điệp Git commit chuẩn
```

---

## 2. Danh mục tài liệu chi tiết trong thư mục `docs/`

| Tên file | Mô tả nội dung chi tiết |
|---|---|
| [`Mid-term.pdf`](Mid-term.pdf) | Đề bài gốc chính thức của môn học (Đặc tả yêu cầu và barem chấm điểm). |
| [`srs.md`](srs.md) | Đặc tả yêu cầu phần mềm chi tiết (Chức năng FR, Phi chức năng NFR, Đối chiếu đề bài). |
| [`rbac-matrix.md`](rbac-matrix.md) | Ma trận phân quyền Role-Based Access Control (Admin, Manager, Employee). |
| [`firestore-schema.md`](firestore-schema.md) | Thiết kế cấu trúc dữ liệu Cloud Firestore (Collections, Subcollections, Documents). |
| [`design.md`](design.md) | Tài liệu thiết kế kiến trúc MVVM, các gói package, màn hình và luồng điều hướng. |
| [`uml.md`](uml.md) | Hệ thống biểu đồ UML hoàn chỉnh: Use Case, Class Diagram, ERD, 5 Sequence Diagrams. |
| [`firebase-setup.md`](firebase-setup.md) | Cẩm nang thiết lập và vận hành Firebase chi tiết từ A đến Z. |
| [`test-and-setup.md`](test-and-setup.md) | Hướng dẫn cài đặt, cấu hình JAVA_HOME, lệnh build CLI và **kế hoạch phân công 27 test cases**. |
| [`team-work-distribution.md`](team-work-distribution.md) | Bảng phân công chi tiết Owner - Reviewer chéo và ma trận trách nhiệm công bằng (50% - 50%). |
| [`team-info.md`](team-info.md) | Thông tin thành viên, MSSV, giảng viên và môi trường bàn giao dự án. |
| [`handover-checklist.md`](handover-checklist.md) | Bảng kiểm tra điều kiện bàn giao và nghiệm thu sản phẩm cuối kỳ. |
| [`project-plan.md`](project-plan.md) | Kế hoạch dự án 6 tuần, phân rã công việc WBS, quản lý rủi ro. |
| [`definition-of-done.md`](definition-of-done.md) | Tiêu chuẩn hoàn thành tính năng (DoD) và tiêu chí nghiệm thu chất lượng. |
| [`demo-script.md`](demo-script.md) | Kịch bản video demo chi tiết 20 phút và checklist chuẩn bị trước khi quay. |
| [`report-outline.md`](report-outline.md) | Đề cương chi tiết và phân bổ trang cho báo cáo học thuật. |
| [`ai-rules.md`](ai-rules.md) | Quy chuẩn coding conventions và nguyên tắc phát triển phần mềm sạch. |
| [`folder-structure.md`](folder-structure.md) | Tài liệu cấu trúc thư mục hiện hành (file này). |
