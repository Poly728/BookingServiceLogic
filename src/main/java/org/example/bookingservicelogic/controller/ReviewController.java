package org.example.bookingservicelogic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.ReviewCreateRequest;
import org.example.bookingservicelogic.dto.request.ReviewUpdateRequest;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.dto.response.ReviewResponse;
import org.example.bookingservicelogic.service.ReviewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for review management operations.
 *
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reviews", description = "Review management API")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create a new review")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewCreateRequest request) {
        log.info("REST request to create review for hotel {} by user {}", request.getHotelId(), request.getUserId());
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        log.info("REST request to get review by ID: {}", id);
        ReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get review by booking ID")
    public ResponseEntity<ReviewResponse> getReviewByBookingId(@PathVariable Long bookingId) {
        log.info("REST request to get review by booking ID: {}", bookingId);
        ReviewResponse response = reviewService.getReviewByBookingId(bookingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get reviews by hotel")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviewsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("REST request to get reviews for hotel: {}", hotelId);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<ReviewResponse> response = reviewService.getReviewsByHotel(hotelId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews by user")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get reviews for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ReviewResponse> response = reviewService.getReviewsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/rating/{minRating}")
    @Operation(summary = "Get hotel reviews with minimum rating")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviewsByHotelAndMinRating(
            @PathVariable Long hotelId,
            @PathVariable Integer minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get reviews for hotel {} with min rating {}", hotelId, minRating);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ReviewResponse> response = reviewService.getReviewsByHotelAndMinRating(hotelId, minRating, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getRecentReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get recent reviews");
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ReviewResponse> response = reviewService.getRecentReviews(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> searchReviews(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to search reviews with query: {}", query);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ReviewResponse> response = reviewService.searchReviews(query, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hotel/{hotelId}/average-rating")
    @Operation(summary = "Get average rating for hotel")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long hotelId) {
        log.info("REST request to get average rating for hotel: {}", hotelId);
        Double avgRating = reviewService.calculateAverageRating(hotelId);
        return ResponseEntity.ok(avgRating != null ? avgRating : 0.0);
    }

    @GetMapping("/hotel/{hotelId}/rating-distribution")
    @Operation(summary = "Get rating distribution for hotel")
    public ResponseEntity<Map<Integer, Long>> getRatingDistribution(@PathVariable Long hotelId) {
        log.info("REST request to get rating distribution for hotel: {}", hotelId);
        Map<Integer, Long> distribution = reviewService.getRatingDistribution(hotelId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/hotel/{hotelId}/count")
    @Operation(summary = "Count reviews for hotel")
    public ResponseEntity<Long> countReviewsByHotel(@PathVariable Long hotelId) {
        log.info("REST request to count reviews for hotel: {}", hotelId);
        Long count = reviewService.countReviewsByHotel(hotelId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/can-review")
    @Operation(summary = "Check if user can review hotel")
    public ResponseEntity<Boolean> canUserReviewHotel(
            @RequestParam Long userId,
            @RequestParam Long hotelId) {
        log.info("REST request to check if user {} can review hotel {}", userId, hotelId);
        boolean canReview = reviewService.canUserReviewHotel(userId, hotelId);
        return ResponseEntity.ok(canReview);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update review")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateRequest request) {
        log.info("REST request to update review: {}", id);
        ReviewResponse response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        log.info("REST request to delete review: {}", id);
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
