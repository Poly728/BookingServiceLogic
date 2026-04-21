package org.example.bookingservicelogic.mapper;

import org.example.bookingservicelogic.dto.request.UserCreateRequest;
import org.example.bookingservicelogic.dto.request.UserUpdateRequest;
import org.example.bookingservicelogic.dto.response.UserResponse;
import org.example.bookingservicelogic.entity.User;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for User entity.
 *
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    /**
     * Converts UserCreateRequest to User entity.
     *
     * @param request the create request
     * @return User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    User toEntity(UserCreateRequest request);

    /**
     * Converts User entity to UserResponse.
     *
     * @param user the entity
     * @return UserResponse DTO
     */
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    UserResponse toResponse(User user);

    /**
     * Converts list of User entities to list of UserResponse.
     *
     * @param users list of entities
     * @return list of UserResponse DTOs
     */
    List<UserResponse> toResponseList(List<User> users);

    /**
     * Updates User entity from UserUpdateRequest.
     *
     * @param request the update request
     * @param user the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    void updateEntityFromRequest(UserUpdateRequest request, @MappingTarget User user);
}
