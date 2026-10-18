package com.example.studentmgmt.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Immutable model representing a single login history event.
 * Subcollection: users/{uid}/loginHistory/{logId}
 */
public final class LoginRecord implements Serializable {

    private final String id;
    private final long timestamp;
    private final String device;
    private final boolean pending;

    public LoginRecord(String id, long timestamp, String device) {
        this(id, timestamp, device, timestamp <= 0);
    }

    public LoginRecord(String id, long timestamp, String device, boolean pending) {
        this.id = id != null ? id : "";
        this.timestamp = timestamp;
        this.device = device != null ? device : "Unknown Device";
        this.pending = pending;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDevice() {
        return device;
    }

    public boolean isPending() {
        return pending || timestamp <= 0;
    }

    public String getFormattedDateTime() {
        if (isPending()) {
            return "Đang đồng bộ (chờ máy chủ)...";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginRecord that = (LoginRecord) o;
        return timestamp == that.timestamp &&
                Objects.equals(id, that.id) &&
                Objects.equals(device, that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, device);
    }

    @Override
    public String toString() {
        return "LoginRecord{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", device='" + device + '\'' +
                '}';
    }
}
