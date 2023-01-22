package com.mihalis.yandexbot.selenium.exceptions;

public class AttemptException extends RuntimeException {
    public AttemptException() {
        super();
    }

    public AttemptException(String message) {
        super(message);
    }

    public AttemptException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttemptException(Throwable cause) {
        super(cause);
    }

    protected AttemptException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
