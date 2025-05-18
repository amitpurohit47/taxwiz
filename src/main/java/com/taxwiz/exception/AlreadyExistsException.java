package com.taxwiz.exception;

import static com.taxwiz.utils.ErrorMessages.ALREADY_EXISTS;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException() {super(ALREADY_EXISTS.name());}

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
