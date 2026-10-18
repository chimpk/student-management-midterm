package com.example.studentmgmt.model;

import java.util.Objects;

/**
 * Immutable representation of screen state across 4 canonical UI phases:
 * LOADING, CONTENT, EMPTY, and ERROR.
 */
public final class UiState<T> {

    public enum Status {
        LOADING,
        CONTENT,
        EMPTY,
        ERROR
    }

    private final Status status;
    private final T data;
    private final String errorMessage;

    private UiState(Status status, T data, String errorMessage) {
        this.status = status;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> UiState<T> loading() {
        return new UiState<>(Status.LOADING, null, null);
    }

    public static <T> UiState<T> content(T data) {
        return new UiState<>(Status.CONTENT, data, null);
    }

    public static <T> UiState<T> empty() {
        return new UiState<>(Status.EMPTY, null, null);
    }

    public static <T> UiState<T> error(String message) {
        return new UiState<>(Status.ERROR, null, message != null ? message : "Đã xảy ra lỗi");
    }

    public Status getStatus() {
        return status;
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isContent() {
        return status == Status.CONTENT;
    }

    public boolean isEmpty() {
        return status == Status.EMPTY;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UiState<?> uiState = (UiState<?>) o;
        return status == uiState.status &&
                Objects.equals(data, uiState.data) &&
                Objects.equals(errorMessage, uiState.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, data, errorMessage);
    }

    @Override
    public String toString() {
        return "UiState{" +
                "status=" + status +
                ", data=" + data +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
