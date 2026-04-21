package org.example.bookingservicelogic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.bookingservicelogic.entity.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Entity representing a booking/reservation in the system.
 * A booking connects a user to a room for specific dates.
 *
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "guests_count", nullable = false)
    @Builder.Default
    private Integer guestsCount = 1;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates the number of nights for this booking.
     *
     * @return number of nights between check-in and check-out
     */
    public long getNightsCount() {
        if (checkInDate != null && checkOutDate != null) {
            return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 0;
    }

    /**
     * Calculates and sets the total price based on room price and nights.
     */
    public void calculateTotalPrice() {
        if (room != null && room.getPricePerNight() != null) {
            long nights = getNightsCount();
            this.totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        }
    }

    /**
     * Checks if this booking overlaps with given dates.
     *
     * @param startDate the start date to check
     * @param endDate the end date to check
     * @return true if dates overlap with this booking
     */
    public boolean overlapsWithDates(LocalDate startDate, LocalDate endDate) {
        return !checkOutDate.isBefore(startDate) && !checkInDate.isAfter(endDate);
    }

    /**
     * Checks if the booking can be cancelled.
     *
     * @return true if booking status allows cancellation
     */
    public boolean canBeCancelled() {
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }
}
