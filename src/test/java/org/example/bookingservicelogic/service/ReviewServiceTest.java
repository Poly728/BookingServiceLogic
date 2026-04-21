package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.ReviewCreateRequest;
import org.example.bookingservicelogic.dto.request.ReviewUpdateRequest;
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
import org.example.bookingservicelogic.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private HotelRepository hotelRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private ReviewMapper reviewMapper;
    @Mock private HotelService hotelService;
    @InjectMocks private ReviewServiceImpl reviewService;

    private User user;
    private Hotel hotel;
    private Review review;
    private ReviewResponse response;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        hotel = Hotel.builder().id(2L).build();
        review = Review.builder().id(3L).user(user).hotel(hotel).build();
        response = ReviewResponse.builder().id(3L).userId(1L).hotelId(2L).build();
    }

    @Test
    void createReviewSuccessWithBooking() {
        ReviewCreateRequest request = ReviewCreateRequest.builder().userId(1L).hotelId(2L).bookingId(4L).build();
        Booking booking = Booking.builder().id(4L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(hotelRepository.findById(2L)).thenReturn(Optional.of(hotel));
        when(bookingRepository.hasCompletedBookingForHotel(1L, 2L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndBookingId(1L, 4L)).thenReturn(false);
        when(reviewMapper.toEntity(request)).thenReturn(review);
        when(bookingRepository.findById(4L)).thenReturn(Optional.of(booking));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(response);

        assertThat(reviewService.createReview(request).getId()).isEqualTo(3L);
        verify(hotelService).updateHotelRating(2L);
    }

    @Test
    void createReviewFailureCases() {
        ReviewCreateRequest request = ReviewCreateRequest.builder().userId(1L).hotelId(2L).bookingId(4L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(hotelRepository.findById(2L)).thenReturn(Optional.of(hotel));
        when(bookingRepository.hasCompletedBookingForHotel(1L, 2L)).thenReturn(false);
        assertThatThrownBy(() -> reviewService.createReview(request)).isInstanceOf(BadRequestException.class);

        when(bookingRepository.hasCompletedBookingForHotel(1L, 2L)).thenReturn(true);
        when(reviewRepository.existsByUserIdAndBookingId(1L, 4L)).thenReturn(true);
        assertThatThrownBy(() -> reviewService.createReview(request)).isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getAndPageMethods() {
        var pageable = PageRequest.of(0, 10);
        when(reviewRepository.findById(3L)).thenReturn(Optional.of(review));
        when(reviewRepository.findByBookingId(4L)).thenReturn(Optional.of(review));
        when(reviewMapper.toResponse(review)).thenReturn(response);
        when(reviewRepository.findByHotelIdOrderByCreatedAtDesc(2L, pageable)).thenReturn(new PageImpl<>(List.of(review), pageable, 1));
        when(reviewRepository.findByUserIdOrderByCreatedAtDesc(1L, pageable)).thenReturn(new PageImpl<>(List.of(review), pageable, 1));
        when(reviewRepository.findByHotelIdAndMinRating(2L, 4, pageable)).thenReturn(new PageImpl<>(List.of(review), pageable, 1));
        when(reviewRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(new PageImpl<>(List.of(review), pageable, 1));
        when(reviewRepository.searchReviews("ok", pageable)).thenReturn(new PageImpl<>(List.of(review), pageable, 1));
        when(reviewMapper.toResponseList(any())).thenReturn(List.of(response));
        when(reviewRepository.calculateAverageRatingByHotel(2L)).thenReturn(4.5);
        when(reviewRepository.getRatingDistributionByHotel(2L)).thenReturn(List.<Object[]>of(new Object[]{5, 2L}));
        when(reviewRepository.countByHotelId(2L)).thenReturn(9L);
        when(bookingRepository.hasCompletedBookingForHotel(1L, 2L)).thenReturn(true);

        assertThat(reviewService.getReviewById(3L).getId()).isEqualTo(3L);
        assertThat(reviewService.getReviewByBookingId(4L).getId()).isEqualTo(3L);
        assertThat(reviewService.getReviewsByHotel(2L, pageable).getContent()).hasSize(1);
        assertThat(reviewService.getReviewsByUser(1L, pageable).getContent()).hasSize(1);
        assertThat(reviewService.getReviewsByHotelAndMinRating(2L, 4, pageable).getContent()).hasSize(1);
        assertThat(reviewService.getRecentReviews(pageable).getContent()).hasSize(1);
        assertThat(reviewService.searchReviews("ok", pageable).getContent()).hasSize(1);
        assertThat(reviewService.calculateAverageRating(2L)).isEqualTo(4.5);
        Map<Integer, Long> distribution = reviewService.getRatingDistribution(2L);
        assertThat(distribution.get(5)).isEqualTo(2L);
        assertThat(reviewService.countReviewsByHotel(2L)).isEqualTo(9L);
        assertThat(reviewService.canUserReviewHotel(1L, 2L)).isTrue();
    }

    @Test
    void updateAndDeleteAndNotFound() {
        ReviewUpdateRequest updateRequest = ReviewUpdateRequest.builder().comment("updated").build();
        when(reviewRepository.findById(3L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toResponse(review)).thenReturn(response);

        assertThat(reviewService.updateReview(3L, updateRequest).getId()).isEqualTo(3L);
        reviewService.deleteReview(3L);
        verify(hotelService, times(2)).updateHotelRating(2L);

        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.getReviewById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
