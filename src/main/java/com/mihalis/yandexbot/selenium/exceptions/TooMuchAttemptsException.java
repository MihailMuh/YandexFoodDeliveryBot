package com.mihalis.yandexbot.selenium.exceptions;

public class TooMuchAttemptsException extends RuntimeException {
    public TooMuchAttemptsException() {
        super();
    }

    public TooMuchAttemptsException(String message) {
        super(message);
    }

    public TooMuchAttemptsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooMuchAttemptsException(Throwable cause) {
        super(cause);
    }

    protected TooMuchAttemptsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
