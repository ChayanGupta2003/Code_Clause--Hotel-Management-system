package ui;

import com.razorpay.*;
import db.DBConnection;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookingStatusUI extends JFrame {

    JTextField emailField;
    JTable table;
    DefaultTableModel model;
    JButton payButton;

    public BookingStatusUI() {
        setTitle("\uD83D\uDD0D Check Your Booking");
        setSize(850, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel: Email input
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter your email:"));
        emailField = new JTextField(20);
        JButton checkBtn = new JButton("Check");
        inputPanel.add(emailField);
        inputPanel.add(checkBtn);
        add(inputPanel, BorderLayout.NORTH);

        // Table Setup
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "Booking ID", "Name", "Room ID", "Guests", "Check-In", "Check-Out", "Total Price", "Booking Date", "Status"
        });
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom Panel: Pay Now button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        payButton = new JButton("\uD83D\uDCB3 Pay Now");
        payButton.setEnabled(false);
        bottomPanel.add(payButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        checkBtn.addActionListener(e -> loadBookings());
        table.getSelectionModel().addListSelectionListener(e -> updatePayButtonState());
        payButton.addActionListener(e -> handlePayment());

        setVisible(true);
    }

    private void loadBookings() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email.");
            return;
        }

        model.setRowCount(0);
        payButton.setEnabled(false);

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("""
                SELECT b.booking_id, c.name, b.room_id, b.guests,
                       b.check_in_date, b.check_out_date, b.total_price, b.booking_date, b.paid
                FROM bookings b
                JOIN customers c ON b.customer_id = c.customer_id
                WHERE c.email = ?
                ORDER BY b.booking_date DESC
            """);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                model.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("name"),
                        rs.getString("room_id"),
                        rs.getInt("guests"),
                        rs.getDate("check_in_date"),
                        rs.getDate("check_out_date"),
                        rs.getDouble("total_price"),
                        rs.getDate("booking_date"),
                        rs.getBoolean("paid") ? "Paid" : "Unpaid"
                });
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No bookings found for this email.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePayButtonState() {
        int row = table.getSelectedRow();
        if (row == -1) {
            payButton.setEnabled(false);
            return;
        }

        String status = (String) table.getValueAt(row, 8);
        payButton.setEnabled(status.equals("Unpaid"));
    }

    private void handlePayment() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        int bookingId = (int) table.getValueAt(row, 0);
        String price = table.getValueAt(row, 6).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Proceed to pay ₹" + price + " using Razorpay?",
                "Confirm Payment", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            initiateRazorpayPayment(price, bookingId, row);
        }
    }

    private void initiateRazorpayPayment(String amountInRupees, int bookingId, int row) {
        try {
            RazorpayClient razorpay = new RazorpayClient("YOUR_API_KEY", "YOUR_API_SECRET");

            JSONObject orderRequest = new JSONObject();
            int amountPaise = (int)(Double.parseDouble(amountInRupees) * 100); // convert to paise
            orderRequest.put("amount", amountPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt_" + bookingId);
            orderRequest.put("payment_capture", true);

            Order order = razorpay.orders.create(orderRequest);
            String orderId = order.get("id");

            JOptionPane.showMessageDialog(this,
                    "✅ Order Created: " + orderId +
                            "\nNow complete the payment using Razorpay Checkout.\n(This UI only simulates backend part.)");

            // Update payment status in DB
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE bookings SET paid = TRUE WHERE booking_id = ?"
                );
                ps.setInt(1, bookingId);
                int updated = ps.executeUpdate();

                if (updated > 0) {
                    table.setValueAt("Paid", row, 8);
                    payButton.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "✅ Payment marked as complete in system.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Razorpay Error: " + e.getMessage());
        }
    }
}
