package org.example.bookingservicelogic.mapper;

import org.example.bookingservicelogic.dto.request.BookingCreateRequest;
import org.example.bookingservicelogic.dto.request.BookingUpdateRequest;
import org.example.bookingservicelogic.dto.response.BookingResponse;
import org.example.bookingservicelogic.entity.Booking;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Booking entity.
 *
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapper {

    /**
     * Converts BookingCreateRequest to Booking entity.
     *
     * @param request the create request
     * @return Booking entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "review", ignore = true)
    Booking toEntity(BookingCreateRequest request);

    /**
     * Converts Booking entity to BookingResponse.
     *
     * @param booking the entity
     * @return BookingResponse DTO
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFullName", expression = "java(booking.getUser().getFullName())")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomNumber", source = "room.roomNumber")
    @Mapping(target = "hotelId", source = "room.hotel.id")
    @Mapping(target = "hotelName", source = "room.hotel.name")
    @Mapping(target = "hotelCity", source = "room.hotel.city")
    @Mapping(target = "nightsCount", expression = "java(booking.getNightsCount())")
    @Mapping(target = "pricePerNight", source = "room.pricePerNight")
    @Mapping(target = "hasReview", expression = "java(booking.getReview() != null)")
    BookingResponse toResponse(Booking booking);

    /**
     * Converts list of Booking entities to list of BookingResponse.
     *
     * @param bookings list of entities
     * @return list of BookingResponse DTOs
     */
    List<BookingResponse> toResponseList(List<Booking> bookings);

    /**
     * Updates Booking entity from BookingUpdateRequest.
     *
     * @param request the update request
     * @param booking the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "review", ignore = true)
    void updateEntityFromRequest(BookingUpdateRequest request, @MappingTarget Booking booking);
}
