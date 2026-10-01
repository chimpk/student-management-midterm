# Project Plan

## 1. Project Overview
A 4-sprint development plan for the Student Management Android Application.

## 2. Project Phases & Sprints

### Sprint 1: Setup & Core Foundation
- Project setup (Android Studio, Java, Kotlin DSL).
- Firebase project initialization (Firestore, Auth, Storage).
- Basic UI shell & Navigation structure.

### Sprint 2: Authentication & User Management
- Login screen & Firebase Auth integration.
- Admin user management module.
- User profile & avatar management.

### Sprint 3: Student & Certificate Operations
- Student CRUD module with realtime Firestore listeners.
- Certificate sub-collection CRUD.
- Search, filter, and sorting features.

### Sprint 4: Import/Export, Testing & Final Polish
- CSV Import/Export functionality.
- Unit testing & UI testing.
- Documentation & final release.

## 3. Project Timeline (Gantt Chart)
```mermaid
gantt
    title StudentMgmt Development Timeline
    dateFormat  YYYY-MM-DD
    section Sprint 1
    Setup & Firebase Auth      :a1, 2026-10-01, 7d
    section Sprint 2
    User Management & Profile  :a2, after a1, 7d
    section Sprint 3
    Student & Certificates     :a3, after a2, 10d
    section Sprint 4
    Import/Export & Polish     :a4, after a3, 6d
```
