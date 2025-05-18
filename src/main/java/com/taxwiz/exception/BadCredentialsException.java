package com.taxwiz.exception;

import static com.taxwiz.utils.ErrorMessages.BAD_CREDENTIALS;

public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException() {super(BAD_CREDENTIALS.name());}
    public BadCredentialsException(String message) {
        super(message);
    }

    public BadCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
