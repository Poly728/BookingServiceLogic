package org.example.bookingservicelogic.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bookingservicelogic.entity.enums.BookingStatus;

import java.time.LocalDate;

/**
 * DTO for updating an existing booking.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingUpdateRequest {

    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "At least 1 guest is required")
    @Max(value = 20, message = "Maximum 20 guests allowed")
    private Integer guestsCount;

    private BookingStatus status;

    @Size(max = 500, message = "Special requests must not exceed 500 characters")
    private String specialRequests;
}
