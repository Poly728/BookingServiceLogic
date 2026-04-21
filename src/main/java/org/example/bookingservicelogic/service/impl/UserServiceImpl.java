package org.example.bookingservicelogic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.LoginRequest;
import org.example.bookingservicelogic.dto.request.UserCreateRequest;
import org.example.bookingservicelogic.dto.request.UserUpdateRequest;
import org.example.bookingservicelogic.dto.response.AuthResponse;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.dto.response.UserResponse;
import org.example.bookingservicelogic.entity.User;
import org.example.bookingservicelogic.entity.enums.Role;
import org.example.bookingservicelogic.exception.DuplicateResourceException;
import org.example.bookingservicelogic.exception.ResourceNotFoundException;
import org.example.bookingservicelogic.mapper.UserMapper;
import org.example.bookingservicelogic.repository.UserRepository;
import org.example.bookingservicelogic.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of UserService interface.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        log.debug("Creating new user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("Created new user with ID: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        User user = findUserById(id);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users, page: {}", pageable.getPageNumber());
        Page<User> users = userRepository.findAll(pageable);
        List<UserResponse> content = userMapper.toResponseList(users.getContent());
        return PageResponse.of(users, content);
    }

    @Override
    public PageResponse<UserResponse> getUsersByRole(Role role, Pageable pageable) {
        log.debug("Fetching users by role: {}", role);
        Page<User> users = userRepository.findByRole(role, pageable);
        List<UserResponse> content = userMapper.toResponseList(users.getContent());
        return PageResponse.of(users, content);
    }

    @Override
    public PageResponse<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
        log.debug("Searching users with term: {}", searchTerm);
        Page<User> users = userRepository.searchUsers(searchTerm, pageable);
        List<UserResponse> content = userMapper.toResponseList(users.getContent());
        return PageResponse.of(users, content);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.debug("Updating user with ID: {}", id);
        User user = findUserById(id);

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("User", "email", request.getEmail());
            }
        }

        userMapper.updateEntityFromRequest(request, user);

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("Updated user with ID: {}", id);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user with ID: {}", id);
        User user = findUserById(id);
        userRepository.delete(user);
        log.info("Deleted user with ID: {}", id);
    }

    @Override
    public AuthResponse authenticate(LoginRequest request) {
        log.debug("Authenticating user: {}", request.getUsernameOrEmail());

        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElse(null);

        if (user == null) {
            log.warn("Authentication failed: user not found");
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid username/email or password")
                    .build();
        }

        if (!user.getEnabled()) {
            log.warn("Authentication failed: user disabled");
            return AuthResponse.builder()
                    .success(false)
                    .message("User account is disabled")
                    .build();
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Authentication failed: invalid password");
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid username/email or password")
                    .build();
        }

        log.info("User authenticated successfully: {}", user.getUsername());
        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .success(true)
                .message("Authentication successful")
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    @Transactional
    public UserResponse changeUserRole(Long id, Role role) {
        log.debug("Changing role for user ID: {} to {}", id, role);
        User user = findUserById(id);
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        log.info("Changed role for user ID: {} to {}", id, role);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse setUserEnabled(Long id, boolean enabled) {
        log.debug("Setting enabled={} for user ID: {}", enabled, id);
        User user = findUserById(id);
        user.setEnabled(enabled);
        User updatedUser = userRepository.save(user);
        log.info("Set enabled={} for user ID: {}", enabled, id);
        return userMapper.toResponse(updatedUser);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
