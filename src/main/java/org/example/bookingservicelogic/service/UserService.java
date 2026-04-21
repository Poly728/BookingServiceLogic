package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.LoginRequest;
import org.example.bookingservicelogic.dto.request.UserCreateRequest;
import org.example.bookingservicelogic.dto.request.UserUpdateRequest;
import org.example.bookingservicelogic.dto.response.AuthResponse;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.dto.response.UserResponse;
import org.example.bookingservicelogic.entity.enums.Role;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for user management operations.
 *
 */
public interface UserService {

    /**
     * Creates a new user.
     *
     * @param request the user creation request
     * @return the created user response
     */
    UserResponse createUser(UserCreateRequest request);

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID
     * @return the user response
     */
    UserResponse getUserById(Long id);

    /**
     * Retrieves a user by username.
     *
     * @param username the username
     * @return the user response
     */
    UserResponse getUserByUsername(String username);

    /**
     * Retrieves a user by email.
     *
     * @param email the email
     * @return the user response
     */
    UserResponse getUserByEmail(String email);

    /**
     * Retrieves all users with pagination.
     *
     * @param pageable pagination information
     * @return page of user responses
     */
    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Retrieves users by role.
     *
     * @param role the role to filter by
     * @param pageable pagination information
     * @return page of user responses
     */
    PageResponse<UserResponse> getUsersByRole(Role role, Pageable pageable);

    /**
     * Searches users by search term.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching user responses
     */
    PageResponse<UserResponse> searchUsers(String searchTerm, Pageable pageable);

    /**
     * Updates an existing user.
     *
     * @param id the user ID
     * @param request the update request
     * @return the updated user response
     */
    UserResponse updateUser(Long id, UserUpdateRequest request);

    /**
     * Deletes a user.
     *
     * @param id the user ID
     */
    void deleteUser(Long id);

    /**
     * Authenticates a user.
     *
     * @param request the login request
     * @return authentication response
     */
    AuthResponse authenticate(LoginRequest request);

    /**
     * Changes user role.
     *
     * @param id the user ID
     * @param role the new role
     * @return the updated user response
     */
    UserResponse changeUserRole(Long id, Role role);

    /**
     * Enables or disables a user account.
     *
     * @param id the user ID
     * @param enabled the enabled status
     * @return the updated user response
     */
    UserResponse setUserEnabled(Long id, boolean enabled);
}
