package org.example.bookingservicelogic.service;

import org.example.bookingservicelogic.dto.request.RoomCreateRequest;
import org.example.bookingservicelogic.dto.request.RoomUpdateRequest;
import org.example.bookingservicelogic.dto.response.RoomResponse;
import org.example.bookingservicelogic.entity.Hotel;
import org.example.bookingservicelogic.entity.Room;
import org.example.bookingservicelogic.entity.enums.RoomType;
import org.example.bookingservicelogic.exception.DuplicateResourceException;
import org.example.bookingservicelogic.exception.ResourceNotFoundException;
import org.example.bookingservicelogic.mapper.RoomMapper;
import org.example.bookingservicelogic.repository.HotelRepository;
import org.example.bookingservicelogic.repository.RoomRepository;
import org.example.bookingservicelogic.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock private RoomRepository roomRepository;
    @Mock private HotelRepository hotelRepository;
    @Mock private RoomMapper roomMapper;
    @InjectMocks private RoomServiceImpl roomService;

    private Room room;
    private RoomResponse response;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = Hotel.builder().id(1L).build();
        room = Room.builder().id(1L).hotel(hotel).roomNumber("101").available(true).build();
        response = RoomResponse.builder().id(1L).roomNumber("101").build();
    }

    @Test
    void createGetUpdateDeleteRoom() {
        RoomCreateRequest create = RoomCreateRequest.builder().hotelId(1L).roomNumber("101").build();
        RoomUpdateRequest update = RoomUpdateRequest.builder().roomNumber("102").build();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomRepository.findByHotelIdAndRoomNumber(1L, "101")).thenReturn(Optional.empty());
        when(roomMapper.toEntity(create)).thenReturn(room);
        when(roomRepository.save(room)).thenReturn(room);
        when(roomMapper.toResponse(room)).thenReturn(response);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.findByHotelIdAndRoomNumber(1L, "102")).thenReturn(Optional.empty());

        assertThat(roomService.createRoom(create).getId()).isEqualTo(1L);
        assertThat(roomService.getRoomById(1L).getId()).isEqualTo(1L);
        assertThat(roomService.updateRoom(1L, update).getId()).isEqualTo(1L);
        roomService.deleteRoom(1L);
        verify(roomRepository).delete(room);
    }

    @Test
    void roomQueriesAndAvailability() {
        var pageable = PageRequest.of(0, 10);
        when(roomRepository.findByHotelId(1L, pageable)).thenReturn(new PageImpl<>(List.of(room), pageable, 1));
        when(roomRepository.findByHotelIdAndAvailableTrue(1L, pageable)).thenReturn(new PageImpl<>(List.of(room), pageable, 1));
        when(roomRepository.findByRoomTypeAndAvailableTrue(RoomType.STANDARD, pageable)).thenReturn(new PageImpl<>(List.of(room), pageable, 1));
        when(roomRepository.findByPriceRange(BigDecimal.ONE, BigDecimal.TEN, pageable)).thenReturn(new PageImpl<>(List.of(room), pageable, 1));
        when(roomRepository.findByCapacityGreaterThanEqualAndAvailableTrue(2, pageable)).thenReturn(new PageImpl<>(List.of(room), pageable, 1));
        when(roomRepository.findAvailableRoomsForDates(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).thenReturn(List.of(room));
        when(roomRepository.findAvailableRoomsWithFilters(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), 2, BigDecimal.TEN)).thenReturn(List.of(room));
        when(roomMapper.toResponseList(any())).thenReturn(List.of(response));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.isRoomAvailableForDates(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).thenReturn(true);

        assertThat(roomService.getRoomsByHotel(1L, pageable).getContent()).hasSize(1);
        assertThat(roomService.getAvailableRoomsByHotel(1L, pageable).getContent()).hasSize(1);
        assertThat(roomService.getRoomsByType(RoomType.STANDARD, pageable).getContent()).hasSize(1);
        assertThat(roomService.getRoomsByPriceRange(BigDecimal.ONE, BigDecimal.TEN, pageable).getContent()).hasSize(1);
        assertThat(roomService.getRoomsByMinCapacity(2, pageable).getContent()).hasSize(1);
        assertThat(roomService.findAvailableRoomsForDates(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).hasSize(1);
        assertThat(roomService.findAvailableRoomsWithFilters(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), 2, BigDecimal.TEN)).hasSize(1);
        assertThat(roomService.isRoomAvailable(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).isTrue();
    }

    @Test
    void availabilityAndValidationFailures() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        room.setAvailable(false);
        assertThat(roomService.isRoomAvailable(1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))).isFalse();

        assertThatThrownBy(() -> roomService.findAvailableRoomsForDates(1L, null, LocalDate.now().plusDays(1)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> roomService.findAvailableRoomsForDates(1L, LocalDate.now().plusDays(2), LocalDate.now().plusDays(2)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void duplicateAndNotFoundCases() {
        RoomCreateRequest create = RoomCreateRequest.builder().hotelId(1L).roomNumber("101").build();
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomRepository.findByHotelIdAndRoomNumber(1L, "101")).thenReturn(Optional.of(room));
        assertThatThrownBy(() -> roomService.createRoom(create)).isInstanceOf(DuplicateResourceException.class);

        when(roomRepository.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> roomService.getRoomById(9L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
