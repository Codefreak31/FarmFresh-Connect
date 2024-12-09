import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class UserOrder {
    int userId;
    String username;
    String orderItems;

    public UserOrder(int userId, String username, String orderItems) {
        this.userId = userId;
        this.username = username;
        this.orderItems = orderItems;
    }
}

public class FarmerApp extends JFrame {
    private JPanel loginPanel;
    private JPanel farmerDashboardPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;
    private List<UserOrder> customerOrders = new ArrayList<>();
    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    private int loggedInFarmerId; // Store the logged-in farmer's ID

    // Constructor to set up the initial login screen
    public FarmerApp() {
        setTitle("Farmer App");
        setSize(600, 600); // Increased size for better layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the login panel with a gradient background
        loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(173, 216, 230), 0, getHeight(),
                        new Color(255, 255, 255));
                ((Graphics2D) g).setPaint(gradient);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // Apply rounded style to buttons
        loginButton.setBackground(new Color(34, 193, 195));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 193, 195), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        registerButton.setBackground(new Color(253, 187, 45));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(253, 187, 45), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Status label for messages
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);

        // Add components to the panel with adjusted positions
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(registerButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        loginPanel.add(statusLabel, gbc);

        // Set the login panel as the default view
        add(loginPanel);
        loginPanel.setVisible(true);

        // Add action listeners for the buttons
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
    }

    // Method to handle farmer login
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Connect to the database to verify farmer login
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM farmers WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Farmer exists, store the farmer's ID
                loggedInFarmerId = resultSet.getInt("id"); // Store farmer's ID
                JOptionPane.showMessageDialog(this, "Login Successful!");
                switchToFarmerDashboard();
            } else {
                // Invalid login credentials
                statusLabel.setText("Invalid username or password!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to handle farmer registration
    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Connect to the database to register new farmer
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO farmers (username, password) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Registration Successful! You can now login.");
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to switch to the farmer dashboard
    private void switchToFarmerDashboard() {
        // Remove login panel and add the farmer dashboard panel
        remove(loginPanel);

        farmerDashboardPanel = new JPanel();
        farmerDashboardPanel.setLayout(new BoxLayout(farmerDashboardPanel, BoxLayout.Y_AXIS));

        // Set the background color of the farmer dashboard to light blue
        farmerDashboardPanel.setBackground(new Color(173, 216, 230)); // Light blue color

        loadCustomerOrders();

        add(new JScrollPane(farmerDashboardPanel));
        farmerDashboardPanel.revalidate();
        farmerDashboardPanel.repaint();

        // Refresh the JFrame
        setSize(600, 600); // Adjusted size
        setVisible(true);
    }

    // Method to load customer orders from the database
    private void loadCustomerOrders() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, username, order_items FROM users WHERE order_items IS NOT NULL";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String orderItems = resultSet.getString("order_items");

                customerOrders.add(new UserOrder(userId, username, orderItems));

                displayCustomerOrderWithCheckbox(username, orderItems, userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to display customer orders with checkboxes
    private void displayCustomerOrderWithCheckbox(String username, String orderItems, int userId) {
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(new Color(255, 255, 255)); // White background for each order panel
        orderPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        orderPanel.setMaximumSize(new Dimension(500, 150));

        // Stylish customer name and order items
        JLabel customerLabel = new JLabel("Customer: " + username);
        customerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        customerLabel.setForeground(new Color(50, 50, 50)); // Dark text color

        JLabel itemsLabel = new JLabel("Order Items: " + orderItems);
        itemsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        itemsLabel.setForeground(new Color(100, 100, 100));

        // Checkbox to mark the order as ready
        JCheckBox readyCheckbox = new JCheckBox("Mark as Ready");
        readyCheckbox.setFont(new Font("Arial", Font.PLAIN, 14));
        readyCheckbox.setBackground(Color.WHITE);
        readyCheckbox.setForeground(new Color(34, 193, 195)); // Stylish greenish color

        readyCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (readyCheckbox.isSelected()) {
                    markOrderAsReady(userId, username); // Pass both userId and username
                }
            }
        });

        orderPanel.add(customerLabel);
        orderPanel.add(itemsLabel);
        orderPanel.add(readyCheckbox);

        farmerDashboardPanel.add(orderPanel);
        farmerDashboardPanel.revalidate();
        farmerDashboardPanel.repaint();
    }

    // Method to mark the order as ready and fetch details
    private void markOrderAsReady(int userId, String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Insert into completed_orders table
            String sqlInsert = "INSERT INTO completed_orders (user_id, farmer_id, order_status) VALUES (?, ?, 'Ready')";
            PreparedStatement insertStatement = connection.prepareStatement(sqlInsert);
            insertStatement.setInt(1, userId); // Set user ID
            insertStatement.setInt(2, loggedInFarmerId); // Set logged-in farmer ID
            insertStatement.executeUpdate();

            // Fetch customer and farmer details
            String sqlSelect = "SELECT u.username AS customer_username, u.phone AS customer_phone, " +
                    "u.order_items AS customer_order_items, u.address AS customer_address, " +
                    "f.username AS farmer_username, f.phone_number AS farmer_phone, " +
                    "f.address AS farmer_address, co.order_status " +
                    "FROM completed_orders co " +
                    "JOIN users u ON co.user_id = u.id " +
                    "JOIN farmers f ON co.farmer_id = f.id " +
                    "WHERE co.order_status = 'Ready' AND co.user_id = ?";
            PreparedStatement selectStatement = connection.prepareStatement(sqlSelect);
            selectStatement.setInt(1, userId); // Select the order marked as ready for the user
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Displaying the results in a dialog or somewhere in the UI
                String customerInfo = "Customer Username: " + resultSet.getString("customer_username") +
                        "\nPhone: " + resultSet.getString("customer_phone") +
                        "\nOrder Items: " + resultSet.getString("customer_order_items") +
                        "\nAddress: " + resultSet.getString("customer_address");

                String farmerInfo = "Farmer Username: " + resultSet.getString("farmer_username") +
                        "\nPhone: " + resultSet.getString("farmer_phone") +
                        "\nAddress: " + resultSet.getString("farmer_address");

                String orderDetails = "Order Status: " + resultSet.getString("order_status");

                String message = customerInfo + "\n\n" + farmerInfo + "\n\n" + orderDetails;
                JOptionPane.showMessageDialog(this, message, "Order Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FarmerApp().setVisible(true);
            }
        });
    }
}