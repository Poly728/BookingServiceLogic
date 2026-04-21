package org.example.bookingservicelogic.entity.enums;

/**
 * Enumeration representing the status of a booking.
 *
 */
public enum BookingStatus {

    /** Booking is pending confirmation */
    PENDING,

    /** Booking has been confirmed */
    CONFIRMED,

    /** Booking has been cancelled */
    CANCELLED,

    /** Guest has checked in */
    CHECKED_IN,

    /** Guest has checked out, booking completed */
    COMPLETED,

    /** Booking was rejected */
    REJECTED
}
