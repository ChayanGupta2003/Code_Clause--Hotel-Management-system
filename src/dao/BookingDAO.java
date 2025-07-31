package dao;

import db.DBConnection;
import model.Booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingDAO {

    public void saveBooking(Booking booking) throws SQLException {
        Connection con = DBConnection.getConnection();

        // Insert booking into DB
        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO bookings (customer_id, room_id, check_in_date, check_out_date, guests, comment, total_price) VALUES (?, ?, ?, ?, ?, ?, ?)"
        );

        ps.setInt(1, booking.getCustomerId());
        ps.setString(2, booking.getRoomId());
        ps.setDate(3, new java.sql.Date(booking.getCheckInDate().getTime()));  // Correct method
        ps.setDate(4, new java.sql.Date(booking.getCheckInDate().getTime())); // Correct method
        ps.setInt(5, booking.getGuests());
        ps.setString(6, booking.getComment());
        ps.setDouble(7, booking.getTotalPrice()); // Correct method

        ps.executeUpdate();

        // Mark the room as unavailable
        PreparedStatement updateRoom = con.prepareStatement(
            "UPDATE rooms SET is_available = FALSE WHERE room_id = ?"
        );
        updateRoom.setString(1, booking.getRoomId());
        updateRoom.executeUpdate();
    }
    public int getLatestBookingIdForCustomer(int customerId) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT booking_id FROM bookings WHERE customer_id = ? ORDER BY booking_id DESC LIMIT 1"
            );
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}
