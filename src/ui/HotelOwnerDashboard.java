package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.sql.*;
import java.util.Vector;

public class HotelOwnerDashboard extends JFrame {

    JTable bookingsTable, roomsTable, customersTable;
    DefaultTableModel bookingsModel, roomsModel, customersModel;

    public HotelOwnerDashboard() {
        setTitle("\uD83C\uDFE8 Hotel Owner Dashboard");
        setSize(1100, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // ========== BOOKINGS TAB ==========
        bookingsModel = new DefaultTableModel();
        bookingsTable = new JTable(bookingsModel);
        JScrollPane bookingsPane = new JScrollPane(bookingsTable);

        bookingsModel.setColumnIdentifiers(new Object[]{
                "Booking ID", "Name", "Email", "Room ID", "Guests",
                "Check-In", "Check-Out", "Total Price", "Booking Date"
        });

        loadBookings();

        JPanel bookingPanel = new JPanel(new BorderLayout());

        // Top panel: filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField fromDateField = new JTextField("2025-08-01", 10);
        JTextField toDateField = new JTextField("2025-08-31", 10);
        JButton filterButton = new JButton("\uD83D\uDCC5 Filter");
        filterPanel.add(new JLabel("From:"));
        filterPanel.add(fromDateField);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(toDateField);
        filterPanel.add(filterButton);

        filterButton.addActionListener(e -> {
            String from = fromDateField.getText();
            String to = toDateField.getText();
            filterBookingsByDate(from, to);
        });

        // Bottom panel: actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBookingBtn = new JButton("\u274C Cancel Booking");
        JButton exportBtn = new JButton("\uD83D\uDCC4 Export to CSV");
        actionPanel.add(cancelBookingBtn);
        actionPanel.add(exportBtn);

        cancelBookingBtn.addActionListener(e -> cancelSelectedBooking());
        exportBtn.addActionListener(e -> exportBookingsToCSV());

        bookingPanel.add(filterPanel, BorderLayout.NORTH);
        bookingPanel.add(bookingsPane, BorderLayout.CENTER);
        bookingPanel.add(actionPanel, BorderLayout.SOUTH);

        tabs.add("\uD83D\uDCCB Bookings", bookingPanel);

        // ========== ROOMS TAB ==========
        roomsModel = new DefaultTableModel();
        roomsTable = new JTable(roomsModel);
        JScrollPane roomsPane = new JScrollPane(roomsTable);

        roomsModel.setColumnIdentifiers(new Object[]{
            "Room ID", "Floor", "Type", "Price", "Available", "Status"
        });

        loadRooms();

        JPanel roomPanel = new JPanel(new BorderLayout());
        roomPanel.add(roomsPane, BorderLayout.CENTER);

        JPanel roomBtnPanel = new JPanel();
        JButton addRoomBtn = new JButton("➕ Add Room");
        JButton editRoomBtn = new JButton("🖋️ Edit Room");
        JButton setStatusBtn = new JButton("🧽 Set Cleaning Status");
        JButton toggleAvailabilityBtn = new JButton("\uD83D\uDD01 Toggle Availability");

        roomBtnPanel.add(addRoomBtn);
        roomBtnPanel.add(editRoomBtn);
        roomBtnPanel.add(setStatusBtn);
        roomBtnPanel.add(toggleAvailabilityBtn);

        roomPanel.add(roomBtnPanel, BorderLayout.SOUTH);

        // Action Listeners for Room Management
        addRoomBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Feature: Add Room"));
        editRoomBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Feature: Edit Room Details"));
        setStatusBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Feature: Set Cleaning Status"));
        toggleAvailabilityBtn.addActionListener(e -> toggleRoomAvailability());

        tabs.add("\uD83D\uDECC Rooms", roomPanel);

        // ========== CUSTOMERS TAB ==========
        customersModel = new DefaultTableModel();
        customersTable = new JTable(customersModel);
        JScrollPane customerPane = new JScrollPane(customersTable);

        customersModel.setColumnIdentifiers(new Object[]{
                "Customer ID", "Name", "Email", "Age", "Gender"
        });

        loadCustomers();

        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.add(customerPane, BorderLayout.CENTER);

        tabs.add("\uD83D\uDC65 Customers", customerPanel);

        add(tabs);
        setVisible(true);
    }

    private void loadBookings() {
        bookingsModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            String sql = """
                    SELECT b.booking_id, c.name, c.email, b.room_id, b.guests,
                           b.check_in_date, b.check_out_date, b.total_price, b.booking_date
                    FROM bookings b
                    JOIN customers c ON b.customer_id = c.customer_id
                    ORDER BY b.booking_date DESC
                    """;

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                bookingsModel.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("room_id"),
                        rs.getInt("guests"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getDouble("total_price"),
                        rs.getDate("booking_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRooms() {
        roomsModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM rooms ORDER BY room_id";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                roomsModel.addRow(new Object[]{
                    rs.getString("room_id"),
                    rs.getString("floor"),
                    rs.getString("room_type"),
                    rs.getDouble("price"),
                    rs.getBoolean("is_available") ? "Yes" : "No",
                    rs.getString("status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomers() {
        customersModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM customers ORDER BY customer_id";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                customersModel.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("age"),
                        rs.getString("gender")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void toggleRoomAvailability() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a room to toggle.");
            return;
        }

        String roomId = (String) roomsModel.getValueAt(selectedRow, 0);
        String availability = (String) roomsModel.getValueAt(selectedRow, 4);
        boolean newStatus = availability.equals("No");

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE rooms SET is_available = ? WHERE room_id = ?");
            ps.setBoolean(1, newStatus);
            ps.setString(2, roomId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Room availability updated.");
            loadRooms();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cancelSelectedBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking to cancel.");
            return;
        }

        int bookingId = (int) bookingsModel.getValueAt(selectedRow, 0);
        String roomId = (String) bookingsModel.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this booking?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM bookings WHERE booking_id = ?");
            ps.setInt(1, bookingId);
            ps.executeUpdate();

            PreparedStatement ps2 = con.prepareStatement("UPDATE rooms SET is_available = TRUE WHERE room_id = ?");
            ps2.setString(1, roomId);
            ps2.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Booking cancelled.");
            loadBookings();
            loadRooms();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void exportBookingsToCSV() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Bookings CSV");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                try (FileWriter fw = new FileWriter(fileToSave)) {
                    for (int i = 0; i < bookingsModel.getColumnCount(); i++) {
                        fw.write(bookingsModel.getColumnName(i) + ",");
                    }
                    fw.write("\n");
                    for (int i = 0; i < bookingsModel.getRowCount(); i++) {
                        for (int j = 0; j < bookingsModel.getColumnCount(); j++) {
                            fw.write(bookingsModel.getValueAt(i, j).toString() + ",");
                        }
                        fw.write("\n");
                    }
                    JOptionPane.showMessageDialog(this, "✅ Exported Successfully!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterBookingsByDate(String from, String to) {
        bookingsModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("""
                SELECT b.booking_id, c.name, c.email, b.room_id, b.guests,
                       b.check_in_date, b.check_out_date, b.total_price, b.booking_date
                FROM bookings b
                JOIN customers c ON b.customer_id = c.customer_id
                WHERE b.booking_date BETWEEN ? AND ?
                ORDER BY b.booking_date DESC
            """);
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bookingsModel.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("room_id"),
                        rs.getInt("guests"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getDouble("total_price"),
                        rs.getDate("booking_date")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String email = JOptionPane.showInputDialog("Enter Owner Email:");
            String password = JOptionPane.showInputDialog("Enter Password:");

            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM owners WHERE email = ? AND password = ?");
                ps.setString(1, email);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    new HotelOwnerDashboard();
                } else {
                    JOptionPane.showMessageDialog(null, "Access Denied. Invalid Credentials.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}