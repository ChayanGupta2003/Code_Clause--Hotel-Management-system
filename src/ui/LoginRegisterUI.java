package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginRegisterUI extends JFrame {

    public LoginRegisterUI() {
        setTitle("User Login / Register");
        setSize(420, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Login", createLoginPanel());
        tabs.addTab("Register", createRegisterPanel());

        add(tabs);
        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGbc();

        JTextField emailField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JButton loginBtn = new JButton("Login");

        addRow(panel, gbc, 0, "Email:", emailField);
        addRow(panel, gbc, 1, "Password:", passField);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword()).trim();

            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT customer_id FROM customers WHERE email = ? AND password = ?");
                ps.setString(1, email);
                ps.setString(2, pass);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int id = rs.getInt("customer_id");
                    JOptionPane.showMessageDialog(this, "✅ Login successful!");
                    dispose();
                    new BookingUI(id);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Invalid credentials.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGbc();

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JTextField ageField = new JTextField(20);
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"M", "F", "Other"});
        JButton registerBtn = new JButton("Register");

        addRow(panel, gbc, 0, "Name:", nameField);
        addRow(panel, gbc, 1, "Email:", emailField);
        addRow(panel, gbc, 2, "Password:", passField);
        addRow(panel, gbc, 3, "Age:", ageField);
        addRow(panel, gbc, 4, "Gender:", genderBox);
        gbc.gridx = 1; gbc.gridy = 5;
        panel.add(registerBtn, gbc);

        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            String ageStr = ageField.getText().trim();
            String gender = (String) genderBox.getSelectedItem();

            // === Name Validation ===
            if (!name.matches("[a-zA-Z ]+")) {
                JOptionPane.showMessageDialog(this, "❌ Name must contain only alphabets.");
                return;
            }

            // === Email Validation ===
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                JOptionPane.showMessageDialog(this, "❌ Invalid email format.");
                return;
            }

            // === Age Validation ===
            int age;
            try {
                age = Integer.parseInt(ageStr);
                if (age < 18 || age > 99) {
                    JOptionPane.showMessageDialog(this, "❌ Age must be between 18 and 99.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Age must be a number.");
                return;
            }

            // === DB Insertion ===
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement check = con.prepareStatement("SELECT COUNT(*) FROM customers WHERE email = ?");
                check.setString(1, email);
                ResultSet rs = check.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "❌ Email already registered.");
                    return;
                }

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO customers (name, email, password, age, gender) VALUES (?, ?, ?, ?, ?)");
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, pass);
                ps.setInt(4, age);
                ps.setString(5, gender);
                ps.executeUpdate();

                int customerId = getCustomerIdByEmail(email);
                JOptionPane.showMessageDialog(this, "✅ Registered successfully!");
                dispose();
                new BookingUI(customerId);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });


        return panel;
    }

    private int getCustomerIdByEmail(String email) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT customer_id FROM customers WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("customer_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int y, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginRegisterUI::new);
    }
}
