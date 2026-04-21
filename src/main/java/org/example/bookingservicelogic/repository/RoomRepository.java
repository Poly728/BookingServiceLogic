package org.example.bookingservicelogic.repository;

import org.example.bookingservicelogic.entity.Room;
import org.example.bookingservicelogic.entity.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Room entity operations.
 * Provides CRUD operations and custom queries for room management.
 *
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Finds all rooms for a specific hotel.
     *
     * @param hotelId the hotel ID
     * @param pageable pagination information
     * @return page of rooms in the hotel
     */
    Page<Room> findByHotelId(Long hotelId, Pageable pageable);

    /**
     * Finds available rooms for a specific hotel.
     *
     * @param hotelId the hotel ID
     * @param pageable pagination information
     * @return page of available rooms
     */
    Page<Room> findByHotelIdAndAvailableTrue(Long hotelId, Pageable pageable);

    /**
     * Finds a room by hotel ID and room number.
     *
     * @param hotelId the hotel ID
     * @param roomNumber the room number
     * @return Optional containing the room if found
     */
    Optional<Room> findByHotelIdAndRoomNumber(Long hotelId, String roomNumber);

    /**
     * Finds rooms by type.
     *
     * @param roomType the room type
     * @param pageable pagination information
     * @return page of rooms of the specified type
     */
    Page<Room> findByRoomTypeAndAvailableTrue(RoomType roomType, Pageable pageable);

    /**
     * Finds rooms with price in range.
     *
     * @param minPrice minimum price per night
     * @param maxPrice maximum price per night
     * @param pageable pagination information
     * @return page of rooms within price range
     */
    @Query("SELECT r FROM Room r WHERE r.available = true " +
            "AND r.pricePerNight BETWEEN :minPrice AND :maxPrice")
    Page<Room> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 Pageable pageable);

    /**
     * Finds rooms with minimum capacity.
     *
     * @param capacity minimum capacity
     * @param pageable pagination information
     * @return page of rooms with capacity >= specified value
     */
    Page<Room> findByCapacityGreaterThanEqualAndAvailableTrue(Integer capacity, Pageable pageable);

    /**
     * Finds available rooms for specific dates (no overlapping bookings).
     *
     * @param hotelId the hotel ID
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return list of available rooms
     */
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId " +
            "AND r.available = true " +
            "AND r.id NOT IN (" +
            "   SELECT b.room.id FROM Booking b " +
            "   WHERE b.room.hotel.id = :hotelId " +
            "   AND b.status NOT IN ('CANCELLED', 'REJECTED') " +
            "   AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)" +
            ")")
    List<Room> findAvailableRoomsForDates(@Param("hotelId") Long hotelId,
                                           @Param("checkIn") LocalDate checkIn,
                                           @Param("checkOut") LocalDate checkOut);

    /**
     * Finds available rooms with filters.
     *
     * @param hotelId the hotel ID
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @param minCapacity minimum capacity
     * @param maxPrice maximum price per night
     * @return list of matching available rooms
     */
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId " +
            "AND r.available = true " +
            "AND r.capacity >= :minCapacity " +
            "AND r.pricePerNight <= :maxPrice " +
            "AND r.id NOT IN (" +
            "   SELECT b.room.id FROM Booking b " +
            "   WHERE b.room.hotel.id = :hotelId " +
            "   AND b.status NOT IN ('CANCELLED', 'REJECTED') " +
            "   AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)" +
            ")")
    List<Room> findAvailableRoomsWithFilters(@Param("hotelId") Long hotelId,
                                              @Param("checkIn") LocalDate checkIn,
                                              @Param("checkOut") LocalDate checkOut,
                                              @Param("minCapacity") Integer minCapacity,
                                              @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Checks if room is available for given dates.
     *
     * @param roomId the room ID
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return true if room is available for the dates
     */
    @Query("SELECT COUNT(b) = 0 FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND b.status NOT IN ('CANCELLED', 'REJECTED') " +
            "AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)")
    boolean isRoomAvailableForDates(@Param("roomId") Long roomId,
                                     @Param("checkIn") LocalDate checkIn,
                                     @Param("checkOut") LocalDate checkOut);

    /**
     * Gets minimum room price for a hotel.
     *
     * @param hotelId the hotel ID
     * @return minimum price or null if no rooms
     */
    @Query("SELECT MIN(r.pricePerNight) FROM Room r " +
            "WHERE r.hotel.id = :hotelId AND r.available = true")
    BigDecimal findMinPriceByHotel(@Param("hotelId") Long hotelId);

    /**
     * Gets maximum room price for a hotel.
     *
     * @param hotelId the hotel ID
     * @return maximum price or null if no rooms
     */
    @Query("SELECT MAX(r.pricePerNight) FROM Room r " +
            "WHERE r.hotel.id = :hotelId AND r.available = true")
    BigDecimal findMaxPriceByHotel(@Param("hotelId") Long hotelId);
}
