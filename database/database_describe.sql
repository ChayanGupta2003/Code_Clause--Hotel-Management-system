create database hotel_booking;
use hotel_booking;
drop database hotel_booking;
CREATE TABLE rooms (
    room_id VARCHAR(10) PRIMARY KEY,     -- e.g. '1A', '10C'
    floor CHAR(1),                       -- 'A', 'B', or 'C'
    room_number INT,                    -- 1 to 10
    room_type ENUM('Basic', 'Deluxe', 'Suite'),
    price DECIMAL(8,2),
    is_available BOOLEAN DEFAULT TRUE,
    
    image_url VARCHAR(255),              -- Link or file path to image
    description TEXT                     -- Description of room features
);

CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    age INT,
    gender ENUM('Male', 'Female', 'Other'),
    password VARCHAR(255) NOT NULL
);
CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    room_id VARCHAR(10),
    check_in_date DATE,
    check_out_date DATE,
    guests INT,
    comment TEXT,
    total_price DECIMAL(10,2),
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);



INSERT INTO customers (name, email, age, gender, password) VALUES
('Rahul Sharma', 'rahul@gmail.com', 28, 'Male', '123'),
('Priya Mehta', 'priya@gmail.com', 25, 'Female', '456'),
('Amit Verma', 'amit@gmail.com', 32, 'Male', '789'),
('Sneha Roy', 'sneha@gmail.com', 29, 'Female', '321'),
('Karan Patel', 'karan@gmail.com', 30, 'Male', '111'),
('Neha Singh', 'neha@gmail.com', 27, 'Female', '222'),
('Ravi Joshi', 'ravi@gmail.com', 35, 'Male', '333'),
('Anjali Das', 'anjali@gmail.com', 24, 'Female', '444'),
('Vikram Rao', 'vikram@gmail.com', 31, 'Male', '555'),
('Pooja Nair', 'pooja@gmail.com', 26, 'Female', '666');


INSERT INTO rooms (room_id, floor, room_number, room_type, price, is_available, image_url, description)
VALUES
('1A', 'A', 1, 'Deluxe', 2000.00, TRUE, 'images/deluxe.jpg', 'Spacious deluxe room with a queen-size bed, TV, and balcony.'),
('2A', 'A', 2, 'Basic', 1000.00, TRUE, 'images/basic.jpg', 'Cozy basic room with one bed and free Wi-Fi.'),
('3A', 'A', 3, 'Suite', 3000.00, TRUE, 'images/suite.jpg', 'Luxury suite with living area, king-size bed, and city view.'),
('4A', 'A', 4, 'Basic', 1000.00, TRUE, 'images/basic.jpg', 'Compact room perfect for solo travelers.'),
('5A', 'A', 5, 'Suite', 3000.00, TRUE, 'images/suite.jpg', 'Premium suite with mini bar and panoramic views.'),
('6A', 'A', 6, 'Deluxe', 2000.00, TRUE, 'images/deluxe.jpg', 'Elegant deluxe room with workstation and large TV.'),
('7A', 'A', 7, 'Basic', 1000.00, TRUE, 'images/basic.jpg', 'Affordable comfort with all essentials.'),
('8A', 'A', 8, 'Suite', 3000.00, TRUE, 'images/suite.jpg', 'Executive suite ideal for business travelers.'),
('9A', 'A', 9, 'Deluxe', 2000.00, TRUE, 'images/deluxe.jpg', 'Relaxing deluxe room with lounge area.'),
('10A', 'A', 10, 'Basic', 1000.00, TRUE, 'images/basic.jpg', 'Standard room with attached bathroom and TV.'),

('1B', 'B', 1, 'Suite', 3000.00, TRUE, 'images/suite.jpg', 'Spacious suite with deluxe amenities.'),
('2B', 'B', 2, 'Deluxe', 2000.00, TRUE, 'images/deluxe.jpg', 'Family-friendly room with extra space.'),
('3B', 'B', 3, 'Basic', 1000.00, TRUE, 'images/basic.jpg', 'Economy room with cozy furnishings.'),
('4B', 'B', 4, 'Deluxe', 2000.00, TRUE, 'images/deluxe.jpg', 'Modern deluxe room with comfortable sofa.'),
('5B', 'B', 5, 'Suite', 3000.00, TRUE, 'images/suite.jpg', 'Luxury suite with separate lounge and workspace.'),
('6B', 'B', 6, 'Basic', 1000.00, TRUE, 'images/basic.jpg', 'Simple room with bed, table, and fan.'),
('7B', 'B', 7, 'Deluxe', 2000.00, TRUE, 'images/deluxe.jpg', 'Stylish room with elegant interiors.'),
('8B', 'B', 8, 'Suite', 3000.00, TRUE, 'images/suite.jpg', 'Top-floor suite with private terrace.'),
('9B', 'B', 9, 'Basic', 1000.00, TRUE, 'images/basic.jpg', 'Minimalistic room for budget travelers.'),
('10B', 'B', 10, 'Deluxe', 2000.00, TRUE, 'images/deluxe.jpg', 'Comfortable room with all modern amenities.'),

('1C', 'C', 1, 'Basic', 1000.00, TRUE, 'images/basic.jpg', 'Ground floor room ideal for elderly guests.'),
('2C', 'C', 2, 'Suite', 3000.00, TRUE, 'images/suite.jpg', 'Deluxe suite with elegant interiors and bath tub.'),
('3C', 'C', 3, 'Deluxe', 2000.00, TRUE, 'images/deluxe.jpg', 'Smart room with smart lighting and workspace.'),
('4C', 'C', 4, 'Basic', 1000.00, TRUE, 'images/basic.jpg', 'Quiet and clean room near the reception.'),
('5C', 'C', 5, 'Suite', 3000.00, TRUE, 'images/suite.jpg', 'Executive suite with extra amenities.'),
('6C', 'C', 6, 'Deluxe', 2000.00, TRUE, 'images/deluxe.jpg', 'Comfortable room with a garden view.'),
('7C', 'C', 7, 'Basic', 1000.00, TRUE, 'images/basic.jpg', 'Neat and clean room perfect for short stays.'),
('8C', 'C', 8, 'Deluxe', 2000.00, TRUE, 'images/deluxe.jpg', 'Deluxe room with a city-facing window.'),
('9C', 'C', 9, 'Suite', 3000.00, TRUE, 'images/suite.jpg', 'Presidential suite with premium facilities.'),
('10C', 'C', 10, 'Basic', 1000.00, TRUE, 'images/basic.jpg', 'Standard room with attached toilet and cupboard.');
ALTER TABLE rooms ADD COLUMN status VARCHAR(20) DEFAULT 'clean';
ALTER TABLE customers MODIFY gender VARCHAR(10);
SET SQL_SAFE_UPDATES = 0;