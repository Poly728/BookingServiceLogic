package org.example.bookingservicelogic.mapper;

import org.example.bookingservicelogic.dto.request.RoomCreateRequest;
import org.example.bookingservicelogic.dto.request.RoomUpdateRequest;
import org.example.bookingservicelogic.dto.response.RoomResponse;
import org.example.bookingservicelogic.entity.Room;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Room entity.
 *
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomMapper {

    /**
     * Converts RoomCreateRequest to Room entity.
     *
     * @param request the create request
     * @return Room entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "available", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    Room toEntity(RoomCreateRequest request);

    /**
     * Converts Room entity to RoomResponse.
     *
     * @param room the entity
     * @return RoomResponse DTO
     */
    @Mapping(target = "hotelId", source = "hotel.id")
    @Mapping(target = "hotelName", source = "hotel.name")
    RoomResponse toResponse(Room room);

    /**
     * Converts list of Room entities to list of RoomResponse.
     *
     * @param rooms list of entities
     * @return list of RoomResponse DTOs
     */
    List<RoomResponse> toResponseList(List<Room> rooms);

    /**
     * Updates Room entity from RoomUpdateRequest.
     *
     * @param request the update request
     * @param room the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    void updateEntityFromRequest(RoomUpdateRequest request, @MappingTarget Room room);
}
