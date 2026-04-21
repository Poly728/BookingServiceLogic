--liquibase formatted sql

--changeset author:booking-service-data-1
-- Insert default users (all passwords: pass123 - BCrypt encoded)
-- Hash generated with: new BCryptPasswordEncoder().encode("pass123")
INSERT INTO users (username, email, password, first_name, last_name, phone, role, enabled)
VALUES
    ('admin', 'admin@booking.com', '$2a$10$Zg86ZPYViz6AfhWGd2gK0O0u6sXv8kHcJRiKTrFsp3UZDUU/bMWey', 'Admin', 'System', '+1234567890', 'ADMIN', true),
    ('user1', 'user1@example.com', '$2a$10$Zg86ZPYViz6AfhWGd2gK0O0u6sXv8kHcJRiKTrFsp3UZDUU/bMWey', 'John', 'Doe', '+1234567891', 'USER', true),
    ('user2', 'user2@example.com', '$2a$10$Zg86ZPYViz6AfhWGd2gK0O0u6sXv8kHcJRiKTrFsp3UZDUU/bMWey', 'Jane', 'Smith', '+1234567892', 'USER', true),
    ('owner1', 'owner1@example.com', '$2a$10$Zg86ZPYViz6AfhWGd2gK0O0u6sXv8kHcJRiKTrFsp3UZDUU/bMWey', 'Mike', 'Johnson', '+1234567893', 'OWNER', true);

--changeset author:booking-service-data-2
-- Insert amenities
INSERT INTO amenities (name, description, icon) VALUES
    ('WiFi', 'Free high-speed wireless internet', 'wifi'),
    ('Parking', 'Free parking on premises', 'parking'),
    ('Pool', 'Swimming pool access', 'pool'),
    ('Gym', 'Fitness center access', 'fitness'),
    ('Spa', 'Spa and wellness services', 'spa'),
    ('Restaurant', 'On-site restaurant', 'restaurant'),
    ('Air Conditioning', 'Climate control in all rooms', 'ac'),
    ('Pet Friendly', 'Pets are welcome', 'pets'),
    ('Kitchen', 'Fully equipped kitchen', 'kitchen'),
    ('Washer', 'Washing machine available', 'washer'),
    ('TV', 'Flat-screen TV', 'tv'),
    ('Balcony', 'Private balcony', 'balcony');

