package org.example.bookingservicelogic.repository;

import org.example.bookingservicelogic.entity.User;
import org.example.bookingservicelogic.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides CRUD operations and custom queries for user management.
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a username already exists.
     *
     * @param username the username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks if an email already exists.
     *
     * @param email the email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds all users with a specific role.
     *
     * @param role the role to filter by
     * @param pageable pagination information
     * @return page of users with the specified role
     */
    Page<User> findByRole(Role role, Pageable pageable);

    /**
     * Finds all enabled users.
     *
     * @param pageable pagination information
     * @return page of enabled users
     */
    Page<User> findByEnabledTrue(Pageable pageable);

    /**
     * Searches users by username or email containing the search term.
     *
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of matching users
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Finds users with bookings count.
     *
     * @return list of users with their booking counts
     */
    @Query("SELECT u, COUNT(b) FROM User u LEFT JOIN u.bookings b GROUP BY u")
    List<Object[]> findUsersWithBookingCount();
}
