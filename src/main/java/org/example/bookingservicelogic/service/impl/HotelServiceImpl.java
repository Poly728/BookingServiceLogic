package org.example.bookingservicelogic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.HotelCreateRequest;
import org.example.bookingservicelogic.dto.request.HotelUpdateRequest;
import org.example.bookingservicelogic.dto.response.HotelResponse;
import org.example.bookingservicelogic.dto.response.HotelSummaryResponse;
import org.example.bookingservicelogic.dto.response.PageResponse;
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
import org.example.bookingservicelogic.service.HotelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

/**
 * Implementation of HotelService interface.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final ReviewRepository reviewRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final HotelMapper hotelMapper;

    @Override
    @Transactional
    public HotelResponse createHotel(HotelCreateRequest request) {
        log.debug("Creating new hotel: {}", request.getName());

        Hotel hotel = hotelMapper.toEntity(request);
        applyOwner(hotel, request.getOwnerId());

        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            Set<Amenity> amenities = amenityRepository.findByIdIn(request.getAmenityIds());
            hotel.setAmenities(amenities);
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Created new hotel with ID: {}", savedHotel.getId());

        return hotelMapper.toResponse(savedHotel);
    }

    @Override
    public HotelResponse getHotelById(Long id) {
        log.debug("Fetching hotel by ID: {}", id);
        Hotel hotel = findHotelById(id);
        HotelResponse response = hotelMapper.toResponse(hotel);
        enrichHotelResponse(response, hotel);
        return response;
    }

    @Override
    public PageResponse<HotelSummaryResponse> getAllHotels(Pageable pageable) {
        log.debug("Fetching all hotels, page: {}", pageable.getPageNumber());
        Page<Hotel> hotels = hotelRepository.findByActiveTrue(pageable);
        List<HotelSummaryResponse> content = hotels.getContent().stream()
                .map(this::toSummaryWithMinPrice)
                .toList();
        return PageResponse.of(hotels, content);
    }

    @Override
    public PageResponse<HotelSummaryResponse> searchHotels(String searchTerm, Pageable pageable) {
        log.debug("Searching hotels with term: {}", searchTerm);
        Page<Hotel> hotels = hotelRepository.searchHotels(searchTerm, pageable);
        List<HotelSummaryResponse> content = hotels.getContent().stream()
                .map(this::toSummaryWithMinPrice)
                .toList();
        return PageResponse.of(hotels, content);
    }

    @Override
    public PageResponse<HotelSummaryResponse> getHotelsByCity(String city, Pageable pageable) {
        log.debug("Fetching hotels by city: {}", city);
        Page<Hotel> hotels = hotelRepository.findByCityIgnoreCaseAndActiveTrue(city, pageable);
        List<HotelSummaryResponse> content = hotels.getContent().stream()
                .map(this::toSummaryWithMinPrice)
                .toList();
        return PageResponse.of(hotels, content);
    }

    @Override
    public PageResponse<HotelSummaryResponse> getHotelsByCountry(String country, Pageable pageable) {
        log.debug("Fetching hotels by country: {}", country);
        Page<Hotel> hotels = hotelRepository.findByCountryIgnoreCaseAndActiveTrue(country, pageable);
        List<HotelSummaryResponse> content = hotels.getContent().stream()
                .map(this::toSummaryWithMinPrice)
                .toList();
        return PageResponse.of(hotels, content);
    }

    @Override
    public PageResponse<HotelSummaryResponse> getHotelsByOwner(Long ownerId, Pageable pageable) {
        log.debug("Fetching hotels by owner ID: {}", ownerId);
        Page<Hotel> hotels = hotelRepository.findByOwnerIdAndActiveTrue(ownerId, pageable);
        List<HotelSummaryResponse> content = hotels.getContent().stream()
                .map(this::toSummaryWithMinPrice)
                .toList();
        return PageResponse.of(hotels, content);
    }

    @Override
    public PageResponse<HotelSummaryResponse> getHotelsByMinRating(BigDecimal minRating, Pageable pageable) {
        log.debug("Fetching hotels with min rating: {}", minRating);
        Page<Hotel> hotels = hotelRepository.findByRatingGreaterThanEqualAndActiveTrue(minRating, pageable);
        List<HotelSummaryResponse> content = hotels.getContent().stream()
                .map(this::toSummaryWithMinPrice)
                .toList();
        return PageResponse.of(hotels, content);
    }

    @Override
    public PageResponse<HotelSummaryResponse> getHotelsByStarRating(Integer starRating, Pageable pageable) {
        log.debug("Fetching hotels by star rating: {}", starRating);
        Page<Hotel> hotels = hotelRepository.findByStarRatingAndActiveTrue(starRating, pageable);
        List<HotelSummaryResponse> content = hotels.getContent().stream()
                .map(this::toSummaryWithMinPrice)
                .toList();
        return PageResponse.of(hotels, content);
    }

    @Override
    public PageResponse<HotelSummaryResponse> getHotelsByAmenity(Long amenityId, Pageable pageable) {
        log.debug("Fetching hotels by amenity ID: {}", amenityId);
        Page<Hotel> hotels = hotelRepository.findByAmenityId(amenityId, pageable);
        List<HotelSummaryResponse> content = hotels.getContent().stream()
                .map(this::toSummaryWithMinPrice)
                .toList();
        return PageResponse.of(hotels, content);
    }

    @Override
    public PageResponse<HotelSummaryResponse> getTopRatedHotels(Pageable pageable) {
        log.debug("Fetching top rated hotels");
        Page<Hotel> hotels = hotelRepository.findTopRated(pageable);
        List<HotelSummaryResponse> content = hotels.getContent().stream()
                .map(this::toSummaryWithMinPrice)
                .toList();
        return PageResponse.of(hotels, content);
    }

    @Override
    public List<String> getAvailableCities() {
        log.debug("Fetching available cities");
        return hotelRepository.findDistinctCities();
    }

    @Override
    public List<String> getAvailableCountries() {
        log.debug("Fetching available countries");
        return hotelRepository.findDistinctCountries();
    }

    @Override
    @Transactional
    public HotelResponse updateHotel(Long id, HotelUpdateRequest request) {
        log.debug("Updating hotel with ID: {}", id);
        Hotel hotel = findHotelById(id);
        hotelMapper.updateEntityFromRequest(request, hotel);
        applyOwner(hotel, request.getOwnerId());

        if (request.getAmenityIds() != null) {
            Set<Amenity> amenities = amenityRepository.findByIdIn(request.getAmenityIds());
            hotel.setAmenities(amenities);
        }

        Hotel updatedHotel = hotelRepository.save(hotel);
        log.info("Updated hotel with ID: {}", id);

        HotelResponse response = hotelMapper.toResponse(updatedHotel);
        enrichHotelResponse(response, updatedHotel);
        return response;
    }

    @Override
    @Transactional
    public void deleteHotel(Long id) {
        log.debug("Deleting hotel with ID: {}", id);
        Hotel hotel = findHotelById(id);
        hotel.setActive(false);
        hotelRepository.save(hotel);
        log.info("Soft deleted hotel with ID: {}", id);
    }

    @Override
    @Transactional
    public HotelResponse addAmenitiesToHotel(Long hotelId, Set<Long> amenityIds) {
        log.debug("Adding amenities to hotel ID: {}", hotelId);
        Hotel hotel = findHotelById(hotelId);
        Set<Amenity> amenities = amenityRepository.findByIdIn(amenityIds);
        hotel.getAmenities().addAll(amenities);
        Hotel updatedHotel = hotelRepository.save(hotel);
        log.info("Added {} amenities to hotel ID: {}", amenities.size(), hotelId);
        return hotelMapper.toResponse(updatedHotel);
    }

    @Override
    @Transactional
    public HotelResponse removeAmenitiesFromHotel(Long hotelId, Set<Long> amenityIds) {
        log.debug("Removing amenities from hotel ID: {}", hotelId);
        Hotel hotel = findHotelById(hotelId);
        Set<Amenity> amenities = amenityRepository.findByIdIn(amenityIds);
        hotel.getAmenities().removeAll(amenities);
        Hotel updatedHotel = hotelRepository.save(hotel);
        log.info("Removed amenities from hotel ID: {}", hotelId);
        return hotelMapper.toResponse(updatedHotel);
    }

    @Override
    @Transactional
    public void updateHotelRating(Long hotelId) {
        log.debug("Updating rating for hotel ID: {}", hotelId);
        Hotel hotel = findHotelById(hotelId);
        Double avgRating = reviewRepository.calculateAverageRatingByHotel(hotelId);
        if (avgRating != null) {
            hotel.setRating(BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP));
            hotelRepository.save(hotel);
            log.info("Updated rating for hotel ID: {} to {}", hotelId, avgRating);
        }
    }

    private Hotel findHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));
    }

    private HotelSummaryResponse toSummaryWithMinPrice(Hotel hotel) {
        HotelSummaryResponse response = hotelMapper.toSummaryResponse(hotel);
        BigDecimal minPrice = roomRepository.findMinPriceByHotel(hotel.getId());
        if (minPrice != null) {
            response.setMinPrice(minPrice);
        }
        return response;
    }

    private void enrichHotelResponse(HotelResponse response, Hotel hotel) {
        BigDecimal minPrice = roomRepository.findMinPriceByHotel(hotel.getId());
        BigDecimal maxPrice = roomRepository.findMaxPriceByHotel(hotel.getId());
        response.setMinPrice(minPrice);
        response.setMaxPrice(maxPrice);
    }

    private void applyOwner(Hotel hotel, Long ownerId) {
        if (ownerId == null) {
            return;
        }
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));
        hotel.setOwner(owner);
    }
}
