package org.example.bookingservicelogic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bookingservicelogic.entity.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for booking response data.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long userId;
    private String userFullName;
    private String userEmail;
    private Long roomId;
    private String roomNumber;
    private Long hotelId;
    private String hotelName;
    private String hotelCity;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long nightsCount;
    private Integer guestsCount;
    private BigDecimal totalPrice;
    private BigDecimal pricePerNight;
    private BookingStatus status;
    private String specialRequests;
    private Boolean hasReview;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
