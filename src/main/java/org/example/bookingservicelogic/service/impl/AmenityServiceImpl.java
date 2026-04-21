package org.example.bookingservicelogic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.AmenityCreateRequest;
import org.example.bookingservicelogic.dto.response.AmenityResponse;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.entity.Amenity;
import org.example.bookingservicelogic.exception.DuplicateResourceException;
import org.example.bookingservicelogic.exception.ResourceNotFoundException;
import org.example.bookingservicelogic.mapper.AmenityMapper;
import org.example.bookingservicelogic.repository.AmenityRepository;
import org.example.bookingservicelogic.service.AmenityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Implementation of AmenityService interface.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    @Override
    @Transactional
    public AmenityResponse createAmenity(AmenityCreateRequest request) {
        log.debug("Creating new amenity: {}", request.getName());

        if (amenityRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Amenity", "name", request.getName());
        }

        Amenity amenity = amenityMapper.toEntity(request);
        Amenity savedAmenity = amenityRepository.save(amenity);
        log.info("Created new amenity with ID: {}", savedAmenity.getId());

        return amenityMapper.toResponse(savedAmenity);
    }

    @Override
    public AmenityResponse getAmenityById(Long id) {
        log.debug("Fetching amenity by ID: {}", id);
        Amenity amenity = findAmenityById(id);
        return amenityMapper.toResponse(amenity);
    }

    @Override
    public AmenityResponse getAmenityByName(String name) {
        log.debug("Fetching amenity by name: {}", name);
        Amenity amenity = amenityRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "name", name));
        return amenityMapper.toResponse(amenity);
    }

    @Override
    public List<AmenityResponse> getAllAmenities() {
        log.debug("Fetching all amenities");
        List<Amenity> amenities = amenityRepository.findAll();
        return amenityMapper.toResponseList(amenities);
    }

    @Override
    public PageResponse<AmenityResponse> getAllAmenitiesPaged(Pageable pageable) {
        log.debug("Fetching all amenities, page: {}", pageable.getPageNumber());
        Page<Amenity> amenities = amenityRepository.findAll(pageable);
        List<AmenityResponse> content = amenityMapper.toResponseList(amenities.getContent());
        return PageResponse.of(amenities, content);
    }

    @Override
    public Set<AmenityResponse> getAmenitiesByHotel(Long hotelId) {
        log.debug("Fetching amenities for hotel ID: {}", hotelId);
        Set<Amenity> amenities = amenityRepository.findByHotelId(hotelId);
        return amenityMapper.toResponseSet(amenities);
    }

    @Override
    public PageResponse<AmenityResponse> searchAmenities(String searchTerm, Pageable pageable) {
        log.debug("Searching amenities with term: {}", searchTerm);
        Page<Amenity> amenities = amenityRepository.searchAmenities(searchTerm, pageable);
        List<AmenityResponse> content = amenityMapper.toResponseList(amenities.getContent());
        return PageResponse.of(amenities, content);
    }

    @Override
    public List<AmenityResponse> getMostPopularAmenities(int limit) {
        log.debug("Fetching {} most popular amenities", limit);
        List<Object[]> results = amenityRepository.findMostPopularAmenities(PageRequest.of(0, limit));
        return results.stream()
                .map(row -> amenityMapper.toResponse((Amenity) row[0]))
                .toList();
    }

    @Override
    @Transactional
    public AmenityResponse updateAmenity(Long id, AmenityCreateRequest request) {
        log.debug("Updating amenity with ID: {}", id);
        Amenity amenity = findAmenityById(id);

        if (request.getName() != null && !request.getName().equalsIgnoreCase(amenity.getName())) {
            if (amenityRepository.existsByNameIgnoreCase(request.getName())) {
                throw new DuplicateResourceException("Amenity", "name", request.getName());
            }
        }

        amenityMapper.updateEntityFromRequest(request, amenity);
        Amenity updatedAmenity = amenityRepository.save(amenity);
        log.info("Updated amenity with ID: {}", id);

        return amenityMapper.toResponse(updatedAmenity);
    }

    @Override
    @Transactional
    public void deleteAmenity(Long id) {
        log.debug("Deleting amenity with ID: {}", id);
        Amenity amenity = findAmenityById(id);
        amenityRepository.delete(amenity);
        log.info("Deleted amenity with ID: {}", id);
    }

    @Override
    public Long countHotelsByAmenity(Long amenityId) {
        log.debug("Counting hotels with amenity ID: {}", amenityId);
        return amenityRepository.countHotelsByAmenity(amenityId);
    }

    private Amenity findAmenityById(Long id) {
        return amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id));
    }
}
