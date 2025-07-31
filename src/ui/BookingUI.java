package ui;

import com.toedter.calendar.JDateChooser;
import dao.BookingDAO;
import db.DBConnection;
import model.Booking;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

public class BookingUI extends JFrame {

 private int customer_id;
 private String name, email, gender;
 private int age;

 private JDateChooser checkInChooser, checkOutChooser;
 private JSpinner guestSpinner;
 private JTextArea commentArea;
 private JComboBox<String> roomTypeBox;
 private JComboBox<String> room_idBox;
 private JLabel priceLabel, pricePerDayLabel;
 private JTextPane descriptionPane;
 private JPanel imagePanel;

 private Map<String, RoomInfo> availableRooms = new HashMap<>();

 class RoomInfo {
     String room_id, floor;
     double price;
     String[] images = new String[3];
     String description;
 }

 public BookingUI(int customer_id) {
     this.customer_id = customer_id;
     fetchCustomerDetails();

     setTitle("Book a Room");
     setSize(1100, 650);
     setDefaultCloseOperation(EXIT_ON_CLOSE);
     setLocationRelativeTo(null);
     setLayout(new BorderLayout(10, 10));

     // ==== LEFT PANEL ====
     JPanel leftPanel = new JPanel();
     leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
     leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

     leftPanel.add(labelWithValue("Name:", name));
     leftPanel.add(Box.createVerticalStrut(8));
     leftPanel.add(labelWithValue("Email:", email));
     leftPanel.add(Box.createVerticalStrut(8));
     leftPanel.add(labelWithValue("Age:", String.valueOf(age)));
     leftPanel.add(Box.createVerticalStrut(8));
     leftPanel.add(labelWithValue("Gender:", gender));
     leftPanel.add(Box.createVerticalStrut(12));

     checkInChooser = new JDateChooser();
     checkInChooser.setDateFormatString("yyyy-MM-dd");

     checkOutChooser = new JDateChooser();
     checkOutChooser.setDateFormatString("yyyy-MM-dd");

     guestSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
     roomTypeBox = new JComboBox<>(new String[]{"Basic", "Deluxe", "Suite"});
     room_idBox = new JComboBox<>();

     leftPanel.add(labelWithInput("Check-In Date ", checkInChooser));
     leftPanel.add(Box.createVerticalStrut(8));
     leftPanel.add(labelWithInput("Check-Out Date ", checkOutChooser));
     leftPanel.add(Box.createVerticalStrut(8));
     leftPanel.add(labelWithInput("Guests ", guestSpinner));
     leftPanel.add(Box.createVerticalStrut(8));
     leftPanel.add(labelWithInput("Room Type ", roomTypeBox));
     leftPanel.add(Box.createVerticalStrut(8));
     leftPanel.add(labelWithInput("Available Rooms ", room_idBox));
     leftPanel.add(Box.createVerticalStrut(12));

     commentArea = new JTextArea(1, 20);
     commentArea.setLineWrap(true);
     commentArea.setWrapStyleWord(true);
     JScrollPane commentBox = new JScrollPane(commentArea);
     commentBox.setPreferredSize(new Dimension(220, 45));
     commentBox.setMaximumSize(new Dimension(220, 45));
     leftPanel.add(labelWithInput("Comments ", commentBox));
     leftPanel.add(Box.createVerticalStrut(12));

     JButton calcBtn = new JButton("Calculate Total");
     JButton bookBtn = new JButton("Book Room");
     JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
     buttonRow.add(calcBtn);
     buttonRow.add(bookBtn);
     leftPanel.add(buttonRow);

     leftPanel.add(Box.createVerticalGlue());

     // ==== RIGHT PANEL ====
     JPanel rightPanel = new JPanel();
     rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
     rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

     JButton checkBookingBtn = new JButton("🔍 Check My Booking");
     checkBookingBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
     checkBookingBtn.addActionListener(e -> new BookingStatusUI());
     rightPanel.add(checkBookingBtn);
     rightPanel.add(Box.createVerticalStrut(10));

     imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
     rightPanel.add(imagePanel);

     descriptionPane = new JTextPane();
     descriptionPane.setContentType("text/html");
     descriptionPane.setEditable(false);
     descriptionPane.setBackground(rightPanel.getBackground());
     descriptionPane.setBorder(BorderFactory.createTitledBorder("Room Description"));

     JScrollPane descScroll = new JScrollPane(descriptionPane);
     descScroll.setPreferredSize(new Dimension(280, 150));
     descScroll.setBorder(null);
     descScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
     rightPanel.add(descScroll);

     pricePerDayLabel = new JLabel("Room Price: ₹0 /day");
     pricePerDayLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
     pricePerDayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     rightPanel.add(Box.createVerticalStrut(10));
     rightPanel.add(pricePerDayLabel);

     priceLabel = new JLabel("Total Price: ₹0");
     priceLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
     priceLabel.setForeground(new Color(0, 102, 204));
     priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     rightPanel.add(Box.createVerticalStrut(5));
     rightPanel.add(priceLabel);

     // === Wrap Panels ===
     JPanel leftWrapper = new JPanel(new BorderLayout());
     leftWrapper.add(leftPanel, BorderLayout.NORTH);

     add(leftWrapper, BorderLayout.CENTER);
     add(rightPanel, BorderLayout.EAST);

     // === Events ===
     roomTypeBox.addActionListener(e -> loadAvailableRooms());
     room_idBox.addActionListener(e -> updateRoomDetails());
     calcBtn.addActionListener(e -> updateRoomDetails());
     bookBtn.addActionListener(e -> handleBooking());

     loadAvailableRooms();
     setVisible(true);
 }

