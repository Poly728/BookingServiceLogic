package org.example.bookingservicelogic.repository;

import org.example.bookingservicelogic.entity.Booking;
import org.example.bookingservicelogic.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Booking entity operations.
 * Provides CRUD operations and custom queries for booking management.
 *
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Finds all bookings for a user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of user's bookings
     */
    Page<Booking> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Finds all bookings for a room.
     *
     * @param roomId the room ID
     * @param pageable pagination information
     * @return page of room's bookings
     */
    Page<Booking> findByRoomIdOrderByCheckInDateDesc(Long roomId, Pageable pageable);

    /**
     * Finds bookings by status.
     *
     * @param status the booking status
     * @param pageable pagination information
     * @return page of bookings with specified status
     */
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    /**
     * Finds bookings by user and status.
     *
     * @param userId the user ID
     * @param status the booking status
     * @param pageable pagination information
     * @return page of matching bookings
     */
    Page<Booking> findByUserIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    /**
     * Finds all bookings for a specific hotel.
     *
     * @param hotelId the hotel ID
     * @param pageable pagination information
     * @return page of hotel's bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.room.hotel.id = :hotelId ORDER BY b.checkInDate DESC")
    Page<Booking> findByHotelId(@Param("hotelId") Long hotelId, Pageable pageable);

    /**
     * Finds overlapping bookings for a room.
     *
     * @param roomId the room ID
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return list of overlapping bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.status NOT IN ('CANCELLED', 'REJECTED') " +
            "AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)")
    List<Booking> findOverlappingBookings(@Param("roomId") Long roomId,
                                           @Param("checkIn") LocalDate checkIn,
                                           @Param("checkOut") LocalDate checkOut);

    /**
     * Finds bookings with check-in date today.
     *
     * @param date the date to check
     * @return list of bookings with check-in on specified date
     */
    List<Booking> findByCheckInDateAndStatus(LocalDate date, BookingStatus status);

    /**
     * Finds bookings with check-out date today.
     *
     * @param date the date to check
     * @return list of bookings with check-out on specified date
     */
    List<Booking> findByCheckOutDateAndStatus(LocalDate date, BookingStatus status);

    /**
     * Finds upcoming bookings for a user.
     *
     * @param userId the user ID
     * @param currentDate current date
     * @param pageable pagination information
     * @return page of upcoming bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId " +
            "AND b.checkInDate >= :currentDate " +
            "AND b.status IN ('PENDING', 'CONFIRMED') " +
            "ORDER BY b.checkInDate ASC")
    Page<Booking> findUpcomingBookings(@Param("userId") Long userId,
                                        @Param("currentDate") LocalDate currentDate,
                                        Pageable pageable);

    /**
     * Finds past bookings for a user.
     *
     * @param userId the user ID
     * @param currentDate current date
     * @param pageable pagination information
     * @return page of past bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId " +
            "AND b.checkOutDate < :currentDate " +
            "ORDER BY b.checkOutDate DESC")
    Page<Booking> findPastBookings(@Param("userId") Long userId,
                                    @Param("currentDate") LocalDate currentDate,
                                    Pageable pageable);

    /**
     * Calculates total revenue for a hotel.
     *
     * @param hotelId the hotel ID
     * @return total revenue
     */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b " +
            "WHERE b.room.hotel.id = :hotelId " +
            "AND b.status IN ('CONFIRMED', 'CHECKED_IN', 'COMPLETED')")
    BigDecimal calculateTotalRevenueByHotel(@Param("hotelId") Long hotelId);

    /**
     * Counts bookings by status for a hotel.
     *
     * @param hotelId the hotel ID
     * @param status the booking status
     * @return count of bookings
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.room.hotel.id = :hotelId AND b.status = :status")
    Long countByHotelIdAndStatus(@Param("hotelId") Long hotelId, @Param("status") BookingStatus status);

    /**
     * Finds bookings within a date range.
     *
     * @param startDate start of date range
     * @param endDate end of date range
     * @param pageable pagination information
     * @return page of bookings within date range
     */
    @Query("SELECT b FROM Booking b WHERE " +
            "(b.checkInDate BETWEEN :startDate AND :endDate) " +
            "OR (b.checkOutDate BETWEEN :startDate AND :endDate)")
    Page<Booking> findBookingsInDateRange(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           Pageable pageable);

    /**
     * Checks if user has a booking for this room (completed).
     *
     * @param userId the user ID
     * @param hotelId the hotel ID
     * @return true if user has completed booking for this hotel
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.user.id = :userId " +
            "AND b.room.hotel.id = :hotelId " +
            "AND b.status = 'COMPLETED'")
    boolean hasCompletedBookingForHotel(@Param("userId") Long userId, @Param("hotelId") Long hotelId);
}
