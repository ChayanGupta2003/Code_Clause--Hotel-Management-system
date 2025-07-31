package ui;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class PaymentUI extends JFrame {

    public PaymentUI(int bookingId, String price) {
        setTitle("💳 Razorpay Payment");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Click below to proceed with payment of ₹" + price, JLabel.CENTER);
        JButton payBtn = new JButton("Proceed to Pay");

        payBtn.addActionListener(e -> {
            try {
                // Convert price string to double
                double priceValue = Double.parseDouble(price);

                // Initialize Razorpay client
                RazorpayClient razorpay = new RazorpayClient("YOUR_API_KEY", "YOUR_API_SECRET");

                // Create Razorpay order
                JSONObject options = new JSONObject();
                options.put("amount", (int)(priceValue * 100));  // price in paise
                options.put("currency", "INR");
                options.put("receipt", "booking_rcpt_" + bookingId);
                options.put("payment_capture", 1);

                Order order = razorpay.orders.create(options);

                JOptionPane.showMessageDialog(this,
                        "✅ Order Created!\n\nOrder ID: " + order.get("id") +
                                "\nGo to Razorpay Dashboard to simulate/test payment.");

                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Payment Failed: " + ex.getMessage());
            }
        });

        add(label, BorderLayout.CENTER);
        add(payBtn, BorderLayout.SOUTH);
        setVisible(true);
    }
}
