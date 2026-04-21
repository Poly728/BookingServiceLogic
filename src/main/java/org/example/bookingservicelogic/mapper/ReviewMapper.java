package org.example.bookingservicelogic.mapper;

import org.example.bookingservicelogic.dto.request.ReviewCreateRequest;
import org.example.bookingservicelogic.dto.request.ReviewUpdateRequest;
import org.example.bookingservicelogic.dto.response.ReviewResponse;
import org.example.bookingservicelogic.entity.Review;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Review entity.
 *
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewMapper {

    /**
     * Converts ReviewCreateRequest to Review entity.
     *
     * @param request the create request
     * @return Review entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toEntity(ReviewCreateRequest request);

    /**
     * Converts Review entity to ReviewResponse.
     *
     * @param review the entity
     * @return ReviewResponse DTO
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(review.getUser().getFullName())")
    @Mapping(target = "hotelId", source = "hotel.id")
    @Mapping(target = "hotelName", source = "hotel.name")
    @Mapping(target = "bookingId", source = "booking.id")
    ReviewResponse toResponse(Review review);

    /**
     * Converts list of Review entities to list of ReviewResponse.
     *
     * @param reviews list of entities
     * @return list of ReviewResponse DTOs
     */
    List<ReviewResponse> toResponseList(List<Review> reviews);

    /**
     * Updates Review entity from ReviewUpdateRequest.
     *
     * @param request the update request
     * @param review the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ReviewUpdateRequest request, @MappingTarget Review review);
}
