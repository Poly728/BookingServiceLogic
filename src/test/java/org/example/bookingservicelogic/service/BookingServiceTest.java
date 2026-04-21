package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.BookingCreateRequest;
import org.example.bookingservicelogic.dto.request.BookingUpdateRequest;
import org.example.bookingservicelogic.dto.response.BookingResponse;
import org.example.bookingservicelogic.entity.Booking;
import org.example.bookingservicelogic.entity.Hotel;
import org.example.bookingservicelogic.entity.Room;
import org.example.bookingservicelogic.entity.User;
import org.example.bookingservicelogic.entity.enums.BookingStatus;
import org.example.bookingservicelogic.entity.enums.Role;
import org.example.bookingservicelogic.entity.enums.RoomType;
import org.example.bookingservicelogic.exception.BadRequestException;
import org.example.bookingservicelogic.exception.BookingConflictException;
import org.example.bookingservicelogic.exception.ResourceNotFoundException;
import org.example.bookingservicelogic.mapper.BookingMapper;
import org.example.bookingservicelogic.repository.BookingRepository;
import org.example.bookingservicelogic.repository.RoomRepository;
import org.example.bookingservicelogic.repository.UserRepository;
import org.example.bookingservicelogic.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingService.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User testUser;
    private Hotel testHotel;
    private Room testRoom;
    private Booking testBooking;
    private BookingResponse testBookingResponse;
    private BookingCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .build();

        testHotel = Hotel.builder()
                .id(1L)
                .name("Test Hotel")
                .city("Test City")
                .country("Test Country")
                .build();

        testRoom = Room.builder()
                .id(1L)
                .hotel(testHotel)
                .roomNumber("101")
                .roomType(RoomType.STANDARD)
                .pricePerNight(BigDecimal.valueOf(100))
                .capacity(2)
                .available(true)
                .build();

        testBooking = Booking.builder()
                .id(1L)
                .user(testUser)
                .room(testRoom)
                .checkInDate(LocalDate.now().plusDays(7))
                .checkOutDate(LocalDate.now().plusDays(10))
                .guestsCount(2)
                .totalPrice(BigDecimal.valueOf(300))
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testBookingResponse = BookingResponse.builder()
                .id(1L)
                .userId(1L)
                .roomId(1L)
                .hotelId(1L)
                .hotelName("Test Hotel")
                .checkInDate(LocalDate.now().plusDays(7))
                .checkOutDate(LocalDate.now().plusDays(10))
                .guestsCount(2)
                .totalPrice(BigDecimal.valueOf(300))
                .status(BookingStatus.PENDING)
                .build();

        createRequest = BookingCreateRequest.builder()
                .userId(1L)
                .roomId(1L)
                .checkInDate(LocalDate.now().plusDays(7))
                .checkOutDate(LocalDate.now().plusDays(10))
                .guestsCount(2)
                .build();
    }

    @Nested
    @DisplayName("Create Booking Tests")
    class CreateBookingTests {

        @Test
        @DisplayName("Should create booking successfully")
        void shouldCreateBookingSuccessfully() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
            when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(Collections.emptyList());
            when(bookingMapper.toEntity(any(BookingCreateRequest.class))).thenReturn(testBooking);
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            when(bookingMapper.toResponse(any(Booking.class))).thenReturn(testBookingResponse);

            BookingResponse result = bookingService.createBooking(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getStatus()).isEqualTo(BookingStatus.PENDING);
            verify(bookingRepository).save(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.createBooking(createRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when room not found")
        void shouldThrowExceptionWhenRoomNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(roomRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.createBooking(createRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when room not available")
        void shouldThrowExceptionWhenRoomNotAvailable() {
            testRoom.setAvailable(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));

            assertThatThrownBy(() -> bookingService.createBooking(createRequest))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("not available");
        }

        @Test
        @DisplayName("Should throw exception when guests exceed capacity")
        void shouldThrowExceptionWhenGuestsExceedCapacity() {
            createRequest.setGuestsCount(5);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));

            assertThatThrownBy(() -> bookingService.createBooking(createRequest))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("capacity");
        }

        @Test
        @DisplayName("Should throw exception when booking conflicts exist")
        void shouldThrowExceptionWhenBookingConflictsExist() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
            when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(List.of(testBooking));

            assertThatThrownBy(() -> bookingService.createBooking(createRequest))
                    .isInstanceOf(BookingConflictException.class);
        }

        @Test
        @DisplayName("Should throw exception when check-in date is in the past")
        void shouldThrowWhenCheckInDateInPast() {
            createRequest.setCheckInDate(LocalDate.now().minusDays(1));

            assertThatThrownBy(() -> bookingService.createBooking(createRequest))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("in the past");
        }

        @Test
        @DisplayName("Should throw exception when check-in and check-out are equal")
        void shouldThrowWhenDatesAreEqual() {
            createRequest.setCheckOutDate(createRequest.getCheckInDate());

            assertThatThrownBy(() -> bookingService.createBooking(createRequest))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("after check-in");
        }

        @Test
        @DisplayName("Should throw exception when booking dates are null")
        void shouldThrowWhenDatesAreNull() {
            createRequest.setCheckInDate(null);

            assertThatThrownBy(() -> bookingService.createBooking(createRequest))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("required");
        }
    }

    @Nested
    @DisplayName("Booking Status Tests")
    class BookingStatusTests {

        @Test
        @DisplayName("Should confirm booking")
        void shouldConfirmBooking() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            when(bookingMapper.toResponse(any(Booking.class))).thenReturn(testBookingResponse);

            BookingResponse result = bookingService.confirmBooking(1L);

            assertThat(result).isNotNull();
            verify(bookingRepository).save(any(Booking.class));
            assertThat(testBooking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        }

        @Test
        @DisplayName("Should cancel booking")
        void shouldCancelBooking() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            when(bookingMapper.toResponse(any(Booking.class))).thenReturn(testBookingResponse);

            BookingResponse result = bookingService.cancelBooking(1L);

            assertThat(result).isNotNull();
            verify(bookingRepository).save(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when cancelling completed booking")
        void shouldThrowExceptionWhenCancellingCompletedBooking() {
            testBooking.setStatus(BookingStatus.COMPLETED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            assertThatThrownBy(() -> bookingService.cancelBooking(1L))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("Should check in guest")
        void shouldCheckInGuest() {
            testBooking.setStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            when(bookingMapper.toResponse(any(Booking.class))).thenReturn(testBookingResponse);

            BookingResponse result = bookingService.checkIn(1L);

            assertThat(result).isNotNull();
            verify(bookingRepository).save(any(Booking.class));
        }

        @Test
        @DisplayName("Should check out guest")
        void shouldCheckOutGuest() {
            testBooking.setStatus(BookingStatus.CHECKED_IN);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            when(bookingMapper.toResponse(any(Booking.class))).thenReturn(testBookingResponse);

            BookingResponse result = bookingService.checkOut(1L);

            assertThat(result).isNotNull();
            verify(bookingRepository).save(any(Booking.class));
        }

        @Test
        @DisplayName("Should throw exception when confirming non-pending booking")
        void shouldThrowWhenConfirmingNonPendingBooking() {
            testBooking.setStatus(BookingStatus.CANCELLED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            assertThatThrownBy(() -> bookingService.confirmBooking(1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Cannot transition");
        }

        @Test
        @DisplayName("Should throw exception when checking in non-confirmed booking")
        void shouldThrowWhenCheckingInWrongStatus() {
            testBooking.setStatus(BookingStatus.PENDING);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            assertThatThrownBy(() -> bookingService.checkIn(1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("confirmed");
        }

        @Test
        @DisplayName("Should throw exception when checking out non-checked-in booking")
        void shouldThrowWhenCheckingOutWrongStatus() {
            testBooking.setStatus(BookingStatus.CONFIRMED);
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

            assertThatThrownBy(() -> bookingService.checkOut(1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("checked-in");
        }
    }

    @Nested
    @DisplayName("Get Booking Tests")
    class GetBookingTests {

        @Test
        @DisplayName("Should get booking by ID")
        void shouldGetBookingById() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingMapper.toResponse(testBooking)).thenReturn(testBookingResponse);

            BookingResponse result = bookingService.getBookingById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when booking not found")
        void shouldThrowExceptionWhenBookingNotFound() {
            when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookingService.getBookingById(1L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should get bookings pages and aggregate data")
        void shouldGetBookingPagesAndAggregates() {
            var pageable = PageRequest.of(0, 10);
            var bookingPage = new org.springframework.data.domain.PageImpl<>(List.of(testBooking), pageable, 1);

            when(bookingRepository.findAll(pageable)).thenReturn(bookingPage);
            when(bookingRepository.findByUserIdOrderByCreatedAtDesc(1L, pageable)).thenReturn(bookingPage);
            when(bookingRepository.findByRoomIdOrderByCheckInDateDesc(1L, pageable)).thenReturn(bookingPage);
            when(bookingRepository.findByHotelId(1L, pageable)).thenReturn(bookingPage);
            when(bookingRepository.findByStatus(BookingStatus.PENDING, pageable)).thenReturn(bookingPage);
            when(bookingRepository.findByUserIdAndStatus(1L, BookingStatus.PENDING, pageable)).thenReturn(bookingPage);
            when(bookingRepository.findUpcomingBookings(eq(1L), any(LocalDate.class), eq(pageable))).thenReturn(bookingPage);
            when(bookingRepository.findPastBookings(eq(1L), any(LocalDate.class), eq(pageable))).thenReturn(bookingPage);
            when(bookingRepository.findByCheckInDateAndStatus(any(LocalDate.class), eq(BookingStatus.CONFIRMED))).thenReturn(List.of(testBooking));
            when(bookingRepository.findByCheckOutDateAndStatus(any(LocalDate.class), eq(BookingStatus.CHECKED_IN))).thenReturn(List.of(testBooking));
            when(bookingMapper.toResponseList(anyList())).thenReturn(List.of(testBookingResponse));
            when(bookingRepository.calculateTotalRevenueByHotel(1L)).thenReturn(BigDecimal.valueOf(1500));
            when(bookingRepository.countByHotelIdAndStatus(1L, BookingStatus.CONFIRMED)).thenReturn(4L);

            assertThat(bookingService.getAllBookings(pageable).getContent()).hasSize(1);
            assertThat(bookingService.getBookingsByUser(1L, pageable).getContent()).hasSize(1);
            assertThat(bookingService.getBookingsByRoom(1L, pageable).getContent()).hasSize(1);
            assertThat(bookingService.getBookingsByHotel(1L, pageable).getContent()).hasSize(1);
            assertThat(bookingService.getBookingsByStatus(BookingStatus.PENDING, pageable).getContent()).hasSize(1);
            assertThat(bookingService.getBookingsByUserAndStatus(1L, BookingStatus.PENDING, pageable).getContent()).hasSize(1);
            assertThat(bookingService.getUpcomingBookings(1L, pageable).getContent()).hasSize(1);
            assertThat(bookingService.getPastBookings(1L, pageable).getContent()).hasSize(1);
            assertThat(bookingService.getTodayCheckIns()).hasSize(1);
            assertThat(bookingService.getTodayCheckOuts()).hasSize(1);
            assertThat(bookingService.calculateHotelRevenue(1L)).isEqualByComparingTo("1500");
            assertThat(bookingService.countBookingsByStatus(1L, BookingStatus.CONFIRMED)).isEqualTo(4L);
        }

        @Test
        @DisplayName("Should update booking and delete booking")
        void shouldUpdateAndDeleteBooking() {
            BookingUpdateRequest request = BookingUpdateRequest.builder()
                    .checkInDate(LocalDate.now().plusDays(8))
                    .checkOutDate(LocalDate.now().plusDays(11))
                    .build();

            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(Collections.emptyList());
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            when(bookingMapper.toResponse(any(Booking.class))).thenReturn(testBookingResponse);

            BookingResponse updated = bookingService.updateBooking(1L, request);
            assertThat(updated).isNotNull();
            verify(bookingRepository).save(any(Booking.class));

            bookingService.deleteBooking(1L);
            verify(bookingRepository).delete(testBooking);
        }

        @Test
        @DisplayName("Should throw conflict on update when overlapping booking exists")
        void shouldThrowConflictOnUpdate() {
            Booking anotherBooking = Booking.builder().id(999L).build();
            BookingUpdateRequest request = BookingUpdateRequest.builder()
                    .checkInDate(LocalDate.now().plusDays(8))
                    .checkOutDate(LocalDate.now().plusDays(11))
                    .build();
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(new java.util.ArrayList<>(List.of(anotherBooking)));

            assertThatThrownBy(() -> bookingService.updateBooking(1L, request))
                    .isInstanceOf(BookingConflictException.class);
        }

        @Test
        @DisplayName("Should allow update when conflict is current booking itself")
        void shouldAllowUpdateWhenOnlySelfConflictExists() {
            BookingUpdateRequest request = BookingUpdateRequest.builder()
                    .checkInDate(LocalDate.now().plusDays(8))
                    .checkOutDate(LocalDate.now().plusDays(11))
                    .build();
            when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
            when(bookingRepository.findOverlappingBookings(any(), any(), any())).thenReturn(new java.util.ArrayList<>(List.of(testBooking)));
            when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
            when(bookingMapper.toResponse(any(Booking.class))).thenReturn(testBookingResponse);

            BookingResponse updated = bookingService.updateBooking(1L, request);
            assertThat(updated).isNotNull();
        }
    }
}
