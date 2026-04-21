package org.example.bookingservicelogic.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new review.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    private Long bookingId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    private String comment;
}
