package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.AmenityCreateRequest;
import org.example.bookingservicelogic.dto.response.AmenityResponse;
import org.example.bookingservicelogic.entity.Amenity;
import org.example.bookingservicelogic.exception.DuplicateResourceException;
import org.example.bookingservicelogic.exception.ResourceNotFoundException;
import org.example.bookingservicelogic.mapper.AmenityMapper;
import org.example.bookingservicelogic.repository.AmenityRepository;
import org.example.bookingservicelogic.service.impl.AmenityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTest {

    @Mock
    private AmenityRepository amenityRepository;
    @Mock
    private AmenityMapper amenityMapper;
    @InjectMocks
    private AmenityServiceImpl amenityService;

    private Amenity amenity;
    private AmenityResponse response;

    @BeforeEach
    void setUp() {
        amenity = Amenity.builder().id(1L).name("WiFi").build();
        response = AmenityResponse.builder().id(1L).name("WiFi").build();
    }

    @Test
    void createAmenitySuccess() {
        AmenityCreateRequest request = AmenityCreateRequest.builder().name("WiFi").build();
        when(amenityRepository.existsByNameIgnoreCase("WiFi")).thenReturn(false);
        when(amenityMapper.toEntity(request)).thenReturn(amenity);
        when(amenityRepository.save(amenity)).thenReturn(amenity);
        when(amenityMapper.toResponse(amenity)).thenReturn(response);

        assertThat(amenityService.createAmenity(request).getName()).isEqualTo("WiFi");
    }

    @Test
    void createAmenityDuplicateThrows() {
        AmenityCreateRequest request = AmenityCreateRequest.builder().name("WiFi").build();
        when(amenityRepository.existsByNameIgnoreCase("WiFi")).thenReturn(true);

        assertThatThrownBy(() -> amenityService.createAmenity(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getByIdAndByNameAndDelete() {
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
        when(amenityRepository.findByNameIgnoreCase("WiFi")).thenReturn(Optional.of(amenity));
        when(amenityMapper.toResponse(amenity)).thenReturn(response);

        assertThat(amenityService.getAmenityById(1L).getId()).isEqualTo(1L);
        assertThat(amenityService.getAmenityByName("WiFi").getName()).isEqualTo("WiFi");

        amenityService.deleteAmenity(1L);
        verify(amenityRepository).delete(amenity);
    }

    @Test
    void updateAmenityDuplicateThrows() {
        AmenityCreateRequest request = AmenityCreateRequest.builder().name("Pool").build();
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(amenity));
        when(amenityRepository.existsByNameIgnoreCase("Pool")).thenReturn(true);

        assertThatThrownBy(() -> amenityService.updateAmenity(1L, request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void getPagedAndSearchAndPopularAndCount() {
        var pageable = PageRequest.of(0, 10);
        when(amenityRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(amenity), pageable, 1));
        when(amenityRepository.searchAmenities("wi", pageable)).thenReturn(new PageImpl<>(List.of(amenity), pageable, 1));
        when(amenityMapper.toResponseList(any())).thenReturn(List.of(response));
        when(amenityRepository.findByHotelId(1L)).thenReturn(Set.of(amenity));
        when(amenityMapper.toResponseSet(any())).thenReturn(Set.of(response));
        when(amenityRepository.findMostPopularAmenities(PageRequest.of(0, 2))).thenReturn(List.<Object[]>of(new Object[]{amenity, 1L}));
        when(amenityMapper.toResponse(amenity)).thenReturn(response);
        when(amenityRepository.countHotelsByAmenity(1L)).thenReturn(3L);

        assertThat(amenityService.getAllAmenitiesPaged(pageable).getContent()).hasSize(1);
        assertThat(amenityService.searchAmenities("wi", pageable).getContent()).hasSize(1);
        assertThat(amenityService.getAmenitiesByHotel(1L)).hasSize(1);
        assertThat(amenityService.getMostPopularAmenities(2)).hasSize(1);
        assertThat(amenityService.countHotelsByAmenity(1L)).isEqualTo(3L);
    }

    @Test
    void findByIdNotFoundThrows() {
        when(amenityRepository.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> amenityService.getAmenityById(10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
