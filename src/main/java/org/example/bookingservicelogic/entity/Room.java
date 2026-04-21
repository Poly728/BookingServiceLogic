package org.example.bookingservicelogic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.bookingservicelogic.entity.enums.RoomType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a room/unit within a hotel or property.
 * Rooms are the bookable units in the system.
 *
 */
@Entity
@Table(name = "rooms", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hotel_id", "room_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false, length = 30)
    private RoomType roomType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(name = "capacity", nullable = false)
    @Builder.Default
    private Integer capacity = 2;

    @Column(name = "bed_count", nullable = false)
    @Builder.Default
    private Integer bedCount = 1;

    @Column(name = "area_sqm", precision = 5, scale = 2)
    private BigDecimal areaSqm;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "available", nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Returns the full room identifier including hotel name.
     *
     * @return formatted room identifier
     */
    public String getFullRoomIdentifier() {
        if (hotel != null) {
            return hotel.getName() + " - Room " + roomNumber;
        }
        return "Room " + roomNumber;
    }
}
