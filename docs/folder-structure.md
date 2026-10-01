# Folder Structure

```
MidTerm/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/example/studentmgmt/
│           │   ├── MainActivity.java
│           │   ├── adapter/         # RecyclerView Adapters
│           │   ├── data/            # Data layer (Firebase & Repositories)
│           │   ├── model/           # Data models / Entities
│           │   ├── service/         # Background Services / Helpers
│           │   ├── ui/              # UI layer (Activities / Fragments)
│           │   │   ├── auth/        # Login & Authentication screens
│           │   │   ├── student/     # Student management screens
│           │   │   ├── user/        # User administration screens
│           │   │   └── certificate/ # Certificate management screens
│           │   ├── utils/           # Utility & Constant classes
│           │   └── viewmodel/       # ViewModels
│           └── res/                 # Layouts, Values, Menus, Drawables
├── docs/                        # Project Documentation
│   ├── srs.md
│   ├── project-plan.md
│   ├── api.md
│   ├── design.md
│   ├── ai-rules.md
│   ├── folder-structure.md
│   ├── uml.md
│   └── git-commit-convention.md
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```
