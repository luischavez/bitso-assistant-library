package com.luischavezb.bitso.assistant.library;

public class AssistantException extends RuntimeException {

    public AssistantException() {
    }

    public AssistantException(String message) {
        super(message);
    }

    public AssistantException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssistantException(Throwable cause) {
        super(cause);
    }
}
