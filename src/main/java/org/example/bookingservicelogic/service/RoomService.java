package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.RoomCreateRequest;
import org.example.bookingservicelogic.dto.request.RoomUpdateRequest;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.dto.response.RoomResponse;
import org.example.bookingservicelogic.entity.enums.RoomType;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for room management operations.
 *
 */
public interface RoomService {

    /**
     * Creates a new room.
     *
     * @param request the room creation request
     * @return the created room response
     */
    RoomResponse createRoom(RoomCreateRequest request);

    /**
     * Retrieves a room by ID.
     *
     * @param id the room ID
     * @return the room response
     */
    RoomResponse getRoomById(Long id);

    /**
     * Retrieves all rooms for a hotel.
     *
     * @param hotelId the hotel ID
     * @param pageable pagination information
     * @return page of room responses
     */
    PageResponse<RoomResponse> getRoomsByHotel(Long hotelId, Pageable pageable);

    /**
     * Retrieves available rooms for a hotel.
     *
     * @param hotelId the hotel ID
     * @param pageable pagination information
     * @return page of available room responses
     */
    PageResponse<RoomResponse> getAvailableRoomsByHotel(Long hotelId, Pageable pageable);

    /**
     * Retrieves rooms by type.
     *
     * @param roomType the room type
     * @param pageable pagination information
     * @return page of room responses
     */
    PageResponse<RoomResponse> getRoomsByType(RoomType roomType, Pageable pageable);

    /**
     * Retrieves rooms within a price range.
     *
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @param pageable pagination information
     * @return page of room responses
     */
    PageResponse<RoomResponse> getRoomsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Retrieves rooms with minimum capacity.
     *
     * @param capacity minimum capacity
     * @param pageable pagination information
     * @return page of room responses
     */
    PageResponse<RoomResponse> getRoomsByMinCapacity(Integer capacity, Pageable pageable);

    /**
     * Finds available rooms for specific dates.
     *
     * @param hotelId the hotel ID
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return list of available room responses
     */
    List<RoomResponse> findAvailableRoomsForDates(Long hotelId, LocalDate checkIn, LocalDate checkOut);

    /**
     * Finds available rooms with filters.
     *
     * @param hotelId the hotel ID
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @param minCapacity minimum capacity
     * @param maxPrice maximum price per night
     * @return list of matching room responses
     */
    List<RoomResponse> findAvailableRoomsWithFilters(Long hotelId, LocalDate checkIn, LocalDate checkOut,
                                                      Integer minCapacity, BigDecimal maxPrice);

    /**
     * Checks if a room is available for given dates.
     *
     * @param roomId the room ID
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return true if room is available
     */
    boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut);

    /**
     * Updates an existing room.
     *
     * @param id the room ID
     * @param request the update request
     * @return the updated room response
     */
    RoomResponse updateRoom(Long id, RoomUpdateRequest request);

    /**
     * Deletes a room.
     *
     * @param id the room ID
     */
    void deleteRoom(Long id);

    /**
     * Sets room availability.
     *
     * @param id the room ID
     * @param available the availability status
     * @return the updated room response
     */
    RoomResponse setRoomAvailability(Long id, boolean available);
}
