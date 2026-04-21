package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.HotelCreateRequest;
import org.example.bookingservicelogic.dto.request.HotelUpdateRequest;
import org.example.bookingservicelogic.dto.response.HotelResponse;
import org.example.bookingservicelogic.dto.response.HotelSummaryResponse;
import org.example.bookingservicelogic.entity.Amenity;
import org.example.bookingservicelogic.entity.Hotel;
import org.example.bookingservicelogic.entity.User;
import org.example.bookingservicelogic.exception.ResourceNotFoundException;
import org.example.bookingservicelogic.mapper.HotelMapper;
import org.example.bookingservicelogic.repository.AmenityRepository;
import org.example.bookingservicelogic.repository.HotelRepository;
import org.example.bookingservicelogic.repository.ReviewRepository;
import org.example.bookingservicelogic.repository.RoomRepository;
import org.example.bookingservicelogic.repository.UserRepository;
import org.example.bookingservicelogic.service.impl.HotelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock private HotelRepository hotelRepository;
    @Mock private AmenityRepository amenityRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private UserRepository userRepository;
    @Mock private HotelMapper hotelMapper;
    @InjectMocks private HotelServiceImpl hotelService;

    private Hotel hotel;
    private HotelResponse response;

    @BeforeEach
    void setUp() {
        hotel = Hotel.builder().id(1L).name("Test").amenities(new java.util.HashSet<>()).active(true).build();
        response = HotelResponse.builder().id(1L).name("Test").build();
    }

    @Test
    void createAndGetHotel() {
        HotelCreateRequest request = HotelCreateRequest.builder().name("Test").ownerId(7L).amenityIds(Set.of(1L)).build();
        User owner = User.builder().id(7L).build();
        Amenity amenity = Amenity.builder().id(1L).build();
        when(hotelMapper.toEntity(request)).thenReturn(hotel);
        when(userRepository.findById(7L)).thenReturn(Optional.of(owner));
        when(amenityRepository.findByIdIn(Set.of(1L))).thenReturn(Set.of(amenity));
        when(hotelRepository.save(hotel)).thenReturn(hotel);
        when(hotelMapper.toResponse(hotel)).thenReturn(response);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomRepository.findMinPriceByHotel(1L)).thenReturn(BigDecimal.TEN);
        when(roomRepository.findMaxPriceByHotel(1L)).thenReturn(BigDecimal.valueOf(99));

        assertThat(hotelService.createHotel(request).getId()).isEqualTo(1L);
        assertThat(hotelService.getHotelById(1L).getMinPrice()).isEqualByComparingTo("10");
    }

    @Test
    void getHotelsPagesAndDictionaries() {
        var pageable = PageRequest.of(0, 10);
        when(hotelRepository.findByActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(hotel), pageable, 1));
        when(hotelRepository.searchHotels("t", pageable)).thenReturn(new PageImpl<>(List.of(hotel), pageable, 1));
        when(hotelRepository.findByCityIgnoreCaseAndActiveTrue("city", pageable)).thenReturn(new PageImpl<>(List.of(hotel), pageable, 1));
        when(hotelRepository.findByCountryIgnoreCaseAndActiveTrue("country", pageable)).thenReturn(new PageImpl<>(List.of(hotel), pageable, 1));
        when(hotelRepository.findByOwnerIdAndActiveTrue(1L, pageable)).thenReturn(new PageImpl<>(List.of(hotel), pageable, 1));
        when(hotelRepository.findByRatingGreaterThanEqualAndActiveTrue(BigDecimal.ONE, pageable)).thenReturn(new PageImpl<>(List.of(hotel), pageable, 1));
        when(hotelRepository.findByStarRatingAndActiveTrue(5, pageable)).thenReturn(new PageImpl<>(List.of(hotel), pageable, 1));
        when(hotelRepository.findByAmenityId(1L, pageable)).thenReturn(new PageImpl<>(List.of(hotel), pageable, 1));
        when(hotelRepository.findTopRated(pageable)).thenReturn(new PageImpl<>(List.of(hotel), pageable, 1));
        when(hotelMapper.toSummaryResponse(any())).thenReturn(HotelSummaryResponse.builder().id(1L).build());
        when(roomRepository.findMinPriceByHotel(1L)).thenReturn(BigDecimal.valueOf(50));
        when(hotelRepository.findDistinctCities()).thenReturn(List.of("A"));
        when(hotelRepository.findDistinctCountries()).thenReturn(List.of("B"));

        assertThat(hotelService.getAllHotels(pageable).getContent()).hasSize(1);
        assertThat(hotelService.searchHotels("t", pageable).getContent()).hasSize(1);
        assertThat(hotelService.getHotelsByCity("city", pageable).getContent()).hasSize(1);
        assertThat(hotelService.getHotelsByCountry("country", pageable).getContent()).hasSize(1);
        assertThat(hotelService.getHotelsByOwner(1L, pageable).getContent()).hasSize(1);
        assertThat(hotelService.getHotelsByMinRating(BigDecimal.ONE, pageable).getContent()).hasSize(1);
        assertThat(hotelService.getHotelsByStarRating(5, pageable).getContent()).hasSize(1);
        assertThat(hotelService.getHotelsByAmenity(1L, pageable).getContent()).hasSize(1);
        assertThat(hotelService.getTopRatedHotels(pageable).getContent()).hasSize(1);
        assertThat(hotelService.getAvailableCities()).contains("A");
        assertThat(hotelService.getAvailableCountries()).contains("B");
    }

    @Test
    void updateDeleteAndAmenitiesAndRating() {
        HotelUpdateRequest updateRequest = HotelUpdateRequest.builder().ownerId(2L).amenityIds(Set.of(3L)).build();
        User owner = User.builder().id(2L).build();
        Amenity amenity = Amenity.builder().id(3L).build();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(amenityRepository.findByIdIn(Set.of(3L))).thenReturn(new HashSet<>(Set.of(amenity)));
        when(hotelRepository.save(hotel)).thenReturn(hotel);
        when(hotelMapper.toResponse(hotel)).thenReturn(response);
        when(roomRepository.findMinPriceByHotel(1L)).thenReturn(BigDecimal.ONE);
        when(roomRepository.findMaxPriceByHotel(1L)).thenReturn(BigDecimal.TEN);
        when(reviewRepository.calculateAverageRatingByHotel(1L)).thenReturn(4.34);

        assertThat(hotelService.updateHotel(1L, updateRequest).getId()).isEqualTo(1L);
        assertThat(hotelService.addAmenitiesToHotel(1L, Set.of(3L)).getId()).isEqualTo(1L);
        assertThat(hotelService.removeAmenitiesFromHotel(1L, Set.of(3L)).getId()).isEqualTo(1L);
        hotelService.deleteHotel(1L);
        hotelService.updateHotelRating(1L);
        verify(hotelRepository, times(5)).save(hotel);
    }

    @Test
    void hotelNotFoundThrows() {
        when(hotelRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> hotelService.getHotelById(42L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
