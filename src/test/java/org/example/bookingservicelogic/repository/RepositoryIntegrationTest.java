package org.example.bookingservicelogic.repository;

import org.example.bookingservicelogic.entity.*;
import org.example.bookingservicelogic.entity.enums.BookingStatus;
import org.example.bookingservicelogic.entity.enums.Role;
import org.example.bookingservicelogic.entity.enums.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for repositories using in-memory H2 database.
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class RepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    private User testUser;
    private Hotel testHotel;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        reviewRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();
        amenityRepository.deleteAll();

        testUser = userRepository.save(User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .build());

        testHotel = hotelRepository.save(Hotel.builder()
                .name("Test Hotel")
                .description("A test hotel")
                .address("123 Test St")
                .city("Test City")
                .country("Test Country")
                .rating(BigDecimal.valueOf(4.5))
                .starRating(4)
                .active(true)
                .build());

        testRoom = roomRepository.save(Room.builder()
                .hotel(testHotel)
                .roomNumber("101")
                .roomType(RoomType.STANDARD)
                .pricePerNight(BigDecimal.valueOf(100))
                .capacity(2)
                .bedCount(1)
                .available(true)
                .build());
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        var result = userRepository.findByUsername("testuser");
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        var result = userRepository.findByEmail("test@example.com");
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should search users")
    void shouldSearchUsers() {
        Page<User> result = userRepository.searchUsers("test", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should find active hotels")
    void shouldFindActiveHotels() {
        Page<Hotel> result = hotelRepository.findByActiveTrue(PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Hotel");
    }

    @Test
    @DisplayName("Should search hotels")
    void shouldSearchHotels() {
        Page<Hotel> result = hotelRepository.searchHotels("Test", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should find hotels by city")
    void shouldFindHotelsByCity() {
        Page<Hotel> result = hotelRepository.findByCityIgnoreCaseAndActiveTrue("Test City", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should find rooms by hotel")
    void shouldFindRoomsByHotel() {
        Page<Room> result = roomRepository.findByHotelId(testHotel.getId(), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRoomNumber()).isEqualTo("101");
    }

    @Test
    @DisplayName("Should find available rooms for dates")
    void shouldFindAvailableRoomsForDates() {
        List<Room> result = roomRepository.findAvailableRoomsForDates(
                testHotel.getId(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should check room availability")
    void shouldCheckRoomAvailability() {
        boolean available = roomRepository.isRoomAvailableForDates(
                testRoom.getId(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));
        assertThat(available).isTrue();
    }

    @Test
    @DisplayName("Should find bookings by user")
    void shouldFindBookingsByUser() {
        Booking booking = bookingRepository.save(Booking.builder()
                .user(testUser)
                .room(testRoom)
                .checkInDate(LocalDate.now().plusDays(7))
                .checkOutDate(LocalDate.now().plusDays(10))
                .guestsCount(2)
                .totalPrice(BigDecimal.valueOf(300))
                .status(BookingStatus.CONFIRMED)
                .build());

        Page<Booking> result = bookingRepository.findByUserIdOrderByCreatedAtDesc(
                testUser.getId(), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should find overlapping bookings")
    void shouldFindOverlappingBookings() {
        bookingRepository.save(Booking.builder()
                .user(testUser)
                .room(testRoom)
                .checkInDate(LocalDate.now().plusDays(5))
                .checkOutDate(LocalDate.now().plusDays(10))
                .guestsCount(2)
                .totalPrice(BigDecimal.valueOf(500))
                .status(BookingStatus.CONFIRMED)
                .build());

        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                testRoom.getId(),
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(12));
        assertThat(overlapping).hasSize(1);
    }

    @Test
    @DisplayName("Should calculate hotel revenue")
    void shouldCalculateHotelRevenue() {
        bookingRepository.save(Booking.builder()
                .user(testUser)
                .room(testRoom)
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .guestsCount(2)
                .totalPrice(BigDecimal.valueOf(200))
                .status(BookingStatus.CONFIRMED)
                .build());

        BigDecimal revenue = bookingRepository.calculateTotalRevenueByHotel(testHotel.getId());
        assertThat(revenue).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
    @DisplayName("Should create and find amenity")
    void shouldCreateAndFindAmenity() {
        Amenity amenity = amenityRepository.save(Amenity.builder()
                .name("WiFi")
                .description("Free WiFi")
                .icon("wifi")
                .build());

        var result = amenityRepository.findByNameIgnoreCase("wifi");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("WiFi");
    }
}
