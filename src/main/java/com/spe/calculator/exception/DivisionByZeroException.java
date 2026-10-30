package com.spe.calculator.exception;

/**
 * Custom exception thrown when division by zero is attempted.
 * RuntimeException means we don't have to declare it in method signatures.
 */
public class DivisionByZeroException extends RuntimeException {

    public DivisionByZeroException(String message) {
        super(message);
    }
}
