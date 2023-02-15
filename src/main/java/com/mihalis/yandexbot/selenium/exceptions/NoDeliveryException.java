package com.mihalis.yandexbot.selenium.exceptions;

public class NoDeliveryException extends Exception {
    public NoDeliveryException() {
        super();
    }

    public NoDeliveryException(String message) {
        super(message);
    }

    public NoDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoDeliveryException(Throwable cause) {
        super(cause);
    }

    protected NoDeliveryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
