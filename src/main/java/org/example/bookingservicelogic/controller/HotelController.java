package org.example.bookingservicelogic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.HotelCreateRequest;
import org.example.bookingservicelogic.dto.request.HotelUpdateRequest;
import org.example.bookingservicelogic.dto.response.HotelResponse;
import org.example.bookingservicelogic.dto.response.HotelSummaryResponse;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.service.HotelService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * REST controller for hotel management operations.
 *
 */
@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Hotels", description = "Hotel management API")
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    @Operation(summary = "Create a new hotel")
    public ResponseEntity<HotelResponse> createHotel(@Valid @RequestBody HotelCreateRequest request) {
        log.info("REST request to create hotel: {}", request.getName());
        HotelResponse response = hotelService.createHotel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by ID")
    public ResponseEntity<HotelResponse> getHotelById(@PathVariable Long id) {
        log.info("REST request to get hotel by ID: {}", id);
        HotelResponse response = hotelService.getHotelById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all hotels with pagination")
    public ResponseEntity<PageResponse<HotelSummaryResponse>> getAllHotels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("REST request to get all hotels, page: {}, size: {}", page, size);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<HotelSummaryResponse> response = hotelService.getAllHotels(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search hotels")
    public ResponseEntity<PageResponse<HotelSummaryResponse>> searchHotels(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to search hotels with query: {}", query);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<HotelSummaryResponse> response = hotelService.searchHotels(query, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get hotels by city")
    public ResponseEntity<PageResponse<HotelSummaryResponse>> getHotelsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get hotels by city: {}", city);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<HotelSummaryResponse> response = hotelService.getHotelsByCity(city, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/country/{country}")
    @Operation(summary = "Get hotels by country")
    public ResponseEntity<PageResponse<HotelSummaryResponse>> getHotelsByCountry(
            @PathVariable String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get hotels by country: {}", country);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<HotelSummaryResponse> response = hotelService.getHotelsByCountry(country, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get hotels by owner")
    public ResponseEntity<PageResponse<HotelSummaryResponse>> getHotelsByOwner(
            @PathVariable Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get hotels by owner: {}", ownerId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<HotelSummaryResponse> response = hotelService.getHotelsByOwner(ownerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rating")
    @Operation(summary = "Get hotels by minimum rating")
    public ResponseEntity<PageResponse<HotelSummaryResponse>> getHotelsByMinRating(
            @RequestParam BigDecimal minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get hotels with min rating: {}", minRating);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<HotelSummaryResponse> response = hotelService.getHotelsByMinRating(minRating, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stars/{starRating}")
    @Operation(summary = "Get hotels by star rating")
    public ResponseEntity<PageResponse<HotelSummaryResponse>> getHotelsByStarRating(
            @PathVariable Integer starRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get hotels by star rating: {}", starRating);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<HotelSummaryResponse> response = hotelService.getHotelsByStarRating(starRating, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/amenity/{amenityId}")
    @Operation(summary = "Get hotels by amenity")
    public ResponseEntity<PageResponse<HotelSummaryResponse>> getHotelsByAmenity(
            @PathVariable Long amenityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get hotels by amenity ID: {}", amenityId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<HotelSummaryResponse> response = hotelService.getHotelsByAmenity(amenityId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-rated")
    @Operation(summary = "Get top rated hotels")
    public ResponseEntity<PageResponse<HotelSummaryResponse>> getTopRatedHotels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get top rated hotels");
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<HotelSummaryResponse> response = hotelService.getTopRatedHotels(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cities")
    @Operation(summary = "Get all available cities")
    public ResponseEntity<List<String>> getAvailableCities() {
        log.info("REST request to get available cities");
        List<String> cities = hotelService.getAvailableCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/countries")
    @Operation(summary = "Get all available countries")
    public ResponseEntity<List<String>> getAvailableCountries() {
        log.info("REST request to get available countries");
        List<String> countries = hotelService.getAvailableCountries();
        return ResponseEntity.ok(countries);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update hotel")
    public ResponseEntity<HotelResponse> updateHotel(
            @PathVariable Long id,
            @Valid @RequestBody HotelUpdateRequest request) {
        log.info("REST request to update hotel: {}", id);
        HotelResponse response = hotelService.updateHotel(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/amenities")
    @Operation(summary = "Add amenities to hotel")
    public ResponseEntity<HotelResponse> addAmenitiesToHotel(
            @PathVariable Long id,
            @RequestBody Set<Long> amenityIds) {
        log.info("REST request to add amenities to hotel: {}", id);
        HotelResponse response = hotelService.addAmenitiesToHotel(id, amenityIds);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/amenities")
    @Operation(summary = "Remove amenities from hotel")
    public ResponseEntity<HotelResponse> removeAmenitiesFromHotel(
            @PathVariable Long id,
            @RequestBody Set<Long> amenityIds) {
        log.info("REST request to remove amenities from hotel: {}", id);
        HotelResponse response = hotelService.removeAmenitiesFromHotel(id, amenityIds);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete hotel")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        log.info("REST request to delete hotel: {}", id);
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}
