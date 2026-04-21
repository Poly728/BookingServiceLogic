package org.example.bookingservicelogic.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.RoomCreateRequest;
import org.example.bookingservicelogic.dto.request.RoomUpdateRequest;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.dto.response.RoomResponse;
import org.example.bookingservicelogic.entity.Hotel;
import org.example.bookingservicelogic.entity.Room;
import org.example.bookingservicelogic.entity.enums.RoomType;
import org.example.bookingservicelogic.exception.DuplicateResourceException;
import org.example.bookingservicelogic.exception.ResourceNotFoundException;
import org.example.bookingservicelogic.mapper.RoomMapper;
import org.example.bookingservicelogic.repository.HotelRepository;
import org.example.bookingservicelogic.repository.RoomRepository;
import org.example.bookingservicelogic.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of RoomService interface.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional
    public RoomResponse createRoom(RoomCreateRequest request) {
        log.debug("Creating new room for hotel ID: {}", request.getHotelId());

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", request.getHotelId()));

        if (roomRepository.findByHotelIdAndRoomNumber(request.getHotelId(), request.getRoomNumber()).isPresent()) {
            throw new DuplicateResourceException("Room", "roomNumber", request.getRoomNumber());
        }

        Room room = roomMapper.toEntity(request);
        room.setHotel(hotel);

        Room savedRoom = roomRepository.save(room);
        log.info("Created new room with ID: {}", savedRoom.getId());

        return roomMapper.toResponse(savedRoom);
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        log.debug("Fetching room by ID: {}", id);
        Room room = findRoomById(id);
        return roomMapper.toResponse(room);
    }

    @Override
    public PageResponse<RoomResponse> getRoomsByHotel(Long hotelId, Pageable pageable) {
        log.debug("Fetching rooms for hotel ID: {}", hotelId);
        Page<Room> rooms = roomRepository.findByHotelId(hotelId, pageable);
        List<RoomResponse> content = roomMapper.toResponseList(rooms.getContent());
        return PageResponse.of(rooms, content);
    }

    @Override
    public PageResponse<RoomResponse> getAvailableRoomsByHotel(Long hotelId, Pageable pageable) {
        log.debug("Fetching available rooms for hotel ID: {}", hotelId);
        Page<Room> rooms = roomRepository.findByHotelIdAndAvailableTrue(hotelId, pageable);
        List<RoomResponse> content = roomMapper.toResponseList(rooms.getContent());
        return PageResponse.of(rooms, content);
    }

    @Override
    public PageResponse<RoomResponse> getRoomsByType(RoomType roomType, Pageable pageable) {
        log.debug("Fetching rooms by type: {}", roomType);
        Page<Room> rooms = roomRepository.findByRoomTypeAndAvailableTrue(roomType, pageable);
        List<RoomResponse> content = roomMapper.toResponseList(rooms.getContent());
        return PageResponse.of(rooms, content);
    }

    @Override
    public PageResponse<RoomResponse> getRoomsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("Fetching rooms by price range: {} - {}", minPrice, maxPrice);
        Page<Room> rooms = roomRepository.findByPriceRange(minPrice, maxPrice, pageable);
        List<RoomResponse> content = roomMapper.toResponseList(rooms.getContent());
        return PageResponse.of(rooms, content);
    }

    @Override
    public PageResponse<RoomResponse> getRoomsByMinCapacity(Integer capacity, Pageable pageable) {
        log.debug("Fetching rooms with min capacity: {}", capacity);
        Page<Room> rooms = roomRepository.findByCapacityGreaterThanEqualAndAvailableTrue(capacity, pageable);
        List<RoomResponse> content = roomMapper.toResponseList(rooms.getContent());
        return PageResponse.of(rooms, content);
    }

    @Override
    public List<RoomResponse> findAvailableRoomsForDates(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        log.debug("Finding available rooms for hotel {} from {} to {}", hotelId, checkIn, checkOut);
        validateDates(checkIn, checkOut);
        List<Room> rooms = roomRepository.findAvailableRoomsForDates(hotelId, checkIn, checkOut);
        return roomMapper.toResponseList(rooms);
    }

    @Override
    public List<RoomResponse> findAvailableRoomsWithFilters(Long hotelId, LocalDate checkIn, LocalDate checkOut,
                                                            Integer minCapacity, BigDecimal maxPrice) {
        log.debug("Finding available rooms with filters for hotel {}", hotelId);
        validateDates(checkIn, checkOut);
        List<Room> rooms = roomRepository.findAvailableRoomsWithFilters(
                hotelId, checkIn, checkOut, minCapacity, maxPrice);
        return roomMapper.toResponseList(rooms);
    }

    @Override
    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        log.debug("Checking availability for room {} from {} to {}", roomId, checkIn, checkOut);
        validateDates(checkIn, checkOut);
        Room room = findRoomById(roomId);
        if (!room.getAvailable()) {
            return false;
        }
        return roomRepository.isRoomAvailableForDates(roomId, checkIn, checkOut);
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, RoomUpdateRequest request) {
        log.debug("Updating room with ID: {}", id);
        Room room = findRoomById(id);

        if (request.getRoomNumber() != null && !request.getRoomNumber().equals(room.getRoomNumber())) {
            if (roomRepository.findByHotelIdAndRoomNumber(room.getHotel().getId(), request.getRoomNumber()).isPresent()) {
                throw new DuplicateResourceException("Room", "roomNumber", request.getRoomNumber());
            }
        }

        roomMapper.updateEntityFromRequest(request, room);
        Room updatedRoom = roomRepository.save(room);
        log.info("Updated room with ID: {}", id);

        return roomMapper.toResponse(updatedRoom);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        log.debug("Deleting room with ID: {}", id);
        Room room = findRoomById(id);
        roomRepository.delete(room);
        log.info("Deleted room with ID: {}", id);
    }

    @Override
    @Transactional
    public RoomResponse setRoomAvailability(Long id, boolean available) {
        log.debug("Setting room {} availability to {}", id, available);
        Room room = findRoomById(id);
        room.setAvailable(available);
        Room updatedRoom = roomRepository.save(room);
        log.info("Set room {} availability to {}", id, available);
        return roomMapper.toResponse(updatedRoom);
    }

    private Room findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }
}
