package com.example.studentmgmt.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable user model representing an authenticated application user.
 * Schema conforms to docs/firestore-schema.md.
 */
public final class AppUser implements Serializable {

    public static final String STATUS_NORMAL = "Normal";
    public static final String STATUS_LOCKED = "Locked";

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_MANAGER = "manager";
    public static final String ROLE_EMPLOYEE = "employee";

    private final String uid;
    private final String username;
    private final String email;
    private final String name;
    private final int age;
    private final String phone;
    private final String status;
    private final String role;
    private final String avatarUrl;

    public AppUser(String uid,
                   String username,
                   String email,
                   String name,
                   int age,
                   String phone,
                   String status,
                   String role,
                   String avatarUrl) {
        this.uid = Objects.requireNonNull(uid, "uid must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.email = email;
        this.name = name != null ? name : "";
        this.age = age;
        this.phone = phone != null ? phone : "";
        this.status = status;
        this.role = role;
        this.avatarUrl = avatarUrl;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public boolean isLocked() {
        return STATUS_LOCKED.equalsIgnoreCase(status);
    }

    public boolean isNormal() {
        return STATUS_NORMAL.equals(status);
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(role);
    }

    public boolean isManager() {
        return ROLE_MANAGER.equalsIgnoreCase(role);
    }

    public boolean isEmployee() {
        return ROLE_EMPLOYEE.equalsIgnoreCase(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return age == appUser.age &&
                Objects.equals(uid, appUser.uid) &&
                Objects.equals(username, appUser.username) &&
                Objects.equals(email, appUser.email) &&
                Objects.equals(name, appUser.name) &&
                Objects.equals(phone, appUser.phone) &&
                Objects.equals(status, appUser.status) &&
                Objects.equals(role, appUser.role) &&
                Objects.equals(avatarUrl, appUser.avatarUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, username, email, name, age, phone, status, role, avatarUrl);
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
