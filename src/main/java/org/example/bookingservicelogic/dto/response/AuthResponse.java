package org.example.bookingservicelogic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bookingservicelogic.entity.enums.Role;

/**
 * DTO for authentication response data.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private Long userId;
    private String username;
    private String email;
    private Role role;
    private String message;
    private boolean success;

    // Full user object for frontend convenience
    private UserResponse user;
}
