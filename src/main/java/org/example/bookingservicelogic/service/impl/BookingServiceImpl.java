package org.example.bookingservicelogic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.BookingCreateRequest;
import org.example.bookingservicelogic.dto.request.BookingUpdateRequest;
import org.example.bookingservicelogic.dto.response.BookingResponse;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.entity.Booking;
import org.example.bookingservicelogic.entity.Room;
import org.example.bookingservicelogic.entity.User;
import org.example.bookingservicelogic.entity.enums.BookingStatus;
import org.example.bookingservicelogic.exception.BadRequestException;
import org.example.bookingservicelogic.exception.BookingConflictException;
import org.example.bookingservicelogic.exception.ResourceNotFoundException;
import org.example.bookingservicelogic.mapper.BookingMapper;
import org.example.bookingservicelogic.repository.BookingRepository;
import org.example.bookingservicelogic.repository.RoomRepository;
import org.example.bookingservicelogic.repository.UserRepository;
import org.example.bookingservicelogic.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of BookingService interface.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request) {
        log.debug("Creating booking for user {} and room {}", request.getUserId(), request.getRoomId());

        validateBookingDates(request.getCheckInDate(), request.getCheckOutDate());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));

        if (!room.getAvailable()) {
            throw new BadRequestException("Room is not available for booking");
        }

        if (request.getGuestsCount() > room.getCapacity()) {
            throw new BadRequestException("Number of guests exceeds room capacity");
        }

        // Check for conflicting bookings
        List<Booking> conflicts = bookingRepository.findOverlappingBookings(
                request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate());
        if (!conflicts.isEmpty()) {
            throw new BookingConflictException(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate());
        }

        Booking booking = bookingMapper.toEntity(request);
        booking.setUser(user);
        booking.setRoom(room);
        booking.calculateTotalPrice();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Created booking with ID: {}", savedBooking.getId());

        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    public BookingResponse getBookingById(Long id) {
        log.debug("Fetching booking by ID: {}", id);
        Booking booking = findBookingById(id);
        return bookingMapper.toResponse(booking);
    }

    @Override
    public PageResponse<BookingResponse> getAllBookings(Pageable pageable) {
        log.debug("Fetching all bookings, page: {}", pageable.getPageNumber());
        Page<Booking> bookings = bookingRepository.findAll(pageable);
        List<BookingResponse> content = bookingMapper.toResponseList(bookings.getContent());
        return PageResponse.of(bookings, content);
    }

    @Override
    public PageResponse<BookingResponse> getBookingsByUser(Long userId, Pageable pageable) {
        log.debug("Fetching bookings for user ID: {}", userId);
        Page<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<BookingResponse> content = bookingMapper.toResponseList(bookings.getContent());
        return PageResponse.of(bookings, content);
    }

    @Override
    public PageResponse<BookingResponse> getBookingsByRoom(Long roomId, Pageable pageable) {
        log.debug("Fetching bookings for room ID: {}", roomId);
        Page<Booking> bookings = bookingRepository.findByRoomIdOrderByCheckInDateDesc(roomId, pageable);
        List<BookingResponse> content = bookingMapper.toResponseList(bookings.getContent());
        return PageResponse.of(bookings, content);
    }

    @Override
    public PageResponse<BookingResponse> getBookingsByHotel(Long hotelId, Pageable pageable) {
        log.debug("Fetching bookings for hotel ID: {}", hotelId);
        Page<Booking> bookings = bookingRepository.findByHotelId(hotelId, pageable);
        List<BookingResponse> content = bookingMapper.toResponseList(bookings.getContent());
        return PageResponse.of(bookings, content);
    }

    @Override
    public PageResponse<BookingResponse> getBookingsByStatus(BookingStatus status, Pageable pageable) {
        log.debug("Fetching bookings by status: {}", status);
        Page<Booking> bookings = bookingRepository.findByStatus(status, pageable);
        List<BookingResponse> content = bookingMapper.toResponseList(bookings.getContent());
        return PageResponse.of(bookings, content);
    }

    @Override
    public PageResponse<BookingResponse> getBookingsByUserAndStatus(Long userId, BookingStatus status, Pageable pageable) {
        log.debug("Fetching bookings for user {} with status {}", userId, status);
        Page<Booking> bookings = bookingRepository.findByUserIdAndStatus(userId, status, pageable);
        List<BookingResponse> content = bookingMapper.toResponseList(bookings.getContent());
        return PageResponse.of(bookings, content);
    }

    @Override
    public PageResponse<BookingResponse> getUpcomingBookings(Long userId, Pageable pageable) {
        log.debug("Fetching upcoming bookings for user ID: {}", userId);
        Page<Booking> bookings = bookingRepository.findUpcomingBookings(userId, LocalDate.now(), pageable);
        List<BookingResponse> content = bookingMapper.toResponseList(bookings.getContent());
        return PageResponse.of(bookings, content);
    }

    @Override
    public PageResponse<BookingResponse> getPastBookings(Long userId, Pageable pageable) {
        log.debug("Fetching past bookings for user ID: {}", userId);
        Page<Booking> bookings = bookingRepository.findPastBookings(userId, LocalDate.now(), pageable);
        List<BookingResponse> content = bookingMapper.toResponseList(bookings.getContent());
        return PageResponse.of(bookings, content);
    }

    @Override
    public List<BookingResponse> getTodayCheckIns() {
        log.debug("Fetching today's check-ins");
        List<Booking> bookings = bookingRepository.findByCheckInDateAndStatus(LocalDate.now(), BookingStatus.CONFIRMED);
        return bookingMapper.toResponseList(bookings);
    }

    @Override
    public List<BookingResponse> getTodayCheckOuts() {
        log.debug("Fetching today's check-outs");
        List<Booking> bookings = bookingRepository.findByCheckOutDateAndStatus(LocalDate.now(), BookingStatus.CHECKED_IN);
        return bookingMapper.toResponseList(bookings);
    }

    @Override
    @Transactional
    public BookingResponse updateBooking(Long id, BookingUpdateRequest request) {
        log.debug("Updating booking with ID: {}", id);
        Booking booking = findBookingById(id);

        if (request.getCheckInDate() != null || request.getCheckOutDate() != null) {
            LocalDate newCheckIn = request.getCheckInDate() != null ? request.getCheckInDate() : booking.getCheckInDate();
            LocalDate newCheckOut = request.getCheckOutDate() != null ? request.getCheckOutDate() : booking.getCheckOutDate();
            validateBookingDates(newCheckIn, newCheckOut);

            // Check for conflicts excluding current booking
            List<Booking> conflicts = bookingRepository.findOverlappingBookings(
                    booking.getRoom().getId(), newCheckIn, newCheckOut);
            conflicts.removeIf(b -> b.getId().equals(id));
            if (!conflicts.isEmpty()) {
                throw new BookingConflictException(booking.getRoom().getId(), newCheckIn, newCheckOut);
            }
        }

        bookingMapper.updateEntityFromRequest(request, booking);
        booking.calculateTotalPrice();

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Updated booking with ID: {}", id);

        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    @Transactional
    public BookingResponse confirmBooking(Long id) {
        log.debug("Confirming booking with ID: {}", id);
        Booking booking = findBookingById(id);
        validateStatusTransition(booking, BookingStatus.CONFIRMED);
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Confirmed booking with ID: {}", id);
        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id) {
        log.debug("Cancelling booking with ID: {}", id);
        Booking booking = findBookingById(id);
        if (!booking.canBeCancelled()) {
            throw new BadRequestException("Booking cannot be cancelled in current status: " + booking.getStatus());
        }
        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Cancelled booking with ID: {}", id);
        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    @Transactional
    public BookingResponse checkIn(Long id) {
        log.debug("Checking in booking with ID: {}", id);
        Booking booking = findBookingById(id);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Can only check in confirmed bookings");
        }
        booking.setStatus(BookingStatus.CHECKED_IN);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Checked in booking with ID: {}", id);
        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    @Transactional
    public BookingResponse checkOut(Long id) {
        log.debug("Checking out booking with ID: {}", id);
        Booking booking = findBookingById(id);
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new BadRequestException("Can only check out checked-in bookings");
        }
        booking.setStatus(BookingStatus.COMPLETED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Checked out booking with ID: {}", id);
        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        log.debug("Deleting booking with ID: {}", id);
        Booking booking = findBookingById(id);
        bookingRepository.delete(booking);
        log.info("Deleted booking with ID: {}", id);
    }

    @Override
    public BigDecimal calculateHotelRevenue(Long hotelId) {
        log.debug("Calculating revenue for hotel ID: {}", hotelId);
        return bookingRepository.calculateTotalRevenueByHotel(hotelId);
    }

    @Override
    public Long countBookingsByStatus(Long hotelId, BookingStatus status) {
        log.debug("Counting bookings for hotel {} with status {}", hotelId, status);
        return bookingRepository.countByHotelIdAndStatus(hotelId, status);
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
    }

    private void validateBookingDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new BadRequestException("Check-in and check-out dates are required");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the past");
        }
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
    }

    private void validateStatusTransition(Booking booking, BookingStatus newStatus) {
        BookingStatus currentStatus = booking.getStatus();
        boolean valid = switch (newStatus) {
            case CONFIRMED -> currentStatus == BookingStatus.PENDING;
            case CANCELLED -> currentStatus == BookingStatus.PENDING || currentStatus == BookingStatus.CONFIRMED;
            case CHECKED_IN -> currentStatus == BookingStatus.CONFIRMED;
            case COMPLETED -> currentStatus == BookingStatus.CHECKED_IN;
            case REJECTED -> currentStatus == BookingStatus.PENDING;
            default -> false;
        };
        if (!valid) {
            throw new BadRequestException("Cannot transition from " + currentStatus + " to " + newStatus);
        }
    }
}
