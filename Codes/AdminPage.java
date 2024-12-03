import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminPage extends JPanel {

    public AdminPage(CardLayout cardLayout, JPanel container) {
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);

        ImageIcon icon = new ImageIcon("C:\\Users\\asus\\IdeaProjects\\RealEstate\\src\\housetemplate.png");
        Image scaledImage = icon.getImage().getScaledInstance(60, 40, Image.SCALE_SMOOTH); // Resize the image
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

        JLabel titleLabel = new JLabel("Admin Page");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 30));

        titlePanel.add(imageLabel);
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.add("User Management", createUserManagementPanel());
        tabbedPane.add("Property Management", createPropertyManagementPanel());
        tabbedPane.add("Sales Management", createSalesManagementPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Back to login page");
        backButton.addActionListener(e -> {
            cardLayout.show(container, "Login");
        });
        add(backButton, BorderLayout.SOUTH);
    }
    // Create a styled button
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(30, 144, 255)); // Blue color
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

    // User Management Panel
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea userArea = new JTextArea();
        userArea.setFont(new Font("Monospaced", Font.PLAIN, 16)); // Set larger font size
        userArea.setEditable(false);

        JButton viewUsersButton = createStyledButton("View Users");
        viewUsersButton.addActionListener(e -> {
            userArea.setText(""); // Clear previous content
            try (Connection connection = DBConnection.getConnection()) {
                String query = "SELECT UserID, Sname, Mname, Lname, Email, City FROM \"User\"";
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    userArea.append(String.format(
                            "ID: %d, Name: %s %s %s, Email: %s, City: %s%n",
                            rs.getInt("UserID"),
                            rs.getString("Sname"),
                            rs.getString("Mname"),
                            rs.getString("Lname"),
                            rs.getString("Email"),
                            rs.getString("City")
                    ));
                }
            } catch (SQLException ex) {
                userArea.setText("Error fetching users: " + ex.getMessage());
            }
        });

        JButton deleteUserButton = createStyledButton("Delete User");
        JTextField deleteUserIdField = new JTextField(10);
        deleteUserButton.addActionListener(e -> {
            try (Connection connection = DBConnection.getConnection()) {
                String query = "DELETE FROM \"User\" WHERE UserID = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(deleteUserIdField.getText()));
                int rowsAffected = stmt.executeUpdate();
                userArea.setText(rowsAffected > 0 ? "User deleted successfully." : "No user found with the given ID.");
            } catch (SQLException ex) {
                userArea.setText("Error deleting user: " + ex.getMessage());
            }
        });

        JPanel actionsPanel = new JPanel();
        actionsPanel.add(viewUsersButton);
        actionsPanel.add(new JLabel("Delete ID:"));
        actionsPanel.add(deleteUserIdField);
        actionsPanel.add(deleteUserButton);

        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(userArea), BorderLayout.CENTER);

        return panel;
    }

    // Property Management Panel
    private JPanel createPropertyManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea propertyArea = new JTextArea();
        propertyArea.setFont(new Font("Monospaced", Font.PLAIN, 16)); // Set larger font size
        propertyArea.setEditable(false);

        JButton viewPropertiesButton = createStyledButton("View Properties");
        viewPropertiesButton.addActionListener(e -> {
            propertyArea.setText(""); // Clear previous content
            try (Connection connection = DBConnection.getConnection()) {
                String query = """
                    SELECT p.PropertyID, c.CityName, pt.TypeName, p.Price, p.Status, p.Address
                    FROM Property p
                    JOIN City c ON p.CityID = c.CityID
                    JOIN PropertyType pt ON p.PropertyTypeID = pt.PropertyTypeID
                    """;
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    propertyArea.append(String.format(
                            "ID: %d, City: %s, Type: %s, Price: %.2f, Status: %s, Address: %s%n",
                            rs.getInt("PropertyID"),
                            rs.getString("CityName"),
                            rs.getString("TypeName"),
                            rs.getDouble("Price"),
                            rs.getString("Status"),
                            rs.getString("Address")
                    ));
                }
            } catch (SQLException ex) {
                propertyArea.setText("Error fetching properties: " + ex.getMessage());
            }
        });

        JButton deletePropertyButton = createStyledButton("Delete Property");
        JTextField deletePropertyIdField = new JTextField(10);
        deletePropertyButton.addActionListener(e -> {
            try (Connection connection = DBConnection.getConnection()) {
                String query = "DELETE FROM Property WHERE PropertyID = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(deletePropertyIdField.getText()));
                int rowsAffected = stmt.executeUpdate();
                propertyArea.setText(rowsAffected > 0 ? "Property deleted successfully." : "No property found with the given ID.");
            } catch (SQLException ex) {
                propertyArea.setText("Error deleting property: " + ex.getMessage());
            }
        });

        JPanel actionsPanel = new JPanel();
        actionsPanel.add(viewPropertiesButton);
        actionsPanel.add(new JLabel("Delete ID:"));
        actionsPanel.add(deletePropertyIdField);
        actionsPanel.add(deletePropertyButton);

        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(propertyArea), BorderLayout.CENTER);

        return panel;
    }

    // Sales Management Panel
    private JPanel createSalesManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea salesArea = new JTextArea();
        salesArea.setFont(new Font("Monospaced", Font.PLAIN, 16)); // Set larger font size
        salesArea.setEditable(false);

        JButton viewSalesButton = createStyledButton("View Sales");
        viewSalesButton.addActionListener(e -> {
            salesArea.setText(""); // Clear previous content
            try (Connection connection = DBConnection.getConnection()) {
                String query = """
                    SELECT s.SalesID, p.Address, b.Sname AS BuyerName, s.SaleDate, s.SalePrice
                    FROM SalesRecord s
                    JOIN Property p ON s.PropertyID = p.PropertyID
                    JOIN "User" b ON s.BuyerID = b.UserID
                    """;
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    salesArea.append(String.format(
                            "ID: %d, Address: %s, Buyer: %s, Date: %s, Price: %.2f%n",
                            rs.getInt("SalesID"),
                            rs.getString("Address"),
                            rs.getString("BuyerName"),
                            rs.getDate("SaleDate"),
                            rs.getDouble("SalePrice")
                    ));
                }
            } catch (SQLException ex) {
                salesArea.setText("Error fetching sales records: " + ex.getMessage());
            }
        });

        JPanel actionsPanel = new JPanel();
        actionsPanel.add(viewSalesButton);

        panel.add(actionsPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(salesArea), BorderLayout.CENTER);

        return panel;
    }
}



