package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.HotelCreateRequest;
import org.example.bookingservicelogic.dto.request.HotelUpdateRequest;
import org.example.bookingservicelogic.dto.response.HotelResponse;
import org.example.bookingservicelogic.dto.response.HotelSummaryResponse;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for hotel management operations.
 *
 */
public interface HotelService {

    /**
     * Creates a new hotel.
     *
     * @param request the hotel creation request
     * @return the created hotel response
     */
    HotelResponse createHotel(HotelCreateRequest request);

    /**
     * Retrieves a hotel by ID.
     *
     * @param id the hotel ID
     * @return the hotel response
     */
    HotelResponse getHotelById(Long id);

    /**
     * Retrieves all active hotels with pagination.
     *
     * @param pageable pagination information
     * @return page of hotel summary responses
     */
    PageResponse<HotelSummaryResponse> getAllHotels(Pageable pageable);

    /**
     * Searches hotels by search term.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching hotel summary responses
     */
    PageResponse<HotelSummaryResponse> searchHotels(String searchTerm, Pageable pageable);

    /**
     * Retrieves hotels by city.
     *
     * @param city the city name
     * @param pageable pagination information
     * @return page of hotel summary responses
     */
    PageResponse<HotelSummaryResponse> getHotelsByCity(String city, Pageable pageable);

    /**
     * Retrieves hotels by country.
     *
     * @param country the country name
     * @param pageable pagination information
     * @return page of hotel summary responses
     */
    PageResponse<HotelSummaryResponse> getHotelsByCountry(String country, Pageable pageable);

    /**
     * Retrieves hotels by owner.
     *
     * @param ownerId owner user ID
     * @param pageable pagination information
     * @return page of hotel summary responses
     */
    PageResponse<HotelSummaryResponse> getHotelsByOwner(Long ownerId, Pageable pageable);

    /**
     * Retrieves hotels by minimum rating.
     *
     * @param minRating minimum rating
     * @param pageable pagination information
     * @return page of hotel summary responses
     */
    PageResponse<HotelSummaryResponse> getHotelsByMinRating(BigDecimal minRating, Pageable pageable);

    /**
     * Retrieves hotels by star rating.
     *
     * @param starRating the star rating
     * @param pageable pagination information
     * @return page of hotel summary responses
     */
    PageResponse<HotelSummaryResponse> getHotelsByStarRating(Integer starRating, Pageable pageable);

    /**
     * Retrieves hotels that have a specific amenity.
     *
     * @param amenityId the amenity ID
     * @param pageable pagination information
     * @return page of hotel summary responses
     */
    PageResponse<HotelSummaryResponse> getHotelsByAmenity(Long amenityId, Pageable pageable);

    /**
     * Retrieves top rated hotels.
     *
     * @param pageable pagination information
     * @return page of hotel summary responses
     */
    PageResponse<HotelSummaryResponse> getTopRatedHotels(Pageable pageable);

    /**
     * Gets distinct cities with hotels.
     *
     * @return list of city names
     */
    List<String> getAvailableCities();

    /**
     * Gets distinct countries with hotels.
     *
     * @return list of country names
     */
    List<String> getAvailableCountries();

    /**
     * Updates an existing hotel.
     *
     * @param id the hotel ID
     * @param request the update request
     * @return the updated hotel response
     */
    HotelResponse updateHotel(Long id, HotelUpdateRequest request);

    /**
     * Deletes a hotel (soft delete - sets active to false).
     *
     * @param id the hotel ID
     */
    void deleteHotel(Long id);

    /**
     * Adds amenities to a hotel.
     *
     * @param hotelId the hotel ID
     * @param amenityIds set of amenity IDs to add
     * @return the updated hotel response
     */
    HotelResponse addAmenitiesToHotel(Long hotelId, java.util.Set<Long> amenityIds);

    /**
     * Removes amenities from a hotel.
     *
     * @param hotelId the hotel ID
     * @param amenityIds set of amenity IDs to remove
     * @return the updated hotel response
     */
    HotelResponse removeAmenitiesFromHotel(Long hotelId, java.util.Set<Long> amenityIds);

    /**
     * Updates the hotel rating based on reviews.
     *
     * @param hotelId the hotel ID
     */
    void updateHotelRating(Long hotelId);
}
