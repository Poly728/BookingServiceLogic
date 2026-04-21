package org.example.bookingservicelogic.repository;

import org.example.bookingservicelogic.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Hotel entity operations.
 * Provides CRUD operations and custom queries for hotel/property management.
 *
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    /**
     * Finds all active hotels.
     *
     * @param pageable pagination information
     * @return page of active hotels
     */
    Page<Hotel> findByActiveTrue(Pageable pageable);

    /**
     * Finds hotels by city.
     *
     * @param city the city to search in
     * @param pageable pagination information
     * @return page of hotels in the specified city
     */
    Page<Hotel> findByCityIgnoreCaseAndActiveTrue(String city, Pageable pageable);

    /**
     * Finds hotels by country.
     *
     * @param country the country to search in
     * @param pageable pagination information
     * @return page of hotels in the specified country
     */
    Page<Hotel> findByCountryIgnoreCaseAndActiveTrue(String country, Pageable pageable);

    /**
     * Finds all active hotels by owner ID.
     *
     * @param ownerId owner user ID
     * @param pageable pagination information
     * @return page of owner's hotels
     */
    Page<Hotel> findByOwnerIdAndActiveTrue(Long ownerId, Pageable pageable);

    /**
     * Finds hotels with rating greater than or equal to specified value.
     *
     * @param minRating minimum rating
     * @param pageable pagination information
     * @return page of hotels with rating >= minRating
     */
    Page<Hotel> findByRatingGreaterThanEqualAndActiveTrue(BigDecimal minRating, Pageable pageable);

    /**
     * Finds hotels by star rating.
     *
     * @param starRating the star rating to filter by
     * @param pageable pagination information
     * @return page of hotels with specified star rating
     */
    Page<Hotel> findByStarRatingAndActiveTrue(Integer starRating, Pageable pageable);

    /**
     * Searches hotels by name, city, or country.
     *
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of matching hotels
     */
    @Query("SELECT h FROM Hotel h WHERE h.active = true AND " +
            "(LOWER(h.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(h.city) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(h.country) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(h.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Hotel> searchHotels(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Finds hotels by city and star rating.
     *
     * @param city the city to search in
     * @param starRating the minimum star rating
     * @param pageable pagination information
     * @return page of matching hotels
     */
    @Query("SELECT h FROM Hotel h WHERE h.active = true " +
            "AND LOWER(h.city) = LOWER(:city) " +
            "AND h.starRating >= :starRating")
    Page<Hotel> findByCityAndMinStarRating(@Param("city") String city,
                                            @Param("starRating") Integer starRating,
                                            Pageable pageable);

    /**
     * Gets distinct cities with active hotels.
     *
     * @return list of city names
     */
    @Query("SELECT DISTINCT h.city FROM Hotel h WHERE h.active = true ORDER BY h.city")
    List<String> findDistinctCities();

    /**
     * Gets distinct countries with active hotels.
     *
     * @return list of country names
     */
    @Query("SELECT DISTINCT h.country FROM Hotel h WHERE h.active = true ORDER BY h.country")
    List<String> findDistinctCountries();

    /**
     * Finds hotels that have a specific amenity.
     *
     * @param amenityId the amenity ID
     * @param pageable pagination information
     * @return page of hotels with the specified amenity
     */
    @Query("SELECT h FROM Hotel h JOIN h.amenities a WHERE h.active = true AND a.id = :amenityId")
    Page<Hotel> findByAmenityId(@Param("amenityId") Long amenityId, Pageable pageable);

    /**
     * Finds top rated hotels.
     *
     * @param pageable pagination information
     * @return page of top rated hotels
     */
    @Query("SELECT h FROM Hotel h WHERE h.active = true ORDER BY h.rating DESC")
    Page<Hotel> findTopRated(Pageable pageable);
}
