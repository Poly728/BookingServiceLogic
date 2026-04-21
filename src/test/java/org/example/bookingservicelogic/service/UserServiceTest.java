package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.LoginRequest;
import org.example.bookingservicelogic.dto.request.UserCreateRequest;
import org.example.bookingservicelogic.dto.request.UserUpdateRequest;
import org.example.bookingservicelogic.dto.response.AuthResponse;
import org.example.bookingservicelogic.dto.response.UserResponse;
import org.example.bookingservicelogic.entity.User;
import org.example.bookingservicelogic.entity.enums.Role;
import org.example.bookingservicelogic.exception.DuplicateResourceException;
import org.example.bookingservicelogic.exception.ResourceNotFoundException;
import org.example.bookingservicelogic.mapper.UserMapper;
import org.example.bookingservicelogic.repository.UserRepository;
import org.example.bookingservicelogic.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserResponse testUserResponse;
    private UserCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testUserResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .fullName("Test User")
                .role(Role.USER)
                .enabled(true)
                .build();

        createRequest = UserCreateRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();
    }

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() {
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userMapper.toEntity(any(UserCreateRequest.class))).thenReturn(testUser);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

            UserResponse result = userService.createUser(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when username exists")
        void shouldThrowExceptionWhenUsernameExists() {
            when(userRepository.existsByUsername(anyString())).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(createRequest))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("username");
        }

        @Test
        @DisplayName("Should throw exception when email exists")
        void shouldThrowExceptionWhenEmailExists() {
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(createRequest))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("email");
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should get user by ID")
        void shouldGetUserById() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

            UserResponse result = userService.getUserById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw exception when user not found by ID")
        void shouldThrowExceptionWhenUserNotFoundById() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(1L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should get user by username")
        void shouldGetUserByUsername() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

            UserResponse result = userService.getUserByUsername("testuser");

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Should get all users with pagination")
        void shouldGetAllUsersWithPagination() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(List.of(testUser), pageable, 1);

            when(userRepository.findAll(pageable)).thenReturn(userPage);
            when(userMapper.toResponseList(anyList())).thenReturn(List.of(testUserResponse));

            var result = userService.getAllUsers(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                    .firstName("Updated")
                    .lastName("Name")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            doNothing().when(userMapper).updateEntityFromRequest(any(), any());
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

            UserResponse result = userService.updateUser(1L, updateRequest);

            assertThat(result).isNotNull();
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should authenticate user successfully")
        void shouldAuthenticateUserSuccessfully() {
            LoginRequest loginRequest = LoginRequest.builder()
                    .usernameOrEmail("testuser")
                    .password("password123")
                    .build();

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

            AuthResponse result = userService.authenticate(loginRequest);

            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("Should fail authentication with wrong password")
        void shouldFailAuthenticationWithWrongPassword() {
            LoginRequest loginRequest = LoginRequest.builder()
                    .usernameOrEmail("testuser")
                    .password("wrongpassword")
                    .build();

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

            AuthResponse result = userService.authenticate(loginRequest);

            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("Should fail authentication when user not found")
        void shouldFailAuthenticationWhenUserNotFound() {
            LoginRequest loginRequest = LoginRequest.builder()
                    .usernameOrEmail("unknown")
                    .password("password123")
                    .build();

            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("unknown")).thenReturn(Optional.empty());

            AuthResponse result = userService.authenticate(loginRequest);

            assertThat(result).isNotNull();
            assertThat(result.isSuccess()).isFalse();
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            doNothing().when(userRepository).delete(testUser);

            userService.deleteUser(1L);

            verify(userRepository).delete(testUser);
        }
    }
}
