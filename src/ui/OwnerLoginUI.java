package ui;

import javax.swing.*;
import java.awt.*;

public class OwnerLoginUI extends JFrame {

    public OwnerLoginUI() {
        setTitle("Owner Login");
        setSize(300, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel info = new JLabel("Login as Owner", SwingConstants.CENTER);
        info.setFont(new Font("Arial", Font.BOLD, 16));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField("admin@gmail.com");
        emailField.setMaximumSize(new Dimension(200, 25));

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField("123");
        passwordField.setMaximumSize(new Dimension(200, 25));

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(info);
        panel.add(Box.createVerticalStrut(15));
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (email.equals("admin@gmail.com") && pass.equals("123")) {
                JOptionPane.showMessageDialog(this, "✅ Login Successful!");
                dispose();
                new HotelOwnerDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid credentials");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OwnerLoginUI::new);
    }
}
