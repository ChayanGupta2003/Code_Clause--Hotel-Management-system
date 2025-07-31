use hotel_booking;
SELECT 
    c.name AS customer_name,
    c.email AS customer_email,
    b.room_id,
    b.guests,
    b.check_in_date,
    b.check_out_date,
    b.total_price,
    b.booking_date
FROM 
    bookings b
JOIN 
    customers c ON b.customer_id = c.customer_id
ORDER BY 
    b.booking_date DESC;


select * from rooms;
select * from customers;
select * from bookings;


update rooms
set description="Luxurious bedroom and living room, Panoramic city or ocean view, Private balcony, Large LED TV, Premium linens and furnishings, Dedicated work area, Room service 24/7, Free breakfast included"
where room_type="Suite";
