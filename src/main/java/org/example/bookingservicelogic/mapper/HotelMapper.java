package org.example.bookingservicelogic.mapper;

import org.example.bookingservicelogic.dto.request.HotelCreateRequest;
import org.example.bookingservicelogic.dto.request.HotelUpdateRequest;
import org.example.bookingservicelogic.dto.response.HotelResponse;
import org.example.bookingservicelogic.dto.response.HotelSummaryResponse;
import org.example.bookingservicelogic.entity.Hotel;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Hotel entity.
 *
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AmenityMapper.class})
public interface HotelMapper {

    /**
     * Converts HotelCreateRequest to Hotel entity.
     *
     * @param request the create request
     * @return Hotel entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", constant = "0.0")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Hotel toEntity(HotelCreateRequest request);

    /**
     * Converts Hotel entity to HotelResponse.
     *
     * @param hotel the entity
     * @return HotelResponse DTO
     */
    @Mapping(target = "roomCount", expression = "java(hotel.getRooms() != null ? hotel.getRooms().size() : 0)")
    @Mapping(target = "reviewCount", expression = "java(hotel.getReviews() != null ? hotel.getReviews().size() : 0)")
    @Mapping(target = "ownerId", expression = "java(hotel.getOwner() != null ? hotel.getOwner().getId() : null)")
    @Mapping(target = "ownerName", expression = "java(hotel.getOwner() != null ? hotel.getOwner().getUsername() : null)")
    @Mapping(target = "minPrice", ignore = true)
    @Mapping(target = "maxPrice", ignore = true)
    HotelResponse toResponse(Hotel hotel);

    /**
     * Converts Hotel entity to HotelSummaryResponse.
     *
     * @param hotel the entity
     * @return HotelSummaryResponse DTO
     */
    @Mapping(target = "reviewCount", expression = "java(hotel.getReviews() != null ? hotel.getReviews().size() : 0)")
    @Mapping(target = "ownerId", expression = "java(hotel.getOwner() != null ? hotel.getOwner().getId() : null)")
    @Mapping(target = "ownerName", expression = "java(hotel.getOwner() != null ? hotel.getOwner().getUsername() : null)")
    @Mapping(target = "minPrice", ignore = true)
    HotelSummaryResponse toSummaryResponse(Hotel hotel);

    /**
     * Converts list of Hotel entities to list of HotelSummaryResponse.
     *
     * @param hotels list of entities
     * @return list of HotelSummaryResponse DTOs
     */
    List<HotelSummaryResponse> toSummaryResponseList(List<Hotel> hotels);

    /**
     * Updates Hotel entity from HotelUpdateRequest.
     *
     * @param request the update request
     * @param hotel the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "owner", ignore = true)
    void updateEntityFromRequest(HotelUpdateRequest request, @MappingTarget Hotel hotel);
}
