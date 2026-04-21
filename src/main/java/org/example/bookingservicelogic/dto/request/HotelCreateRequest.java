package org.example.bookingservicelogic.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for creating a new hotel/property.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelCreateRequest {

    @NotBlank(message = "Hotel name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Size(max = 20, message = "Zip code must not exceed 20 characters")
    private String zipCode;

    @Pattern(regexp = "^\\+?[0-9]{10,20}$", message = "Invalid phone number format")
    private String phone;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Min(value = 1, message = "Star rating must be at least 1")
    @Max(value = 5, message = "Star rating must not exceed 5")
    private Integer starRating;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    private Long ownerId;

    private Set<Long> amenityIds;
}
