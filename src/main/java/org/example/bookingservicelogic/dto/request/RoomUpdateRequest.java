package org.example.bookingservicelogic.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bookingservicelogic.entity.enums.RoomType;

import java.math.BigDecimal;

/**
 * DTO for updating an existing room.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomUpdateRequest {

    @Size(max = 20, message = "Room number must not exceed 20 characters")
    private String roomNumber;

    private RoomType roomType;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal pricePerNight;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 20, message = "Capacity must not exceed 20")
    private Integer capacity;

    @Min(value = 1, message = "Bed count must be at least 1")
    @Max(value = 10, message = "Bed count must not exceed 10")
    private Integer bedCount;

    @DecimalMin(value = "1.0", message = "Area must be at least 1 sq.m")
    @DecimalMax(value = "1000.0", message = "Area must not exceed 1000 sq.m")
    private BigDecimal areaSqm;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    private Boolean available;
}
