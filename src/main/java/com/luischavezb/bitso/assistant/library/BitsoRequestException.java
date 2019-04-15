package com.luischavezb.bitso.assistant.library;

/**
 *
 * @author luischavez
 */
public class BitsoRequestException extends RuntimeException {

    public BitsoRequestException() {
    }

    public BitsoRequestException(String message) {
        super(message);
    }

    public BitsoRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BitsoRequestException(Throwable cause) {
        super(cause);
    }
}
