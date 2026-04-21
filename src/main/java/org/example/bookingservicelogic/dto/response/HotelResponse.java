package org.example.bookingservicelogic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for hotel response data.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {

    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String country;
    private String zipCode;
    private String phone;
    private String email;
    private Long ownerId;
    private String ownerName;
    private BigDecimal rating;
    private Integer starRating;
    private String imageUrl;
    private Boolean active;
    private Integer roomCount;
    private Integer reviewCount;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Set<AmenityResponse> amenities;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
