package org.example.bookingservicelogic.exception;

/**
 * Exception thrown when a request contains invalid data.
 *
 */
public class BadRequestException extends RuntimeException {

    private final String field;

    public BadRequestException(String message) {
        super(message);
        this.field = null;
    }

    public BadRequestException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
