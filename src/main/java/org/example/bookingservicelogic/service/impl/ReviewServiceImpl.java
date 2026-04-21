package org.example.bookingservicelogic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.ReviewCreateRequest;
import org.example.bookingservicelogic.dto.request.ReviewUpdateRequest;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.dto.response.ReviewResponse;
import org.example.bookingservicelogic.entity.Booking;
import org.example.bookingservicelogic.entity.Hotel;
import org.example.bookingservicelogic.entity.Review;
import org.example.bookingservicelogic.entity.User;
import org.example.bookingservicelogic.exception.BadRequestException;
import org.example.bookingservicelogic.exception.DuplicateResourceException;
import org.example.bookingservicelogic.exception.ResourceNotFoundException;
import org.example.bookingservicelogic.mapper.ReviewMapper;
import org.example.bookingservicelogic.repository.BookingRepository;
import org.example.bookingservicelogic.repository.HotelRepository;
import org.example.bookingservicelogic.repository.ReviewRepository;
import org.example.bookingservicelogic.repository.UserRepository;
import org.example.bookingservicelogic.service.HotelService;
import org.example.bookingservicelogic.service.ReviewService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ReviewService interface.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;
    @Lazy
    private final HotelService hotelService;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request) {
        log.debug("Creating review for hotel {} by user {}", request.getHotelId(), request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", request.getHotelId()));

        // Check if user has a completed booking for this hotel
        if (!bookingRepository.hasCompletedBookingForHotel(request.getUserId(), request.getHotelId())) {
            throw new BadRequestException("You can only review hotels where you have completed a stay");
        }

        // Check for duplicate review
        if (request.getBookingId() != null) {
            if (reviewRepository.existsByUserIdAndBookingId(request.getUserId(), request.getBookingId())) {
                throw new DuplicateResourceException("Review", "bookingId", request.getBookingId());
            }
        }

        Review review = reviewMapper.toEntity(request);
        review.setUser(user);
        review.setHotel(hotel);

        if (request.getBookingId() != null) {
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.getBookingId()));
            review.setBooking(booking);
        }

        Review savedReview = reviewRepository.save(review);
        log.info("Created review with ID: {}", savedReview.getId());

        // Update hotel rating
        hotelService.updateHotelRating(request.getHotelId());

        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public ReviewResponse getReviewById(Long id) {
        log.debug("Fetching review by ID: {}", id);
        Review review = findReviewById(id);
        return reviewMapper.toResponse(review);
    }

    @Override
    public ReviewResponse getReviewByBookingId(Long bookingId) {
        log.debug("Fetching review by booking ID: {}", bookingId);
        Review review = reviewRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "bookingId", bookingId));
        return reviewMapper.toResponse(review);
    }

    @Override
    public PageResponse<ReviewResponse> getReviewsByHotel(Long hotelId, Pageable pageable) {
        log.debug("Fetching reviews for hotel ID: {}", hotelId);
        Page<Review> reviews = reviewRepository.findByHotelIdOrderByCreatedAtDesc(hotelId, pageable);
        List<ReviewResponse> content = reviewMapper.toResponseList(reviews.getContent());
        return PageResponse.of(reviews, content);
    }

    @Override
    public PageResponse<ReviewResponse> getReviewsByUser(Long userId, Pageable pageable) {
        log.debug("Fetching reviews for user ID: {}", userId);
        Page<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<ReviewResponse> content = reviewMapper.toResponseList(reviews.getContent());
        return PageResponse.of(reviews, content);
    }

    @Override
    public PageResponse<ReviewResponse> getReviewsByHotelAndMinRating(Long hotelId, Integer minRating, Pageable pageable) {
        log.debug("Fetching reviews for hotel {} with min rating {}", hotelId, minRating);
        Page<Review> reviews = reviewRepository.findByHotelIdAndMinRating(hotelId, minRating, pageable);
        List<ReviewResponse> content = reviewMapper.toResponseList(reviews.getContent());
        return PageResponse.of(reviews, content);
    }

    @Override
    public PageResponse<ReviewResponse> getRecentReviews(Pageable pageable) {
        log.debug("Fetching recent reviews");
        Page<Review> reviews = reviewRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<ReviewResponse> content = reviewMapper.toResponseList(reviews.getContent());
        return PageResponse.of(reviews, content);
    }

    @Override
    public PageResponse<ReviewResponse> searchReviews(String searchTerm, Pageable pageable) {
        log.debug("Searching reviews with term: {}", searchTerm);
        Page<Review> reviews = reviewRepository.searchReviews(searchTerm, pageable);
        List<ReviewResponse> content = reviewMapper.toResponseList(reviews.getContent());
        return PageResponse.of(reviews, content);
    }

    @Override
    public Double calculateAverageRating(Long hotelId) {
        log.debug("Calculating average rating for hotel ID: {}", hotelId);
        return reviewRepository.calculateAverageRatingByHotel(hotelId);
    }

    @Override
    public Map<Integer, Long> getRatingDistribution(Long hotelId) {
        log.debug("Getting rating distribution for hotel ID: {}", hotelId);
        List<Object[]> distribution = reviewRepository.getRatingDistributionByHotel(hotelId);
        Map<Integer, Long> result = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            result.put(i, 0L);
        }
        for (Object[] row : distribution) {
            result.put((Integer) row[0], (Long) row[1]);
        }
        return result;
    }

    @Override
    public Long countReviewsByHotel(Long hotelId) {
        log.debug("Counting reviews for hotel ID: {}", hotelId);
        return reviewRepository.countByHotelId(hotelId);
    }

    @Override
    public boolean canUserReviewHotel(Long userId, Long hotelId) {
        log.debug("Checking if user {} can review hotel {}", userId, hotelId);
        return bookingRepository.hasCompletedBookingForHotel(userId, hotelId);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, ReviewUpdateRequest request) {
        log.debug("Updating review with ID: {}", id);
        Review review = findReviewById(id);

        reviewMapper.updateEntityFromRequest(request, review);

        Review updatedReview = reviewRepository.save(review);
        log.info("Updated review with ID: {}", id);

        // Update hotel rating
        hotelService.updateHotelRating(review.getHotel().getId());

        return reviewMapper.toResponse(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        log.debug("Deleting review with ID: {}", id);
        Review review = findReviewById(id);
        Long hotelId = review.getHotel().getId();

        reviewRepository.delete(review);
        log.info("Deleted review with ID: {}", id);

        // Update hotel rating
        hotelService.updateHotelRating(hotelId);
    }

    private Review findReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
    }
}
