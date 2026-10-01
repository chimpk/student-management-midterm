# API & Database Specification

## 1. Overview
The application utilizes **Firebase Firestore** as a serverless NoSQL database and **Firebase Auth** for authentication.

## 2. Database Collections Schema

### Collection: `users`
Document ID: `{uid}` (Matches Firebase Auth UID)
```json
{
  "uid": "STRING",
  "email": "STRING",
  "fullName": "STRING",
  "role": "Admin | Manager | Employee",
  "avatarUrl": "STRING",
  "status": "Active | Locked",
  "createdAt": "TIMESTAMP"
}
```

#### Sub-collection: `users/{uid}/loginHistory`
Document ID: Auto-generated
```json
{
  "timestamp": "TIMESTAMP",
  "ipAddress": "STRING",
  "deviceInfo": "STRING"
}
```

### Collection: `students`
Document ID: Auto-generated
```json
{
  "id": "STRING",
  "fullName": "STRING",
  "age": "NUMBER",
  "phone": "STRING",
  "address": "STRING",
  "status": "Normal | Locked",
  "createdAt": "TIMESTAMP",
  "updatedAt": "TIMESTAMP"
}
```

#### Sub-collection: `students/{studentId}/certificates`
Document ID: Auto-generated
```json
{
  "id": "STRING",
  "title": "STRING",
  "issueDate": "STRING",
  "expiryDate": "STRING",
  "imageUrl": "STRING"
}
```

## 3. Firestore Indexes
- Collection `students`: `fullName` ASC, `age` DESC.
- Collection `students`: `status` ASC, `createdAt` DESC.
