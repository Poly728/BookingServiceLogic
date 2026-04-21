package org.example.bookingservicelogic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.AmenityCreateRequest;
import org.example.bookingservicelogic.dto.response.AmenityResponse;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.service.AmenityService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * REST controller for amenity management operations.
 *
 */
@RestController
@RequestMapping("/amenities")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Amenities", description = "Amenity management API")
public class AmenityController {

    private final AmenityService amenityService;

    @PostMapping
    @Operation(summary = "Create a new amenity")
    public ResponseEntity<AmenityResponse> createAmenity(@Valid @RequestBody AmenityCreateRequest request) {
        log.info("REST request to create amenity: {}", request.getName());
        AmenityResponse response = amenityService.createAmenity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get amenity by ID")
    public ResponseEntity<AmenityResponse> getAmenityById(@PathVariable Long id) {
        log.info("REST request to get amenity by ID: {}", id);
        AmenityResponse response = amenityService.getAmenityById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get amenity by name")
    public ResponseEntity<AmenityResponse> getAmenityByName(@PathVariable String name) {
        log.info("REST request to get amenity by name: {}", name);
        AmenityResponse response = amenityService.getAmenityByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all amenities")
    public ResponseEntity<List<AmenityResponse>> getAllAmenities() {
        log.info("REST request to get all amenities");
        List<AmenityResponse> response = amenityService.getAllAmenities();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paged")
    @Operation(summary = "Get all amenities with pagination")
    public ResponseEntity<PageResponse<AmenityResponse>> getAllAmenitiesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get all amenities paged, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<AmenityResponse> response = amenityService.getAllAmenitiesPaged(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get amenities by hotel")
    public ResponseEntity<Set<AmenityResponse>> getAmenitiesByHotel(@PathVariable Long hotelId) {
        log.info("REST request to get amenities for hotel: {}", hotelId);
        Set<AmenityResponse> response = amenityService.getAmenitiesByHotel(hotelId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search amenities")
    public ResponseEntity<PageResponse<AmenityResponse>> searchAmenities(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to search amenities with query: {}", query);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<AmenityResponse> response = amenityService.searchAmenities(query, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get most popular amenities")
    public ResponseEntity<List<AmenityResponse>> getMostPopularAmenities(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("REST request to get {} most popular amenities", limit);
        List<AmenityResponse> response = amenityService.getMostPopularAmenities(limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/hotel-count")
    @Operation(summary = "Count hotels with amenity")
    public ResponseEntity<Long> countHotelsByAmenity(@PathVariable Long id) {
        log.info("REST request to count hotels with amenity: {}", id);
        Long count = amenityService.countHotelsByAmenity(id);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update amenity")
    public ResponseEntity<AmenityResponse> updateAmenity(
            @PathVariable Long id,
            @Valid @RequestBody AmenityCreateRequest request) {
        log.info("REST request to update amenity: {}", id);
        AmenityResponse response = amenityService.updateAmenity(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete amenity")
    public ResponseEntity<Void> deleteAmenity(@PathVariable Long id) {
        log.info("REST request to delete amenity: {}", id);
        amenityService.deleteAmenity(id);
        return ResponseEntity.noContent().build();
    }
}
