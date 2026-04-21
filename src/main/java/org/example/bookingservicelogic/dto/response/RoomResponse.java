package org.example.bookingservicelogic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bookingservicelogic.entity.enums.RoomType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for room response data.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    private Long id;
    private Long hotelId;
    private String hotelName;
    private String roomNumber;
    private RoomType roomType;
    private String description;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private Integer bedCount;
    private BigDecimal areaSqm;
    private String imageUrl;
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
