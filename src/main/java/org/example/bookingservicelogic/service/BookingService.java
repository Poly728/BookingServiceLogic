package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.BookingCreateRequest;
import org.example.bookingservicelogic.dto.request.BookingUpdateRequest;
import org.example.bookingservicelogic.dto.response.BookingResponse;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.entity.enums.BookingStatus;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for booking management operations.
 *
 */
public interface BookingService {

    /**
     * Creates a new booking.
     *
     * @param request the booking creation request
     * @return the created booking response
     */
    BookingResponse createBooking(BookingCreateRequest request);

    /**
     * Retrieves a booking by ID.
     *
     * @param id the booking ID
     * @return the booking response
     */
    BookingResponse getBookingById(Long id);

    /**
     * Retrieves all bookings with pagination.
     *
     * @param pageable pagination information
     * @return page of booking responses
     */
    PageResponse<BookingResponse> getAllBookings(Pageable pageable);

    /**
     * Retrieves bookings for a user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of booking responses
     */
    PageResponse<BookingResponse> getBookingsByUser(Long userId, Pageable pageable);

    /**
     * Retrieves bookings for a room.
     *
     * @param roomId the room ID
     * @param pageable pagination information
     * @return page of booking responses
     */
    PageResponse<BookingResponse> getBookingsByRoom(Long roomId, Pageable pageable);

    /**
     * Retrieves bookings for a hotel.
     *
     * @param hotelId the hotel ID
     * @param pageable pagination information
     * @return page of booking responses
     */
    PageResponse<BookingResponse> getBookingsByHotel(Long hotelId, Pageable pageable);

    /**
     * Retrieves bookings by status.
     *
     * @param status the booking status
     * @param pageable pagination information
     * @return page of booking responses
     */
    PageResponse<BookingResponse> getBookingsByStatus(BookingStatus status, Pageable pageable);

    /**
     * Retrieves bookings by user and status.
     *
     * @param userId the user ID
     * @param status the booking status
     * @param pageable pagination information
     * @return page of booking responses
     */
    PageResponse<BookingResponse> getBookingsByUserAndStatus(Long userId, BookingStatus status, Pageable pageable);

    /**
     * Retrieves upcoming bookings for a user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of upcoming booking responses
     */
    PageResponse<BookingResponse> getUpcomingBookings(Long userId, Pageable pageable);

    /**
     * Retrieves past bookings for a user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of past booking responses
     */
    PageResponse<BookingResponse> getPastBookings(Long userId, Pageable pageable);

    /**
     * Retrieves bookings with check-in today.
     *
     * @return list of booking responses
     */
    List<BookingResponse> getTodayCheckIns();

    /**
     * Retrieves bookings with check-out today.
     *
     * @return list of booking responses
     */
    List<BookingResponse> getTodayCheckOuts();

    /**
     * Updates an existing booking.
     *
     * @param id the booking ID
     * @param request the update request
     * @return the updated booking response
     */
    BookingResponse updateBooking(Long id, BookingUpdateRequest request);

    /**
     * Confirms a booking.
     *
     * @param id the booking ID
     * @return the confirmed booking response
     */
    BookingResponse confirmBooking(Long id);

    /**
     * Cancels a booking.
     *
     * @param id the booking ID
     * @return the cancelled booking response
     */
    BookingResponse cancelBooking(Long id);

    /**
     * Checks in a guest.
     *
     * @param id the booking ID
     * @return the updated booking response
     */
    BookingResponse checkIn(Long id);

    /**
     * Checks out a guest.
     *
     * @param id the booking ID
     * @return the completed booking response
     */
    BookingResponse checkOut(Long id);

    /**
     * Deletes a booking.
     *
     * @param id the booking ID
     */
    void deleteBooking(Long id);

    /**
     * Calculates total revenue for a hotel.
     *
     * @param hotelId the hotel ID
     * @return total revenue
     */
    BigDecimal calculateHotelRevenue(Long hotelId);

    /**
     * Counts bookings by status for a hotel.
     *
     * @param hotelId the hotel ID
     * @param status the booking status
     * @return count of bookings
     */
    Long countBookingsByStatus(Long hotelId, BookingStatus status);
}
