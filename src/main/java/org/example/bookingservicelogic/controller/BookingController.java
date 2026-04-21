package org.example.bookingservicelogic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.BookingCreateRequest;
import org.example.bookingservicelogic.dto.request.BookingUpdateRequest;
import org.example.bookingservicelogic.dto.response.BookingResponse;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.entity.enums.BookingStatus;
import org.example.bookingservicelogic.service.BookingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for booking management operations.
 *
 */
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bookings", description = "Booking management API")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create a new booking")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingCreateRequest request) {
        log.info("REST request to create booking for user {} and room {}", request.getUserId(), request.getRoomId());
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        log.info("REST request to get booking by ID: {}", id);
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all bookings with pagination")
    public ResponseEntity<PageResponse<BookingResponse>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("REST request to get all bookings, page: {}, size: {}", page, size);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<BookingResponse> response = bookingService.getAllBookings(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get bookings by user")
    public ResponseEntity<PageResponse<BookingResponse>> getBookingsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get bookings for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> response = bookingService.getBookingsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get bookings by room")
    public ResponseEntity<PageResponse<BookingResponse>> getBookingsByRoom(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get bookings for room: {}", roomId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> response = bookingService.getBookingsByRoom(roomId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get bookings by hotel")
    public ResponseEntity<PageResponse<BookingResponse>> getBookingsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get bookings for hotel: {}", hotelId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> response = bookingService.getBookingsByHotel(hotelId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get bookings by status")
    public ResponseEntity<PageResponse<BookingResponse>> getBookingsByStatus(
            @PathVariable BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get bookings by status: {}", status);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> response = bookingService.getBookingsByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/status/{status}")
    @Operation(summary = "Get user bookings by status")
    public ResponseEntity<PageResponse<BookingResponse>> getBookingsByUserAndStatus(
            @PathVariable Long userId,
            @PathVariable BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get bookings for user {} with status {}", userId, status);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> response = bookingService.getBookingsByUserAndStatus(userId, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/upcoming")
    @Operation(summary = "Get upcoming bookings for user")
    public ResponseEntity<PageResponse<BookingResponse>> getUpcomingBookings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get upcoming bookings for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> response = bookingService.getUpcomingBookings(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/past")
    @Operation(summary = "Get past bookings for user")
    public ResponseEntity<PageResponse<BookingResponse>> getPastBookings(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get past bookings for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> response = bookingService.getPastBookings(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today/check-ins")
    @Operation(summary = "Get today's check-ins")
    public ResponseEntity<List<BookingResponse>> getTodayCheckIns() {
        log.info("REST request to get today's check-ins");
        List<BookingResponse> response = bookingService.getTodayCheckIns();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today/check-outs")
    @Operation(summary = "Get today's check-outs")
    public ResponseEntity<List<BookingResponse>> getTodayCheckOuts() {
        log.info("REST request to get today's check-outs");
        List<BookingResponse> response = bookingService.getTodayCheckOuts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/revenue")
    @Operation(summary = "Get hotel revenue")
    public ResponseEntity<BigDecimal> getHotelRevenue(@PathVariable Long hotelId) {
        log.info("REST request to get revenue for hotel: {}", hotelId);
        BigDecimal revenue = bookingService.calculateHotelRevenue(hotelId);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/hotel/{hotelId}/count/{status}")
    @Operation(summary = "Count bookings by status for hotel")
    public ResponseEntity<Long> countBookingsByStatus(
            @PathVariable Long hotelId,
            @PathVariable BookingStatus status) {
        log.info("REST request to count bookings for hotel {} with status {}", hotelId, status);
        Long count = bookingService.countBookingsByStatus(hotelId, status);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update booking")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingUpdateRequest request) {
        log.info("REST request to update booking: {}", id);
        BookingResponse response = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm booking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long id) {
        log.info("REST request to confirm booking: {}", id);
        BookingResponse response = bookingService.confirmBooking(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking")
    @PreAuthorize("@bookingSecurity.canModifyBooking(#id, authentication.name) or hasRole('ADMIN')")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        log.info("REST request to cancel booking: {}", id);
        BookingResponse response = bookingService.cancelBooking(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/check-in")
    @Operation(summary = "Check in guest")
    public ResponseEntity<BookingResponse> checkIn(@PathVariable Long id) {
        log.info("REST request to check in booking: {}", id);
        BookingResponse response = bookingService.checkIn(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/check-out")
    @Operation(summary = "Check out guest")
    public ResponseEntity<BookingResponse> checkOut(@PathVariable Long id) {
        log.info("REST request to check out booking: {}", id);
        BookingResponse response = bookingService.checkOut(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete booking")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        log.info("REST request to delete booking: {}", id);
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
