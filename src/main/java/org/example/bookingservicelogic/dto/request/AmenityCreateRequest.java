package org.example.bookingservicelogic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new amenity.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenityCreateRequest {

    @NotBlank(message = "Amenity name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @Size(max = 50, message = "Icon must not exceed 50 characters")
    private String icon;
}
