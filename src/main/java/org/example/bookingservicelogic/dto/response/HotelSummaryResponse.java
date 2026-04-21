package org.example.bookingservicelogic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for hotel summary response (lightweight version for lists).
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelSummaryResponse {

    private Long id;
    private String name;
    private String city;
    private String country;
    private Long ownerId;
    private String ownerName;
    private BigDecimal rating;
    private Integer starRating;
    private String imageUrl;
    private BigDecimal minPrice;
    private Integer reviewCount;
}
