package org.example.bookingservicelogic.mapper;

import org.example.bookingservicelogic.dto.request.AmenityCreateRequest;
import org.example.bookingservicelogic.dto.response.AmenityResponse;
import org.example.bookingservicelogic.entity.Amenity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

/**
 * MapStruct mapper for Amenity entity.
 *
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AmenityMapper {

    /**
     * Converts AmenityCreateRequest to Amenity entity.
     *
     * @param request the create request
     * @return Amenity entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "hotels", ignore = true)
    Amenity toEntity(AmenityCreateRequest request);

    /**
     * Converts Amenity entity to AmenityResponse.
     *
     * @param amenity the entity
     * @return AmenityResponse DTO
     */
    AmenityResponse toResponse(Amenity amenity);

    /**
     * Converts list of Amenity entities to list of AmenityResponse.
     *
     * @param amenities list of entities
     * @return list of AmenityResponse DTOs
     */
    List<AmenityResponse> toResponseList(List<Amenity> amenities);

    /**
     * Converts set of Amenity entities to set of AmenityResponse.
     *
     * @param amenities set of entities
     * @return set of AmenityResponse DTOs
     */
    Set<AmenityResponse> toResponseSet(Set<Amenity> amenities);

    /**
     * Updates Amenity entity from AmenityCreateRequest.
     *
     * @param request the update request
     * @param amenity the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "hotels", ignore = true)
    void updateEntityFromRequest(AmenityCreateRequest request, @MappingTarget Amenity amenity);
}
