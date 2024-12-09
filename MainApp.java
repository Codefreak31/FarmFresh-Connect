import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends JFrame {
    private String loggedInUsername; // Declare the logged-in username here
    private JLabel cartLabel;
    private JPanel categoriesPanel;
    private JPanel vegetablesPanel;
    private JPanel leafyVegetablesPanel;
    private JPanel cartPanel;
    private double cartTotal = 0.0;
    private List<CartItem> cartItems = new ArrayList<>();
    private JTextArea cartDetails; // Declare cartDetails as a class field

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password"; // Replace with your actual password

    public MainApp() {
        setTitle("Farm Fresh");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create navbar with title and buttons
        JPanel navbar = setupNavbar();
        add(navbar, BorderLayout.NORTH);

        // Add categories panel on the left side
        categoriesPanel = setupCategoriesPanel();
        add(categoriesPanel, BorderLayout.WEST);

        // Set up vegetables and leafy vegetables panels
        vegetablesPanel = setupVegetablesPanel();
        leafyVegetablesPanel = setupLeafyVegetablesPanel();
        cartPanel = setupCartPanel(); // Cart panel setup

        // Show vegetables by default
        add(vegetablesPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // Setup the navbar
    // Setup the navbar with better styling
    // Setup the navbar with light green background and light green buttons inside
    // it
    // Setup the navbar with better styling
    private JPanel setupNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(Color.decode("#3B5998")); // A pleasant blue gradient background

        // Left side: Company name FARM FRESH
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        JLabel companyName = new JLabel("F A R M F R E S H");
        companyName.setFont(new Font("Arial", Font.BOLD, 30));
        companyName.setForeground(Color.WHITE);
        leftPanel.add(companyName);
        navbar.add(leftPanel, BorderLayout.WEST);

        // Right side: Profile, Login/Register, and Cart buttons in a horizontal box
        JPanel rightSidePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightSidePanel.setOpaque(false);

        // Login/Register Box
        JPanel loginBox = new JPanel(new FlowLayout());
        loginBox.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        loginBox.setBackground(Color.decode("#4B86A1"));
        loginBox.setPreferredSize(new Dimension(140, 40));
        JLabel loginText = new JLabel("Login/Register");
        loginText.setFont(new Font("Arial", Font.PLAIN, 16));
        loginText.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginText.setForeground(Color.WHITE);
        loginBox.add(loginText);
        rightSidePanel.add(loginBox);

        // Cart Box with icon
        JPanel cartBox = new JPanel(new FlowLayout());
        cartBox.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        cartBox.setBackground(Color.decode("#4B86A1"));
        cartBox.setPreferredSize(new Dimension(120, 40));
        cartLabel = new JLabel("Cart: ₹" + cartTotal);
        cartLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        cartLabel.setForeground(Color.WHITE);
        cartLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cartBox.add(cartLabel);
        rightSidePanel.add(cartBox);

        navbar.add(rightSidePanel, BorderLayout.EAST);

        // Add action listeners for login/register and cart click events
        loginText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showLoginOrRegisterDialog();
            }
        });
        cartLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showCart();
            }
        });

        return navbar;
    }

    private void updateCart(double price, String itemName, int quantity) {
        boolean itemExists = false;

        // Update cart item if it already exists
        for (CartItem item : cartItems) {
            if (item.getName().equals(itemName)) {
                cartTotal -= item.getPrice(); // Subtract the old price
                item.setQuantity(quantity);
                item.setPrice(quantity * price); // Update new price
                cartTotal += item.getPrice(); // Add updated price
                itemExists = true;
                break;
            }
        }

        // Add new item if it doesn't exist
        if (!itemExists) {
            CartItem newItem = new CartItem(itemName, quantity, price * quantity);
            cartItems.add(newItem);
            cartTotal += newItem.getPrice();
        }

        // Update the cart label and details
        cartLabel.setText("Cart: ₹" + String.format("%.2f", cartTotal));
        refreshCartDetails();
    }

    private void refreshCartDetails() {
        StringBuilder detailsBuilder = new StringBuilder();
        for (CartItem item : cartItems) {
            detailsBuilder.append(item.getName())
                    .append(" - Qty: ").append(item.getQuantity())
                    .append(", Price: ₹").append(String.format("%.2f", item.getPrice()))
                    .append("\n");
        }
        cartDetails.setText(detailsBuilder.toString());
        totalLabel.setText("Total: ₹" + String.format("%.2f", cartTotal)); // Update total label dynamically
    }

    private JPanel setupCategoriesPanel() {
        JPanel categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setPreferredSize(new Dimension(200, 0));
        categoriesPanel.setBorder(BorderFactory.createTitledBorder("Categories"));

        JButton vegetablesButton = new JButton("Vegetables");
        vegetablesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        vegetablesButton.addActionListener(e -> showPanel(vegetablesPanel));
        categoriesPanel.add(vegetablesButton);

        JButton leafyButton = new JButton("Leafy Vegetables");
        leafyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leafyButton.addActionListener(e -> showPanel(leafyVegetablesPanel));
        categoriesPanel.add(leafyButton);

        return categoriesPanel;
    }

    private void showPanel(JPanel panel) {
        getContentPane().remove(vegetablesPanel);
        getContentPane().remove(leafyVegetablesPanel);
        getContentPane().remove(cartPanel); // Ensure cart is removed
        add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showCart() {
        getContentPane().remove(vegetablesPanel);
        getContentPane().remove(leafyVegetablesPanel);
        add(cartPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ... [rest of your existing code]
    // Declare totalLabel as a class-level field
    private JLabel totalLabel;

    private JPanel setupCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        cartDetails = new JTextArea(); // Initialize here
        cartDetails.setEditable(false);
        panel.add(new JScrollPane(cartDetails), BorderLayout.CENTER);

        JPanel paymentPanel = new JPanel(new FlowLayout());

        // Initialize totalLabel before calling refreshCartDetails
        totalLabel = new JLabel("Total: ₹0.00");
        paymentPanel.add(totalLabel);

        String[] billingOptions = { "Weekly", "Monthly" };
        JComboBox<String> billingComboBox = new JComboBox<>(billingOptions);
        paymentPanel.add(billingComboBox);

        JButton payButton = new JButton("Make Payment");
        payButton.addActionListener(e -> {
            // Prompt for the address
            JTextArea addressArea = new JTextArea(5, 20);
            JScrollPane scrollPane = new JScrollPane(addressArea);
            int result = JOptionPane.showConfirmDialog(this, scrollPane, "Enter your Address",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String address = addressArea.getText().trim();
                if (!address.isEmpty()) {
                    String selectedOption = (String) billingComboBox.getSelectedItem();
                    double finalAmount = (selectedOption != null && selectedOption.equals("Monthly")) ? cartTotal * 4
                            : cartTotal;

                    // Call method to save order details
                    saveOrderDetails(address, finalAmount);

                    JOptionPane.showMessageDialog(this,
                            "Total Amount to Pay: ₹" + finalAmount + "\nAddress: " + address);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a valid address.");
                }
            }
        });
        paymentPanel.add(payButton);

        panel.add(paymentPanel, BorderLayout.SOUTH);

        // Initialize cart details after all components
        refreshCartDetails();

        return panel;
    }

    private void saveOrderDetails(String address, double totalAmount) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Prepare the SQL update statement
            String sql = "UPDATE users SET address = ?, order_items = ?, total_amount = ? WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            // Create a string of ordered items
            StringBuilder items = new StringBuilder();
            for (CartItem item : cartItems) {
                items.append(item.getName()).append(" (Qty: ").append(item.getQuantity()).append("), ");
            }

            // Remove the last comma and space if there are items
            if (items.length() > 0) {
                items.setLength(items.length() - 2); // Remove last comma and space
            }

            // Use the correct logged-in username
            statement.setString(1, address);
            statement.setString(2, items.toString());
            statement.setDouble(3, totalAmount);
            statement.setString(4, loggedInUsername); // Use the logged-in user's username

            // Execute update
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Order details saved successfully!");
                cartItems.clear();
                cartTotal = 0.0;
                cartLabel.setText("Cart: ₹" + cartTotal);
                refreshCartDetails();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save order details. No rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                loggedInUsername = username; // Store the username of the logged-in user
                JOptionPane.showMessageDialog(this, "Welcome, " + loggedInUsername + "!");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ... [rest of your existing code]

    private JPanel setupVegetablesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 4)); // Four columns for a better layout
        String[] vegetableNames = {
                "Potato", "Tomato", "Onion", "Carrot", "Cabbage", "Cauliflower", "Pepper", "Pumpkin",
                "Lady Finger", "Brinjal", "Bottle Gourd", "Bitter Gourd", "Ridge Gourd", "Snake Gourd",
                "Drumstick", "Green Beans", "Sweet Corn", "Yam", "Beetroot", "Radish", "Turnip",
                "Mushroom", "Spring Onion", "Broccoli", "Zucchini", "Capsicum", "Cucumber"
        };
        double[] prices = {
                30.0, 40.0, 20.0, 25.0, 35.0, 50.0, 70.0, 80.0, 40.0, 60.0, 45.0, 55.0, 65.0, 75.0,
                60.0, 35.0, 45.0, 70.0, 25.0, 20.0, 30.0, 50.0, 40.0, 75.0, 60.0, 35.0, 25.0
        };

        for (int i = 0; i < vegetableNames.length; i++) {
            String vegetable = vegetableNames[i];
            double price = prices[i];

            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            JLabel label = new JLabel(vegetable + " - ₹" + price);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            itemPanel.add(label, BorderLayout.NORTH);

            JPanel quantityPanel = new JPanel();
            final int[] quantity = { 0 };
            JLabel quantityDisplay = new JLabel("0");

            JButton minusButton = new JButton("-");
            minusButton.addActionListener(e -> {
                if (quantity[0] > 0) {
                    quantityDisplay.setText(String.valueOf(--quantity[0]));
                }
            });

            JButton plusButton = new JButton("+");
            plusButton.addActionListener(e -> quantityDisplay.setText(String.valueOf(++quantity[0])));

            JButton addButton = new JButton("Add to Cart");
            addButton.addActionListener(e -> {
                if (quantity[0] > 0) {
                    updateCart(price, vegetable, quantity[0]);
                } else {
                    JOptionPane.showMessageDialog(this, "Quantity must be greater than 0 to add to cart.");
                }
            });

            quantityPanel.add(new JLabel("Qty (kg):"));
            quantityPanel.add(minusButton);
            quantityPanel.add(quantityDisplay);
            quantityPanel.add(plusButton);
            quantityPanel.add(addButton);

            itemPanel.add(quantityPanel, BorderLayout.SOUTH);
            panel.add(itemPanel);
        }

        return panel;
    }

    private JPanel setupLeafyVegetablesPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 3)); // Similar layout for leafy vegetables
        String[] leafyNames = { "Spinach", "Lettuce", "Kale", "Cabbage", "Swiss Chard", "Collard Greens", "Arugula",
                "Mustard Greens" };
        double[] prices = { 30.0, 25.0, 40.0, 35.0, 50.0, 60.0, 20.0, 45.0 };

        for (int i = 0; i < leafyNames.length; i++) {
            String leafy = leafyNames[i];
            double price = prices[i];

            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            JLabel label = new JLabel(leafy + " - ₹" + price);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            itemPanel.add(label, BorderLayout.NORTH);

            JPanel quantityPanel = new JPanel();
            final int[] quantity = { 0 };
            JLabel quantityDisplay = new JLabel("0");

            JButton minusButton = new JButton("-");
            minusButton.addActionListener(e -> {
                if (quantity[0] > 0) {
                    quantityDisplay.setText(String.valueOf(--quantity[0]));
                }
            });

            JButton plusButton = new JButton("+");
            plusButton.addActionListener(e -> quantityDisplay.setText(String.valueOf(++quantity[0])));

            JButton addButton = new JButton("Add to Cart");
            addButton.addActionListener(e -> {
                if (quantity[0] > 0) {
                    updateCart(quantity[0] * price, leafy, quantity[0]);
                }
            });

            quantityPanel.add(new JLabel("Qty (kg):"));
            quantityPanel.add(minusButton);
            quantityPanel.add(quantityDisplay);
            quantityPanel.add(plusButton);
            quantityPanel.add(addButton);

            itemPanel.add(quantityPanel, BorderLayout.SOUTH);
            panel.add(itemPanel);
        }

        return panel;
    }

    private void showLoginOrRegisterDialog() {
        String[] options = { "Login", "Register" };
        int choice = JOptionPane.showOptionDialog(this, "Choose an option:", "Login/Register",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            loginUser();
        } else if (choice == 1) {
            registerUser();
        }
    }

    private void loginUser() {
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(Box.createHorizontalStrut(15)); // spacer
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (authenticateUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Login successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Login failed! Invalid credentials.");
            }
        }
    }

    private void registerUser() {
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField phoneNumberField = new JTextField(15); // New field for phone number
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 5, 5)); // Adjust layout for three fields
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneNumberField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String phoneNumber = phoneNumberField.getText().trim(); // Get phone number input

            if (registerNewUser(username, password, phoneNumber)) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed! Username may already exist.");
            }
        }
    }

    private boolean registerNewUser(String username, String password, String phoneNumber) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO users (username, password, phone) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, phoneNumber); // Store the phone number

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }

    // CartItem class to hold item information
    private class CartItem {
        private String name;
        private int quantity;
        private double price;

        public CartItem(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
