# Biểu Đồ UML

Tên lớp, màn hình và trường dữ liệu trong các biểu đồ khớp với [`design.md`](design.md) và [`firestore-schema.md`](firestore-schema.md).

## 1. Biểu đồ Use Case

```mermaid
graph TD
    Admin(("Admin"))
    Manager(("Manager"))
    Employee(("Employee"))

    subgraph System["Ứng dụng Quản lý Thông tin Sinh viên"]
        UC1["UC-01 Đăng nhập"]
        UC1a["Kiểm tra trạng thái Locked"]
        UC1b["Ghi lịch sử đăng nhập"]
        UC2["UC-02 Đổi ảnh đại diện"]
        UC3["UC-03 Quản lý người dùng"]
        UC3a["Xem lịch sử đăng nhập của người dùng"]
        UC4["UC-04 Xem, tìm kiếm, sắp xếp sinh viên"]
        UC5["UC-05 Thêm, sửa, xóa sinh viên"]
        UC6["UC-06 Quản lý chứng chỉ"]
        UC7["UC-07 Import / Export CSV"]
    end

    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC4
    Admin --> UC5
    Admin --> UC6
    Admin --> UC7

    Manager --> UC1
    Manager --> UC2
    Manager --> UC4
    Manager --> UC5
    Manager --> UC6
    Manager --> UC7

    Employee --> UC1
    Employee --> UC2
    Employee --> UC4

    UC1 -.->|include| UC1a
    UC1 -.->|include| UC1b
    UC3 -.->|include| UC3a
```
*Hình 1: Biểu đồ use case. Employee chỉ xem và đổi ảnh đại diện.*

## 2. Biểu đồ lớp

```mermaid
classDiagram
    class User {
        +String uid
        +String email
        +String name
        +int age
        +String phone
        +String status
        +String role
        +String avatarUrl
        +Timestamp createdAt
    }
    class LoginRecord {
        +Timestamp timestamp
        +String device
    }
    class Student {
        +String studentId
        +String name
        +String dateOfBirth
        +String className
        +String faculty
        +Timestamp createdAt
    }
    class Certificate {
        +String certName
        +String issueDate
        +String issuedBy
    }

    class AuthRepository {
        +login(email, password)
        +logout()
    }
    class UserRepository {
        +observeUsers()
        +addUser(user, password)
        +updateUser(user)
        +deleteUser(uid)
        +getLoginHistory(uid)
        +uploadAvatar(uid, uri)
    }
    class StudentRepository {
        +observeStudents()
        +addStudent(student)
        +updateStudent(student)
        +deleteStudent(studentId)
        +importStudents(list)
    }
    class CertificateRepository {
        +observeCertificates(studentId)
        +addCertificate(studentId, cert)
        +updateCertificate(studentId, cert)
        +deleteCertificate(studentId, certId)
        +importCertificates(list)
    }
    class CsvHelper {
        +parseStudents(uri)
        +parseCertificates(uri)
        +writeStudents(list, uri)
        +writeCertificates(list, uri)
    }

    class AuthViewModel
    class UserViewModel
    class StudentViewModel
    class CertificateViewModel

    User "1" --> "*" LoginRecord : có
    Student "1" --> "*" Certificate : có

    AuthViewModel --> AuthRepository
    UserViewModel --> UserRepository
    StudentViewModel --> StudentRepository
    StudentViewModel --> CsvHelper
    CertificateViewModel --> CertificateRepository
    CertificateViewModel --> CsvHelper

    AuthRepository ..> User
    UserRepository ..> User
    UserRepository ..> LoginRecord
    StudentRepository ..> Student
    CertificateRepository ..> Certificate
```
*Hình 2: Biểu đồ lớp chính (model, repository, ViewModel, tiện ích CSV).*

## 3. Mô hình dữ liệu Firestore (ER)

```mermaid
erDiagram
    USERS {
        string uid PK
        string email
        string name
        int age
        string phone
        string status
        string role
        string avatarUrl
        timestamp createdAt
    }
    LOGIN_HISTORY {
        string logId PK
        timestamp timestamp
        string device
    }
    STUDENTS {
        string studentId PK
        string name
        string dateOfBirth
        string className
        string faculty
        timestamp createdAt
    }
    CERTIFICATES {
        string certId PK
        string certName
        string issueDate
        string issuedBy
    }

    USERS ||--o{ LOGIN_HISTORY : "có"
    STUDENTS ||--o{ CERTIFICATES : "có"
```
*Hình 3: Sơ đồ dữ liệu Firestore (collection và subcollection).*

## 4. Biểu đồ tuần tự

### 4.1 Đăng nhập và điều hướng theo vai trò

