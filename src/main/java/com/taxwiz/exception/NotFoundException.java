package com.taxwiz.exception;

import static com.taxwiz.utils.ErrorMessages.NOT_FOUND;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {super(NOT_FOUND.name());}

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
