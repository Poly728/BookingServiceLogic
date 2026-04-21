package org.example.bookingservicelogic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.RoomCreateRequest;
import org.example.bookingservicelogic.dto.request.RoomUpdateRequest;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.dto.response.RoomResponse;
import org.example.bookingservicelogic.entity.enums.RoomType;
import org.example.bookingservicelogic.service.RoomService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for room management operations.
 *
 */
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rooms", description = "Room management API")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "Create a new room")
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        log.info("REST request to create room for hotel: {}", request.getHotelId());
        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        log.info("REST request to get room by ID: {}", id);
        RoomResponse response = roomService.getRoomById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get all rooms for a hotel")
    public ResponseEntity<PageResponse<RoomResponse>> getRoomsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "pricePerNight") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("REST request to get rooms for hotel: {}", hotelId);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<RoomResponse> response = roomService.getRoomsByHotel(hotelId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/available")
    @Operation(summary = "Get available rooms for a hotel")
    public ResponseEntity<PageResponse<RoomResponse>> getAvailableRoomsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get available rooms for hotel: {}", hotelId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<RoomResponse> response = roomService.getAvailableRoomsByHotel(hotelId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{roomType}")
    @Operation(summary = "Get rooms by type")
    public ResponseEntity<PageResponse<RoomResponse>> getRoomsByType(
            @PathVariable RoomType roomType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get rooms by type: {}", roomType);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<RoomResponse> response = roomService.getRoomsByType(roomType, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/price")
    @Operation(summary = "Get rooms by price range")
    public ResponseEntity<PageResponse<RoomResponse>> getRoomsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get rooms by price range: {} - {}", minPrice, maxPrice);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<RoomResponse> response = roomService.getRoomsByPriceRange(minPrice, maxPrice, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/capacity/{capacity}")
    @Operation(summary = "Get rooms by minimum capacity")
    public ResponseEntity<PageResponse<RoomResponse>> getRoomsByMinCapacity(
            @PathVariable Integer capacity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get rooms with min capacity: {}", capacity);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<RoomResponse> response = roomService.getRoomsByMinCapacity(capacity, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/available-dates")
    @Operation(summary = "Find available rooms for specific dates")
    public ResponseEntity<List<RoomResponse>> findAvailableRoomsForDates(
            @PathVariable Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        log.info("REST request to find available rooms for hotel {} from {} to {}", hotelId, checkIn, checkOut);
        List<RoomResponse> response = roomService.findAvailableRoomsForDates(hotelId, checkIn, checkOut);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/search")
    @Operation(summary = "Find available rooms with filters")
    public ResponseEntity<List<RoomResponse>> findAvailableRoomsWithFilters(
            @PathVariable Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "1") Integer guests,
            @RequestParam(required = false) BigDecimal maxPrice) {
        log.info("REST request to find rooms with filters for hotel {}", hotelId);
        BigDecimal price = maxPrice != null ? maxPrice : BigDecimal.valueOf(10000);
        List<RoomResponse> response = roomService.findAvailableRoomsWithFilters(
                hotelId, checkIn, checkOut, guests, price);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Check room availability for dates")
    public ResponseEntity<Boolean> checkRoomAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        log.info("REST request to check availability for room {} from {} to {}", id, checkIn, checkOut);
        boolean available = roomService.isRoomAvailable(id, checkIn, checkOut);
        return ResponseEntity.ok(available);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomUpdateRequest request) {
        log.info("REST request to update room: {}", id);
        RoomResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Set room availability")
    public ResponseEntity<RoomResponse> setRoomAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        log.info("REST request to set availability for room {} to {}", id, available);
        RoomResponse response = roomService.setRoomAvailability(id, available);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete room")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        log.info("REST request to delete room: {}", id);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
