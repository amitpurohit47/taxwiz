package com.taxwiz.exception;

import static com.taxwiz.utils.ErrorMessages.NOT_VERIFIED;

public class UnverifiedException extends RuntimeException{
    public UnverifiedException() {
        super(NOT_VERIFIED.name());
    }
}
