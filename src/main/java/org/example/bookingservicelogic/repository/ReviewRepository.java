package org.example.bookingservicelogic.repository;

import org.example.bookingservicelogic.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Review entity operations.
 * Provides CRUD operations and custom queries for review management.
 *
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Finds all reviews for a hotel.
     *
     * @param hotelId the hotel ID
     * @param pageable pagination information
     * @return page of hotel reviews
     */
    Page<Review> findByHotelIdOrderByCreatedAtDesc(Long hotelId, Pageable pageable);

    /**
     * Finds all reviews by a user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of user's reviews
     */
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Finds a review by booking ID.
     *
     * @param bookingId the booking ID
     * @return Optional containing the review if found
     */
    Optional<Review> findByBookingId(Long bookingId);

    /**
     * Checks if a user has already reviewed a booking.
     *
     * @param userId the user ID
     * @param bookingId the booking ID
     * @return true if review exists
     */
    boolean existsByUserIdAndBookingId(Long userId, Long bookingId);

    /**
     * Checks if a user has already reviewed a hotel.
     *
     * @param userId the user ID
     * @param hotelId the hotel ID
     * @return true if user has reviewed this hotel
     */
    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);

    /**
     * Finds reviews by rating.
     *
     * @param rating the rating to filter by
     * @param pageable pagination information
     * @return page of reviews with specified rating
     */
    Page<Review> findByRating(Integer rating, Pageable pageable);

    /**
     * Finds reviews for a hotel with minimum rating.
     *
     * @param hotelId the hotel ID
     * @param minRating minimum rating
     * @param pageable pagination information
     * @return page of matching reviews
     */
    @Query("SELECT r FROM Review r WHERE r.hotel.id = :hotelId AND r.rating >= :minRating ORDER BY r.createdAt DESC")
    Page<Review> findByHotelIdAndMinRating(@Param("hotelId") Long hotelId,
                                            @Param("minRating") Integer minRating,
                                            Pageable pageable);

    /**
     * Calculates average rating for a hotel.
     *
     * @param hotelId the hotel ID
     * @return average rating
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.hotel.id = :hotelId")
    Double calculateAverageRatingByHotel(@Param("hotelId") Long hotelId);

    /**
     * Counts reviews by hotel.
     *
     * @param hotelId the hotel ID
     * @return number of reviews
     */
    Long countByHotelId(Long hotelId);

    /**
     * Gets rating distribution for a hotel.
     *
     * @param hotelId the hotel ID
     * @return list of rating counts (rating, count)
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.hotel.id = :hotelId GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getRatingDistributionByHotel(@Param("hotelId") Long hotelId);

    /**
     * Searches reviews by comment text.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching reviews
     */
    @Query("SELECT r FROM Review r WHERE LOWER(r.comment) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Review> searchReviews(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Finds the most recent reviews.
     *
     * @param pageable pagination information
     * @return page of recent reviews
     */
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
