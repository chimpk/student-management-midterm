# UI/UX & Architecture Design

## 1. Architecture Pattern
The project strictly follows the **MVVM (Model-View-ViewModel)** architectural pattern with a Repository pattern for Firebase data abstraction.

```
+------------------------------------+
|            View (UI)               |
| Activity / Fragment / ViewBinding  |
+------------------------------------+
                 | (Observes LiveData/State)
                 v
+------------------------------------+
|             ViewModel              |
| LiveData / UI Logic / State        |
+------------------------------------+
                 |
                 v
+------------------------------------+
|            Repository              |
| AuthRepository / StudentRepository |
+------------------------------------+
                 |
                 v
+------------------------------------+
|       Data Source (Firebase)       |
| Firestore / FirebaseAuth / Storage |
+------------------------------------+
```

## 2. Design Guidelines
- **Color Palette**:
  - Primary Green: `#00A859`
  - Primary Dark: `#008647`
  - Accent Green: `#4CAF50`
- **UI Framework**: Material Components / Material 3 (`Theme.MaterialComponents.DayNight.DarkActionBar`).
- **View Binding**: Android ViewBinding enabled for type-safe view interaction.
