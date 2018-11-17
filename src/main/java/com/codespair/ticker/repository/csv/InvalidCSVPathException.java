package com.codespair.ticker.repository.csv;

/**
 * Runtime Exception that should be thrown when an invalid csv file path is specified.
 */
public class InvalidCSVPathException extends RuntimeException {

    public InvalidCSVPathException(String message) {
        super(message);
    }
}
