package org.example.bookingservicelogic.repository;

import org.example.bookingservicelogic.entity.Amenity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for Amenity entity operations.
 * Provides CRUD operations and custom queries for amenity management.
 *
 */
@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    /**
     * Finds an amenity by name.
     *
     * @param name the amenity name
     * @return Optional containing the amenity if found
     */
    Optional<Amenity> findByNameIgnoreCase(String name);

    /**
     * Checks if an amenity exists by name.
     *
     * @param name the amenity name
     * @return true if amenity exists
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Finds amenities by IDs.
     *
     * @param ids the set of IDs
     * @return set of amenities
     */
    Set<Amenity> findByIdIn(Set<Long> ids);

    /**
     * Finds all amenities for a hotel.
     *
     * @param hotelId the hotel ID
     * @return set of amenities for the hotel
     */
    @Query("SELECT a FROM Amenity a JOIN a.hotels h WHERE h.id = :hotelId")
    Set<Amenity> findByHotelId(@Param("hotelId") Long hotelId);

    /**
     * Searches amenities by name.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching amenities
     */
    @Query("SELECT a FROM Amenity a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Amenity> searchAmenities(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Finds most popular amenities (by hotel count).
     *
     * @param pageable pagination information
     * @return list of amenities ordered by popularity
     */
    @Query("SELECT a, COUNT(h) as hotelCount FROM Amenity a LEFT JOIN a.hotels h " +
            "GROUP BY a ORDER BY hotelCount DESC")
    List<Object[]> findMostPopularAmenities(Pageable pageable);

    /**
     * Counts hotels that have a specific amenity.
     *
     * @param amenityId the amenity ID
     * @return number of hotels with this amenity
     */
    @Query("SELECT COUNT(h) FROM Hotel h JOIN h.amenities a WHERE a.id = :amenityId AND h.active = true")
    Long countHotelsByAmenity(@Param("amenityId") Long amenityId);
}
