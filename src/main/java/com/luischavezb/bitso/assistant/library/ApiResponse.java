package com.luischavezb.bitso.assistant.library;

/**
 * @param <T>
 * @author luischavez
 */
public class ApiResponse<T> {

    private final T object;

    private final String errorCode;
    private final String errorMessage;

    private final Exception exception;

    public ApiResponse(T object) {
        this.object = object;

        errorCode = null;
        errorMessage = null;

        exception = null;
    }

    public ApiResponse(String errorCode, String errorMessage) {
        object = null;

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

        exception = null;
    }

    public ApiResponse(Exception exception) {
        object = null;

        errorCode = null;
        errorMessage = null;

        this.exception = exception;
    }

    public T object() {
        return object;
    }

    public String errorCode() {
        return errorCode;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public Exception exception() {
        return exception;
    }

    public boolean success() {
        return null != object;
    }
}
