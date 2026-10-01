# AI Rules & Guidelines

## 1. Code Style & Architecture Rules
- Use **Java 17** for source files in `com.example.studentmgmt`.
- Maintain **MVVM pattern**: Activities/Fragments must not contain database logic. Use `ViewModel` and `Repository`.
- Use **View Binding** instead of `findViewById`.
- Use **Kotlin DSL (`build.gradle.kts`)** and Gradle Version Catalog (`gradle/libs.versions.toml`) for dependency management.

## 2. Safety & Error Handling
- Always validate input before submitting to Firebase.
- Provide user feedback using `Toast` or `Snackbar` on network operations.
- Enforce strict role-based checks on UI actions matching SRS specifications.
