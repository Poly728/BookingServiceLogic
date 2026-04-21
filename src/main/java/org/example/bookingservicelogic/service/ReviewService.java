package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.ReviewCreateRequest;
import org.example.bookingservicelogic.dto.request.ReviewUpdateRequest;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface for review management operations.
 *
 */
public interface ReviewService {

    /**
     * Creates a new review.
     *
     * @param request the review creation request
     * @return the created review response
     */
    ReviewResponse createReview(ReviewCreateRequest request);

    /**
     * Retrieves a review by ID.
     *
     * @param id the review ID
     * @return the review response
     */
    ReviewResponse getReviewById(Long id);

    /**
     * Retrieves a review by booking ID.
     *
     * @param bookingId the booking ID
     * @return the review response
     */
    ReviewResponse getReviewByBookingId(Long bookingId);

    /**
     * Retrieves all reviews for a hotel.
     *
     * @param hotelId the hotel ID
     * @param pageable pagination information
     * @return page of review responses
     */
    PageResponse<ReviewResponse> getReviewsByHotel(Long hotelId, Pageable pageable);

    /**
     * Retrieves all reviews by a user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of review responses
     */
    PageResponse<ReviewResponse> getReviewsByUser(Long userId, Pageable pageable);

    /**
     * Retrieves reviews for a hotel with minimum rating.
     *
     * @param hotelId the hotel ID
     * @param minRating minimum rating
     * @param pageable pagination information
     * @return page of review responses
     */
    PageResponse<ReviewResponse> getReviewsByHotelAndMinRating(Long hotelId, Integer minRating, Pageable pageable);

    /**
     * Retrieves recent reviews.
     *
     * @param pageable pagination information
     * @return page of recent review responses
     */
    PageResponse<ReviewResponse> getRecentReviews(Pageable pageable);

    /**
     * Searches reviews by text.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching review responses
     */
    PageResponse<ReviewResponse> searchReviews(String searchTerm, Pageable pageable);

    /**
     * Calculates average rating for a hotel.
     *
     * @param hotelId the hotel ID
     * @return average rating
     */
    Double calculateAverageRating(Long hotelId);

    /**
     * Gets rating distribution for a hotel.
     *
     * @param hotelId the hotel ID
     * @return map of rating to count
     */
    Map<Integer, Long> getRatingDistribution(Long hotelId);

    /**
     * Counts reviews for a hotel.
     *
     * @param hotelId the hotel ID
     * @return number of reviews
     */
    Long countReviewsByHotel(Long hotelId);

    /**
     * Checks if user can review a hotel (has completed booking).
     *
     * @param userId the user ID
     * @param hotelId the hotel ID
     * @return true if user can review
     */
    boolean canUserReviewHotel(Long userId, Long hotelId);

    /**
     * Updates an existing review.
     *
     * @param id the review ID
     * @param request the update request
     * @return the updated review response
     */
    ReviewResponse updateReview(Long id, ReviewUpdateRequest request);

    /**
     * Deletes a review.
     *
     * @param id the review ID
     */
    void deleteReview(Long id);
}
