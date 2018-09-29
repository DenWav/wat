package com.demonwav.wat.exception;

public class WatException extends Exception {
    public WatException(String message) {
        super(message);
    }

    public WatException(String message, Throwable cause) {
        super(message, cause);
    }
}
