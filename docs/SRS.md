# Software Requirements Specification (SRS)

## 1. Introduction & Purpose
This document defines the requirements for the Realtime Student Information Management System (StudentMgmt), a lightweight Android application backed by Firebase Firestore.

## 2. User Roles & RBAC Matrix
The system supports 3 user roles:
- **Admin**: System administration, user account management, full access.
- **Manager**: Managing student records, certificates, import/export.
- **Employee**: Read-only access to student information, ability to update own profile/avatar.

### Permission Matrix
| Functionality | Admin | Manager | Employee |
|---|:---:|:---:|:---:|
| User Login & Auth | ✅ | ✅ | ✅ |
| Update Own Avatar | ✅ | ✅ | ✅ |
| CRUD Users & Login Logs | ✅ | ❌ | ❌ |
| CRUD Students | ✅ | ✅ | ❌ |
| Search & Filter Students | ✅ | ✅ | ✅ |
| CRUD Certificates | ✅ | ✅ | ❌ |
| Import / Export CSV | ✅ | ✅ | ❌ |

## 3. Functional Requirements
- **FR-ACC-01**: User authentication via email and password.
- **FR-ACC-02**: Profile avatar upload and update.
- **FR-USR-01**: Admin user management (Create, Read, Update, Lock user accounts).
- **FR-STU-01**: Student CRUD operations (Name, Age, Phone, Address, Status).
- **FR-STU-02**: Realtime search and sorting of student list.
- **FR-CER-01**: Student certificate management.
- **FR-IO-01**: Bulk import/export student data via CSV format.

## 4. Validation Rules
- **Student Age**: Must be between 18 and 35.
- **Phone Number**: Valid 10-15 digit string.
- **Required Fields**: Full Name, Age, Phone, Status (`Normal` | `Locked`).

## 5. Non-Functional Requirements
- **NFR-01**: Realtime synchronization with Firestore (< 2 seconds latency).
- **NFR-02**: Offline capability with local Firestore cache persistence.
- **NFR-03**: Secure data access enforced via Firebase Firestore Security Rules.
