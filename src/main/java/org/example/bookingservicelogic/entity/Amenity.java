package org.example.bookingservicelogic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing an amenity/feature that hotels can offer.
 * Examples: WiFi, Pool, Parking, etc.
 *
 */
@Entity
@Table(name = "amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "icon", length = 50)
    private String icon;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany(mappedBy = "amenities")
    @Builder.Default
    private Set<Hotel> hotels = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Amenity amenity)) return false;
        return id != null && id.equals(amenity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
