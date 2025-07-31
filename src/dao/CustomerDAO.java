package dao;

import db.DBConnection;
import model.Customer;

import java.sql.*;

public class CustomerDAO {

    public boolean emailExists(String email) throws SQLException {
        String query = "SELECT 1 FROM customers WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public boolean validateLogin(String email, String password) throws SQLException {
        String query = "SELECT * FROM customers WHERE email = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public int getCustomerIdByEmail(String email) throws SQLException {
        String query = "SELECT customer_id FROM customers WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("customer_id");
        }
        return -1;
    }

    public void registerCustomer(Customer customer) throws SQLException {
        String query = "INSERT INTO customers (name, email, age, gender, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setInt(3, customer.getAge());
            ps.setString(4, customer.getGender());
            ps.setString(5, customer.getPassword());
            ps.executeUpdate();
        }
    }
}
