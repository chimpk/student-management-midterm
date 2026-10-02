# Cấu Trúc Thư Mục Dự Án

Hướng dẫn cài đặt và chạy nằm ở [`test-and-setup.md`](test-and-setup.md). Ý nghĩa các package nằm ở [`design.md`](design.md) mục 2.

**Trạng thái:** mới có khung dự án (`MainActivity`, layout/theme mặc định, các thư mục package chứa `.gitkeep`). Các file Java còn lại là **dự kiến**.

```
student-management-midterm/
├── app/
│   ├── build.gradle.kts                 # Dependencies, minSdk 26, ViewBinding
│   ├── proguard-rules.pro
│   ├── google-services.json.example     # Mẫu cấu hình Firebase (file thật bị .gitignore)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/studentmgmt/
│       │   ├── MainActivity.java        # Có sẵn. Chứa Navigation Component
│       │   ├── adapter/                 # UserAdapter, StudentAdapter, CertificateAdapter, LoginRecordAdapter
│       │   ├── data/
│       │   │   ├── firebase/            # FirebaseClient
│       │   │   └── repository/          # Auth/User/Student/CertificateRepository
│       │   ├── model/                   # User, Student, Certificate, LoginRecord
│       │   ├── service/                 # AdminSeedService
│       │   ├── ui/
│       │   │   ├── auth/                # LoginActivity
│       │   │   ├── common/              # HomeFragment
│       │   │   ├── user/                # UserList/UserEdit/LoginHistory Fragment
│       │   │   ├── student/             # StudentList/Detail/Edit, ImportExport Fragment
│       │   │   ├── certificate/         # CertificateEditFragment
│       │   │   └── profile/             # ProfileFragment
│       │   ├── utils/                   # CsvHelper, ValidationUtils, DateUtils, PermissionHelper, Constants
│       │   └── viewmodel/               # Auth/User/Student/CertificateViewModel
│       └── res/                         # drawable, layout, menu, mipmap-anydpi-v26, values, values-night, xml
├── docs/                                # Tài liệu (xem bảng dưới)
├── sample-data/                         # File CSV mẫu để thử import
├── gradle/
│   ├── wrapper/gradle-wrapper.properties
│   └── libs.versions.toml               # Gradle Version Catalog
├── .github/                             # Template issue và pull request
├── build.gradle.kts, settings.gradle.kts, gradle.properties
├── firestore.rules                      # Security Rules
├── firestore.indexes.json               # Composite index (hiện rỗng, xem firestore-schema.md mục 5)
├── project-progress.xlsx                # Theo dõi tiến độ và cân bằng khối lượng
├── README.md, CONTRIBUTING.md, CHANGELOG.md
├── .editorconfig, .gitignore, .gitmessage
```

## Các file trong `docs/`

| File | Nội dung |
|---|---|
| `Mid-term.pdf` | Đề bài gốc |
| `SRS.md` | Yêu cầu chức năng, phi chức năng, quy tắc dữ liệu, đối chiếu với đề |
| `rbac-matrix.md` | Ma trận phân quyền |
| `firestore-schema.md` | Cấu trúc dữ liệu Firestore (nguồn duy nhất) |
| `design.md` | Kiến trúc MVVM, package, màn hình, điều hướng |
| `uml.md` | Use case, lớp, ER, tuần tự |
| `ai-rules.md` | Quy chuẩn lập trình |
| `test-and-setup.md` | Cài đặt, chạy, test case |
| `project-plan.md` | WBS, Gantt, rủi ro |
| `team-work-distribution.md` | Phân công theo tuần |
| `report-outline.md` | Đề cương báo cáo |
| `demo-script.md` | Kịch bản video demo, checklist nộp bài |
| `folder-structure.md` | Tài liệu này |

Quy ước commit và nhánh nằm ở [`../CONTRIBUTING.md`](../CONTRIBUTING.md).