--changeset author:booking-service-data-3
-- Insert hotels/properties
INSERT INTO hotels (name, description, address, city, country, zip_code, phone, email, rating, star_rating, image_url, active) VALUES
    ('Grand Hotel Palace', 'Luxury 5-star hotel in the heart of the city with stunning views and world-class amenities.', '123 Main Street', 'New York', 'USA', '10001', '+1-212-555-0100', 'info@grandpalace.com', 4.8, 5, 'https://foto.hrsstatic.com/fotos/0/2/800/458/80/000000/http%3A%2F%2Ffoto-origin.hrsstatic.com%2Ffoto%2FMTS%2F220043%2F220043_ha_50016063_86.jpg/6pe8wwg334RP9BjAbXFvDA%3D%3D/1500%2C1163/6/Grand_Hotel_Palace-Thessaloniki-Hotel_outdoor_area-2-220043.jpg', true),
    ('Seaside Resort', 'Beautiful beachfront resort with private beach access and tropical gardens.', '456 Ocean Drive', 'Miami', 'USA', '33139', '+1-305-555-0200', 'info@seasideresort.com', 4.5, 4, 'https://resource.rentcafe.com/image/upload/x_0,y_0,w_2000,h_1234,c_crop/q_auto,f_auto,c_lfill,w_420,ar_1.05,g_auto/s3/3/1147478/sole%20at%20city%20center-48(1).jpg', true),
    ('Mountain Lodge', 'Cozy mountain retreat perfect for skiing and hiking adventures.', '789 Alpine Road', 'Denver', 'USA', '80201', '+1-303-555-0300', 'info@mountainlodge.com', 4.6, 4, 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/07/88/53/51/mountain-lodge.jpg?w=900&h=500&s=1', true),
    ('City Center Apartments', 'Modern apartments in downtown location, perfect for business travelers.', '321 Business Ave', 'Chicago', 'USA', '60601', '+1-312-555-0400', 'info@citycenter.com', 4.3, 3, 'https://images.trvl-media.com/lodging/2000000/1160000/1151900/1151863/61d24af3.jpg?impolicy=resizecrop&rw=575&rh=575&ra=fill', true),
    ('Cozy Beach House', 'Charming beach house with ocean views and direct beach access.', '555 Coastal Way', 'Los Angeles', 'USA', '90210', '+1-310-555-0500', 'info@cozybeach.com', 4.7, 4, 'https://i.ytimg.com/vi/Ql4w2oKxEH8/maxresdefault.jpg', true);

--changeset author:booking-service-data-4
-- Link hotels with amenities
INSERT INTO hotel_amenities (hotel_id, amenity_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 11),
    (2, 1), (2, 2), (2, 3), (2, 6), (2, 7), (2, 8), (2, 11), (2, 12),
    (3, 1), (3, 2), (3, 4), (3, 6), (3, 7), (3, 11),
    (4, 1), (4, 2), (4, 4), (4, 7), (4, 9), (4, 10), (4, 11),
    (5, 1), (5, 2), (5, 7), (5, 8), (5, 9), (5, 10), (5, 11), (5, 12);

--changeset author:booking-service-data-5
-- Insert rooms
INSERT INTO rooms (hotel_id, room_number, room_type, description, price_per_night, capacity, bed_count, area_sqm, available) VALUES
    (1, '101', 'STANDARD', 'Comfortable standard room with city view', 150.00, 2, 1, 25.0, true),
    (1, '102', 'STANDARD', 'Comfortable standard room with garden view', 150.00, 2, 1, 25.0, true),
    (1, '201', 'DELUXE', 'Spacious deluxe room with panoramic city view', 250.00, 2, 1, 35.0, true),
    (1, '301', 'SUITE', 'Luxurious suite with separate living area', 450.00, 4, 2, 60.0, true),
    (1, '401', 'PENTHOUSE', 'Exclusive penthouse with terrace and jacuzzi', 800.00, 4, 2, 100.0, true),
    (2, '101', 'STANDARD', 'Ocean view standard room', 180.00, 2, 1, 28.0, true),
    (2, '102', 'STANDARD', 'Garden view standard room', 160.00, 2, 1, 28.0, true),
    (2, '201', 'DELUXE', 'Beachfront deluxe room with balcony', 300.00, 2, 1, 40.0, true),
    (2, '301', 'SUITE', 'Ocean suite with private terrace', 500.00, 4, 2, 70.0, true),
    (3, '101', 'STANDARD', 'Mountain view room', 120.00, 2, 1, 22.0, true),
    (3, '102', 'STANDARD', 'Forest view room', 120.00, 2, 1, 22.0, true),
    (3, '201', 'DELUXE', 'Premium mountain view with fireplace', 200.00, 3, 2, 35.0, true),
    (3, '301', 'CABIN', 'Private cabin with kitchen', 280.00, 6, 3, 55.0, true),
    (4, 'A101', 'STUDIO', 'Modern studio apartment', 100.00, 2, 1, 30.0, true),
    (4, 'A102', 'STUDIO', 'Modern studio apartment', 100.00, 2, 1, 30.0, true),
    (4, 'B201', 'APARTMENT', 'One bedroom apartment', 150.00, 3, 1, 45.0, true),
    (4, 'B202', 'APARTMENT', 'One bedroom apartment', 150.00, 3, 1, 45.0, true),
    (4, 'C301', 'APARTMENT', 'Two bedroom apartment', 220.00, 5, 2, 65.0, true),
    (5, '1', 'HOUSE', 'Entire beach house - ground floor', 350.00, 6, 3, 120.0, true),
    (5, '2', 'HOUSE', 'Entire beach house - upper floor', 400.00, 8, 4, 150.0, true);

--changeset author:booking-service-data-6
-- Insert sample bookings
INSERT INTO bookings (user_id, room_id, check_in_date, check_out_date, guests_count, total_price, status, special_requests) VALUES
    (2, 1, CURRENT_DATE + 7, CURRENT_DATE + 10, 2, 450.00, 'CONFIRMED', 'Late check-in requested'),
    (2, 6, CURRENT_DATE + 30, CURRENT_DATE + 35, 2, 900.00, 'PENDING', 'Ocean view preferred'),
    (3, 10, CURRENT_DATE + 14, CURRENT_DATE + 17, 2, 360.00, 'CONFIRMED', null),
    (3, 14, CURRENT_DATE - 10, CURRENT_DATE - 7, 1, 300.00, 'COMPLETED', 'Early check-out');

--changeset author:booking-service-data-7
-- Insert sample reviews
INSERT INTO reviews (user_id, hotel_id, booking_id, rating, title, comment) VALUES
    (3, 4, 4, 4, 'Great location', 'The apartment was clean and well-equipped. Perfect for business travel. Would recommend!'),
    (2, 1, 1, 5, 'Amazing experience', 'Outstanding service and beautiful rooms. The staff was incredibly helpful. Will definitely return!');
