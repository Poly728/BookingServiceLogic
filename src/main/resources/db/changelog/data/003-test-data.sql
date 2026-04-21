--liquibase formatted sql

--changeset author:booking-service-test-1 context:test
-- Test data for unit and integration tests

-- Test users
INSERT INTO users (username, email, password, first_name, last_name, phone, role, enabled)
VALUES
    ('testadmin', 'testadmin@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjqQBrkHx5l8.p1wF.5LqWBnrqJK.W', 'Test', 'Admin', '+1000000001', 'ADMIN', true),
    ('testuser', 'testuser@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjqQBrkHx5l8.p1wF.5LqWBnrqJK.W', 'Test', 'User', '+1000000002', 'USER', true),
    ('testowner', 'testowner@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjqQBrkHx5l8.p1wF.5LqWBnrqJK.W', 'Test', 'Owner', '+1000000003', 'OWNER', true);

--changeset author:booking-service-test-2 context:test
-- Test amenities
INSERT INTO amenities (name, description, icon) VALUES
    ('Test WiFi', 'Test wireless internet', 'wifi'),
    ('Test Parking', 'Test parking', 'parking'),
    ('Test Pool', 'Test pool access', 'pool');

--changeset author:booking-service-test-3 context:test
-- Test hotels
INSERT INTO hotels (name, description, address, city, country, zip_code, phone, email, rating, star_rating, active) VALUES
    ('Test Hotel 1', 'Test hotel description 1', '1 Test Street', 'Test City', 'Test Country', '00001', '+1111111111', 'test1@hotel.com', 4.0, 4, true),
    ('Test Hotel 2', 'Test hotel description 2', '2 Test Street', 'Test City', 'Test Country', '00002', '+2222222222', 'test2@hotel.com', 4.5, 5, true);

--changeset author:booking-service-test-4 context:test
-- Test hotel amenities
INSERT INTO hotel_amenities (hotel_id, amenity_id) VALUES
    (1, 1), (1, 2),
    (2, 1), (2, 2), (2, 3);

--changeset author:booking-service-test-5 context:test
-- Test rooms
INSERT INTO rooms (hotel_id, room_number, room_type, description, price_per_night, capacity, bed_count, area_sqm, available) VALUES
    (1, 'T101', 'STANDARD', 'Test standard room', 100.00, 2, 1, 20.0, true),
    (1, 'T102', 'DELUXE', 'Test deluxe room', 200.00, 2, 1, 30.0, true),
    (2, 'T201', 'SUITE', 'Test suite', 300.00, 4, 2, 50.0, true),
    (2, 'T202', 'STANDARD', 'Test standard room 2', 150.00, 2, 1, 25.0, false);

--changeset author:booking-service-test-6 context:test
-- Test bookings
INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, guests_count, total_price, status) VALUES
    (2, 1, CURRENT_DATE + 1, CURRENT_DATE + 3, 2, 200.00, 'CONFIRMED'),
    (2, 3, CURRENT_DATE + 10, CURRENT_DATE + 12, 3, 600.00, 'PENDING');

--changeset author:booking-service-test-7 context:test
-- Test reviews
INSERT INTO reviews (user_id, hotel_id, booking_id, rating, title, comment) VALUES
    (2, 1, 1, 5, 'Test Review', 'This is a test review comment');
