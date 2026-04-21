package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.AmenityCreateRequest;
import org.example.bookingservicelogic.dto.response.AmenityResponse;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * Service interface for amenity management operations.
 *
 */
public interface AmenityService {

    /**
     * Creates a new amenity.
     *
     * @param request the amenity creation request
     * @return the created amenity response
     */
    AmenityResponse createAmenity(AmenityCreateRequest request);

    /**
     * Retrieves an amenity by ID.
     *
     * @param id the amenity ID
     * @return the amenity response
     */
    AmenityResponse getAmenityById(Long id);

    /**
     * Retrieves an amenity by name.
     *
     * @param name the amenity name
     * @return the amenity response
     */
    AmenityResponse getAmenityByName(String name);

    /**
     * Retrieves all amenities.
     *
     * @return list of all amenity responses
     */
    List<AmenityResponse> getAllAmenities();

    /**
     * Retrieves all amenities with pagination.
     *
     * @param pageable pagination information
     * @return page of amenity responses
     */
    PageResponse<AmenityResponse> getAllAmenitiesPaged(Pageable pageable);

    /**
     * Retrieves amenities for a hotel.
     *
     * @param hotelId the hotel ID
     * @return set of amenity responses
     */
    Set<AmenityResponse> getAmenitiesByHotel(Long hotelId);

    /**
     * Searches amenities by name or description.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching amenity responses
     */
    PageResponse<AmenityResponse> searchAmenities(String searchTerm, Pageable pageable);

    /**
     * Retrieves most popular amenities.
     *
     * @param limit maximum number to return
     * @return list of popular amenity responses
     */
    List<AmenityResponse> getMostPopularAmenities(int limit);

    /**
     * Updates an existing amenity.
     *
     * @param id the amenity ID
     * @param request the update request
     * @return the updated amenity response
     */
    AmenityResponse updateAmenity(Long id, AmenityCreateRequest request);

    /**
     * Deletes an amenity.
     *
     * @param id the amenity ID
     */
    void deleteAmenity(Long id);

    /**
     * Counts hotels that have a specific amenity.
     *
     * @param amenityId the amenity ID
     * @return number of hotels
     */
    Long countHotelsByAmenity(Long amenityId);
}