 private JPanel labelWithValue(String label, String value) {
     JPanel panel = new JPanel(new BorderLayout());
     panel.add(new JLabel(label), BorderLayout.WEST);
     panel.add(new JLabel(value), BorderLayout.CENTER);
     panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
     return panel;
 }

 private JPanel labelWithInput(String label, JComponent field) {
     JPanel panel = new JPanel(new BorderLayout());
     panel.add(new JLabel(label), BorderLayout.WEST);
     panel.add(field, BorderLayout.CENTER);
     panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
     return panel;
 }

 private void fetchCustomerDetails() {
     try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM customers WHERE customer_id = ?");
         ps.setInt(1, customer_id);
         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
             name = rs.getString("name");
             email = rs.getString("email");
             age = rs.getInt("age");
             gender = rs.getString("gender");
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
 }

 private void loadAvailableRooms() {
     availableRooms.clear();
     room_idBox.removeAllItems();
     String selectedType = (String) roomTypeBox.getSelectedItem();

     try (Connection con = DBConnection.getConnection()) {
         PreparedStatement ps = con.prepareStatement("SELECT * FROM rooms WHERE is_available = TRUE AND room_type = ?");
         ps.setString(1, selectedType);
         ResultSet rs = ps.executeQuery();

         while (rs.next()) {
             String room_id = rs.getString("room_id");
             RoomInfo info = new RoomInfo();
             info.room_id = room_id;
             info.floor = rs.getString("floor");
             info.price = rs.getDouble("price");
             String base = rs.getString("image_url");
             info.images[0] = base;
             info.images[1] = base.replace(".jpg", "1.jpg");
             info.images[2] = base.replace(".jpg", "2.jpg");
             info.description = rs.getString("description");

             availableRooms.put(room_id, info);
             room_idBox.addItem(room_id);
         }

     } catch (SQLException e) {
         e.printStackTrace();
     }

     updateRoomDetails();
 }

 private void updateRoomDetails() {
     String selectedRoomId = (String) room_idBox.getSelectedItem();
     if (selectedRoomId == null) return;

     RoomInfo info = availableRooms.get(selectedRoomId);
     imagePanel.removeAll();

     for (String img : info.images) {
         ImageIcon icon = new ImageIcon(img);
         JLabel lbl = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(220, 160, Image.SCALE_SMOOTH)));
         imagePanel.add(lbl);
     }
     imagePanel.revalidate();
     imagePanel.repaint();

     String[] bullets = info.description.split(",");
     StringBuilder bulletHTML = new StringBuilder("<html><body><ul>");
     for (String point : bullets) {
         bulletHTML.append("<li>").append(point.trim()).append("</li>");
     }
     bulletHTML.append("</ul></body></html>");
     descriptionPane.setText(bulletHTML.toString());

     long days = calculateDays();
     double totalPrice = days * info.price;
     pricePerDayLabel.setText("Room Price: ₹" + info.price + " /day");
     priceLabel.setText("Total Price: ₹" + totalPrice);
 }

 private long calculateDays() {
     try {
         Date in = checkInChooser.getDate();
         Date out = checkOutChooser.getDate();
         if (in == null || out == null) return 1;
         long diff = out.getTime() - in.getTime();
         return Math.max(diff / (1000 * 60 * 60 * 24), 1);
     } catch (Exception e) {
         return 1;
     }
 }

 private void handleBooking() {
	    try {
	        Date in = checkInChooser.getDate();
	        Date out = checkOutChooser.getDate();

	        if (in == null || out == null) {
	            JOptionPane.showMessageDialog(this, "❌ Please select both check-in and check-out dates.");
	            return;
	        }
	        if (!out.after(in)) {
	            JOptionPane.showMessageDialog(this, "❌ Check-out date must be after check-in date.");
	            return;
	        }

	        String room_id = (String) room_idBox.getSelectedItem();
	        if (room_id == null || room_id.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "❌ No room available for the selected type.");
	            return;
	        }

	        int guests = (Integer) guestSpinner.getValue();
	        String comment = commentArea.getText().trim();
	        double totalPrice = Double.parseDouble(priceLabel.getText().replaceAll("[^\\d.]", ""));

	        Booking booking = new Booking(customer_id, room_id, in, out, guests, comment, totalPrice);
	        new BookingDAO().saveBooking(booking);

	        // Show success receipt in new window
	        JFrame receiptFrame = new JFrame("✅ Booking Confirmed");
	        receiptFrame.setSize(400, 320);
	        receiptFrame.setLocationRelativeTo(null);
	        receiptFrame.setLayout(new BorderLayout());

	        JTextArea receiptArea = new JTextArea();
	        receiptArea.setText(
	            "Booking Confirmed!\n\n" +
	            "Name: " + name +
	            "\nEmail: " + email +
	            "\nRoom ID: " + room_id +
	            "\nGuests: " + guests +
	            "\nCheck-in: " + new SimpleDateFormat("yyyy-MM-dd").format(in) +
	            "\nCheck-out: " + new SimpleDateFormat("yyyy-MM-dd").format(out) +
	            "\nTotal: ₹" + totalPrice +
	            "\nDate: " + new java.sql.Date(System.currentTimeMillis())
	        );
	        receiptArea.setEditable(false);
	        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

	        JButton payNowBtn = new JButton("💳 Pay Now");
	        payNowBtn.addActionListener(ev -> {
	            int bookingId = new BookingDAO().getLatestBookingIdForCustomer(customer_id);
	            new PaymentUI(bookingId, String.valueOf(totalPrice));
	            receiptFrame.dispose();
	        });

	        receiptFrame.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
	        receiptFrame.add(payNowBtn, BorderLayout.SOUTH);
	        receiptFrame.setVisible(true);

	        dispose(); // close the booking window

	    } catch (Exception e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(this, "❌ Booking failed: " + e.getMessage());
	    }
	}

}
