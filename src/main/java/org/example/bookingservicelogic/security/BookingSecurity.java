package org.example.bookingservicelogic.security;

import lombok.RequiredArgsConstructor;
import org.example.bookingservicelogic.entity.Booking;
import org.example.bookingservicelogic.entity.User;
import org.example.bookingservicelogic.entity.enums.Role;
import org.example.bookingservicelogic.repository.BookingRepository;
import org.example.bookingservicelogic.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingSecurity {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    /**
     * Разрешает изменение бронирования админу или владельцу бронирования.
     *
     * @param bookingId идентификатор бронирования
     * @param username имя пользователя текущей сессии
     * @return true если разрешено, иначе false
     */
    public boolean canModifyBooking(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return false;
        }

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return false;
        }

        // ADMIN всегда имеет право
        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        // Владелец бронирования может менять свои бронирования
        if (booking.getUser() != null && booking.getUser().getUsername().equals(username)) {
            return true;
        }

        return false;
    }
}