```mermaid
sequenceDiagram
    autonumber
    actor U as Người dùng
    participant A as LoginActivity
    participant VM as AuthViewModel
    participant R as AuthRepository
    participant FB as Firebase Auth
    participant FS as Firestore

    U->>A: Nhập email, mật khẩu, bấm Đăng nhập
    A->>VM: login(email, password)
    VM->>R: login(email, password)
    R->>FB: signInWithEmailAndPassword()
    alt Sai thông tin
        FB-->>R: Lỗi xác thực
        R-->>A: Hiện lỗi "Sai thông tin đăng nhập"
    else Xác thực thành công
        FB-->>R: uid
        R->>FS: get users/uid
        alt Không có document hoặc status = Locked
            R->>FB: signOut()
            R-->>A: Hiện lỗi "Tài khoản bị khóa"
        else status = Normal
            R->>FS: add users/uid/loginHistory
            R-->>VM: User (role)
            VM-->>A: Chuyển sang MainActivity
        end
    end
```
*Hình 4: Luồng đăng nhập, kiểm tra khóa và ghi lịch sử.*

### 4.2 Thêm sinh viên và đồng bộ thời gian thực

```mermaid
sequenceDiagram
    autonumber
    actor M as Manager (thiết bị 1)
    participant F as StudentEditFragment
    participant VM as StudentViewModel
    participant R as StudentRepository
    participant FS as Firestore
    participant L as StudentListFragment (thiết bị 2)

    M->>F: Điền form, bấm Lưu
    F->>F: Kiểm tra dữ liệu (ValidationUtils)
    F->>VM: addStudent(student)
    VM->>R: addStudent(student)
    R->>FS: set students/studentId
    FS-->>R: Thành công
    R-->>F: Thông báo thành công, quay lại danh sách
    FS-->>L: Snapshot ADDED (dưới 2 giây)
    L->>L: Cập nhật danh sách
```
*Hình 5: Thêm sinh viên và cập nhật tức thì trên thiết bị khác.*

### 4.3 Tìm kiếm và sắp xếp

```mermaid
sequenceDiagram
    autonumber
    actor U as Người dùng
    participant L as StudentListFragment
    participant VM as StudentViewModel
    participant R as StudentRepository
    participant FS as Firestore

    L->>VM: observe students
    VM->>R: observeStudents()
    R->>FS: addSnapshotListener(students)
    FS-->>R: Danh sách sinh viên
    R-->>VM: Cập nhật LiveData (danh sách gốc)
    VM-->>L: Hiện danh sách
    U->>L: Nhập từ khóa, chọn tiêu chí sắp xếp
    L->>VM: searchAndSort(query, sortCriteria)
    VM->>VM: Lọc và sắp xếp danh sách gốc ở phía client
    VM-->>L: Danh sách đã lọc
```
*Hình 6: Tìm kiếm và sắp xếp phía client trên dữ liệu thời gian thực.*

### 4.4 Import sinh viên từ CSV

```mermaid
sequenceDiagram
    autonumber
    actor M as Manager hoặc Admin
    participant F as ImportExportFragment
    participant VM as StudentViewModel
    participant H as CsvHelper
    participant R as StudentRepository
    participant FS as Firestore

    M->>F: Chọn file CSV qua SAF
    F->>VM: importStudents(uri)
    VM->>H: parseStudents(uri)
    H-->>VM: Danh sách hợp lệ và danh sách dòng lỗi
    VM-->>F: Hiện bản xem trước và lỗi
    M->>F: Xác nhận import
    VM->>R: importStudents(danh sách hợp lệ)
    loop Mỗi nhóm tối đa 500 dòng
        R->>FS: WriteBatch.commit()
    end
    R-->>F: Báo cáo X dòng thành công, Y dòng lỗi
```
*Hình 7: Luồng import sinh viên từ CSV.*

### 4.5 Admin thêm người dùng

```mermaid
sequenceDiagram
    autonumber
    actor A as Admin
    participant F as UserEditFragment
    participant VM as UserViewModel
    participant R as UserRepository
    participant FB as Firebase Auth (app phụ)
    participant FS as Firestore

    A->>F: Điền tên, tuổi, SĐT, trạng thái, email, mật khẩu, vai trò
    F->>F: Kiểm tra dữ liệu (ValidationUtils)
    F->>VM: addUser(user, password)
    VM->>R: addUser(user, password)
    R->>FB: createUserWithEmailAndPassword()
    FB-->>R: uid
    R->>FS: set users/uid
    FS-->>R: Thành công
    R-->>F: Thông báo thành công
```
*Hình 8: Admin tạo tài khoản mới. Dùng FirebaseApp phụ để Admin không bị đăng xuất.*
