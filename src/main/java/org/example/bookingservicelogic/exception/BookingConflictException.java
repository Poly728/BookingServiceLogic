package org.example.bookingservicelogic.exception;

import lombok.Getter;

import java.time.LocalDate;

/**
 * Exception thrown when a booking conflicts with existing bookings.
 *
 */
@Getter
public class BookingConflictException extends RuntimeException {

    private final Long roomId;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;

    public BookingConflictException(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        super(String.format("Room %d is not available from %s to %s", roomId, checkInDate, checkOutDate));
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public BookingConflictException(String message) {
        super(message);
        this.roomId = null;
        this.checkInDate = null;
        this.checkOutDate = null;
    }

}
