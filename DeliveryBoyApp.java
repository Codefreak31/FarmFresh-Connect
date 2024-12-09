import javax.swing.*;

import org.w3c.dom.events.MouseEvent;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DeliveryBoyApp extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    private JPanel cardPanel;
    private CardLayout cardLayout;

    public DeliveryBoyApp() {
        setTitle("Delivery Boy System");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setup CardLayout for switching between login, register, and dashboard screens
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Add login and register panels
        cardPanel.add(new LoginPanel(), "Login");
        cardPanel.add(new RegisterPanel(), "Register");
        cardPanel.add(new DeliveryDashboard(), "Dashboard");

        add(cardPanel);
        setVisible(true);
    }

    private boolean login(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM delivery_boys WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // If a matching user is found, return true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Login Panel
    // Login Panel
    private class LoginPanel extends JPanel {
        private JTextField usernameField;
        private JPasswordField passwordField;

        public LoginPanel() {
            // Gradient background
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            // Custom gradient background
            setOpaque(true);
            setBackground(new Color(173, 216, 230)); // Light blue as default background

            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            usernameField = new JTextField(20);

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
            passwordField = new JPasswordField(20);

            JButton loginButton = createStyledButton("Login");
            JButton registerButton = createStyledButton("Register");

            // Add action listeners
            loginButton.addActionListener(e -> {
                if (login(usernameField.getText(), new String(passwordField.getPassword()))) {
                    cardLayout.show(cardPanel, "Dashboard");
                    JOptionPane.showMessageDialog(DeliveryBoyApp.this, "Login Successful!");
                } else {
                    JOptionPane.showMessageDialog(DeliveryBoyApp.this, "Invalid username or password.");
                }
            });

            registerButton.addActionListener(e -> {
                // Placeholder for registration logic
                cardLayout.show(cardPanel, "Register");
                JOptionPane.showMessageDialog(DeliveryBoyApp.this, "Redirecting to Registration!");
            });

            // Layout components
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(usernameLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            add(usernameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            add(passwordLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            add(loginButton, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            add(registerButton, gbc);
        }

        private JButton createStyledButton(String text) {
            JButton button = new JButton(text);
            button.setBackground(new Color(34, 193, 195));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(34, 193, 195), 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            return button;
        }
    }

    // Updated RegisterPanel
    private class RegisterPanel extends JPanel {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JPasswordField confirmPasswordField;

        public RegisterPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Padding
            setBackground(new Color(255, 245, 238)); // Light coral background

            JLabel titleLabel = new JLabel("Create a New Account");
            titleLabel.setFont(new Font("Verdana", Font.BOLD, 22));
            titleLabel.setAlignmentX(CENTER_ALIGNMENT);
            titleLabel.setForeground(new Color(205, 92, 92)); // Indian red color

            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setFont(new Font("Verdana", Font.PLAIN, 16));
            usernameField = new JTextField(20);

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(new Font("Verdana", Font.PLAIN, 16));
            passwordField = new JPasswordField(20);

            JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
            confirmPasswordLabel.setFont(new Font("Verdana", Font.PLAIN, 16));
            confirmPasswordField = new JPasswordField(20);

            JButton registerButton = new JButton("Register");
            registerButton.setFont(new Font("Verdana", Font.BOLD, 14));
            registerButton.setBackground(new Color(60, 179, 113)); // Medium sea green
            registerButton.setForeground(Color.WHITE);
            registerButton.setFocusPainted(false);
            registerButton.addActionListener(e -> {
                if (new String(passwordField.getPassword())
                        .equals(new String(confirmPasswordField.getPassword()))) {
                    if (register(usernameField.getText(), new String(passwordField.getPassword()))) {
                        JOptionPane.showMessageDialog(DeliveryBoyApp.this, "Registration Successful!");
                        cardLayout.show(cardPanel, "Login");
                    } else {
                        JOptionPane.showMessageDialog(DeliveryBoyApp.this, "Registration failed, try again.");
                    }
                } else {
                    JOptionPane.showMessageDialog(DeliveryBoyApp.this, "Passwords do not match.");
                }
            });

            JButton backButton = new JButton("Back");
            backButton.setFont(new Font("Verdana", Font.BOLD, 14));
            backButton.setBackground(new Color(220, 20, 60)); // Crimson
            backButton.setForeground(Color.WHITE);
            backButton.setFocusPainted(false);
            backButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));

            // Adding components with spacing
            add(titleLabel);
            add(Box.createVerticalStrut(30)); // Spacing
            add(usernameLabel);
            add(usernameField);
            add(Box.createVerticalStrut(15));
            add(passwordLabel);
            add(passwordField);
            add(Box.createVerticalStrut(15));
            add(confirmPasswordLabel);
            add(confirmPasswordField);
            add(Box.createVerticalStrut(30));
            add(registerButton);
            add(Box.createVerticalStrut(15));
            add(backButton);
        }

        // Method to register the delivery boy
        private boolean register(String username, String password) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO delivery_boys (username, password) VALUES (?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                statement.setString(2, password);

                int rowsInserted = statement.executeUpdate();
                return rowsInserted > 0; // Return true if the registration was successful
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    // Delivery Dashboard Panel (already created in your code)
    // Delivery Dashboard Panel with Enhanced UI
    // Updated DeliveryDashboard with improved order view
    private class DeliveryDashboard extends JPanel {
        public DeliveryDashboard() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(new Color(245, 245, 245)); // Light gray background

            // Title
            JLabel titleLabel = new JLabel("Orders Ready for Delivery");
            titleLabel.setFont(new Font("Verdana", Font.BOLD, 18));
            titleLabel.setForeground(new Color(34, 193, 195)); // Aqua green
            titleLabel.setAlignmentX(CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Padding around the title
            add(titleLabel);

            // Call the method to load and display customer and farmer details
            loadOrders();
        }

        private void loadOrders() {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT u.username AS customer_username, u.phone AS customer_phone, u.order_items AS customer_order_items, u.address AS customer_address, "
                        + "f.username AS farmer_username, f.phone_number AS farmer_phone, f.address AS farmer_address, co.order_status, co.id AS order_id "
                        + "FROM completed_orders co "
                        + "JOIN users u ON co.user_id = u.id "
                        + "JOIN farmers f ON co.farmer_id = f.id "
                        + "WHERE co.order_status = 'Ready'"; // Fetch only "Ready" orders

                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();

                // Create a scrollable panel to hold all orders
                JPanel orderListPanel = new JPanel();
                orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));
                orderListPanel.setBackground(Color.WHITE); // White background for the orders

                // Loop through the result set and create order panels
                while (resultSet.next()) {
                    // Extract order details
                    String customerUsername = resultSet.getString("customer_username");
                    String customerPhone = resultSet.getString("customer_phone");
                    String customerOrderItems = resultSet.getString("customer_order_items");
                    String customerAddress = resultSet.getString("customer_address");

                    String farmerUsername = resultSet.getString("farmer_username");
                    String farmerPhone = resultSet.getString("farmer_phone");
                    String farmerAddress = resultSet.getString("farmer_address");
                    String orderStatus = resultSet.getString("order_status");
                    int orderId = resultSet.getInt("order_id");

                    // Create a panel for each order
                    JPanel orderPanel = new JPanel();
                    orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
                    orderPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Gray border
                    orderPanel.setBackground(Color.WHITE); // White background

                    // Add order details with spacing and formatting
                    orderPanel.add(createLabel("Customer Username: " + customerUsername));
                    orderPanel.add(createLabel("Customer Phone: " + customerPhone));
                    orderPanel.add(createLabel("Order Items: " + customerOrderItems));
                    orderPanel.add(createLabel("Customer Address: " + customerAddress));

                    orderPanel.add(createLabel("Farmer Username: " + farmerUsername));
                    orderPanel.add(createLabel("Farmer Phone: " + farmerPhone));
                    orderPanel.add(createLabel("Farmer Address: " + farmerAddress));

                    orderPanel.add(createLabel("Order Status: " + orderStatus));

                    // Add "Mark as Delivered" checkbox
                    JCheckBox markDeliveredCheckBox = new JCheckBox("Mark as Delivered");
                    markDeliveredCheckBox.addActionListener(e -> {
                        if (markDeliveredCheckBox.isSelected()) {
                            updateOrderStatusToDelivered(orderId);
                        }
                    });
                    orderPanel.add(markDeliveredCheckBox);

                    // Add the order panel to the main order list panel
                    orderListPanel.add(orderPanel);
                    orderListPanel.add(Box.createVerticalStrut(10)); // Add some space between orders
                }

                // Scroll Pane to hold the orders
                JScrollPane scrollPane = new JScrollPane(orderListPanel);
                scrollPane.setPreferredSize(new Dimension(550, 400)); // Scroll pane size
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                add(scrollPane);

                // Revalidate the panel to update the UI
                revalidate();
                repaint();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private JLabel createLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Verdana", Font.PLAIN, 14));
            label.setForeground(Color.DARK_GRAY); // Dark gray color for text
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding around text
            return label;
        }

        private void updateOrderStatusToDelivered(int orderId) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE completed_orders SET order_status = 'Delivered' WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, orderId);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Order marked as delivered.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to update order status.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DeliveryBoyApp();
            }
        });
    }
}
