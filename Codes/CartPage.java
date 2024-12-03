import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartPage extends JPanel {
    private Connection connection;
    private int userID;
    private List<Property> cartProperties;
    private JList<String> propertyList;
    private DefaultListModel<String> listModel;
    private JButton checkoutButton;
    private JButton removeButton;

    public CartPage(CardLayout cardLayout, JPanel mainPanel, int userID) {
        this.userID = userID;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Initialize database connection
        connection = DBConnection.getConnection();
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.", "Қате", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create list model and property list to display cart properties
        listModel = new DefaultListModel<>();
        propertyList = new JList<>(listModel);
        propertyList.setFont(new Font("Arial", Font.PLAIN, 18));
        propertyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(propertyList);

        // Load and display cart properties
        displayCartProperties();

        // Add scrollPane to the main panel
        add(scrollPane, BorderLayout.CENTER);

        // Create checkout and remove buttons
        checkoutButton = createStyledButton("Сатып алуды растау");
        removeButton = createStyledButton("Себеттен жою");

        checkoutButton.addActionListener(e -> checkoutSelectedProperty());
        removeButton.addActionListener(e -> removeSelectedPropertyFromCart());

        checkoutButton.setEnabled(false);
        removeButton.setEnabled(false);

        // Enable buttons when a selection is made
        propertyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && propertyList.getSelectedIndex() != -1) {
                checkoutButton.setEnabled(true);
                removeButton.setEnabled(true);
            }
        });

        // Create bottom panel with back, checkout, and remove buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton backButton = createStyledButton("Артқа");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));

        bottomPanel.add(backButton);
        bottomPanel.add(checkoutButton);
        bottomPanel.add(removeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }


    private void displayCartProperties() {
        cartProperties = loadCartProperties();
        listModel.clear();
        if (cartProperties.isEmpty()) {
            listModel.addElement("Себет бос.");
        } else {
            for (Property property : cartProperties) {
                listModel.addElement(property.toString());
            }
        }
    }

    private List<Property> loadCartProperties() {
        List<Property> properties = new ArrayList<>();
        String query = "SELECT p.PropertyID, p.Price, p.Address, c.CityName, pt.TypeName " +
                "FROM Cart ca " +
                "JOIN Property p ON ca.PropertyID = p.PropertyID " +
                "JOIN City c ON p.CityID = c.CityID " +
                "JOIN PropertyType pt ON p.PropertyTypeID = pt.PropertyTypeID " +
                "WHERE ca.UserID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("PropertyID");
                String address = rs.getString("Address");
                double price = rs.getDouble("Price");
                String cityName = rs.getString("CityName");
                String typeName = rs.getString("TypeName");

                properties.add(new Property(id, cityName, typeName, price, address));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Мүлікті себеттен жүктеуде қате кетті.");
        }
        return properties;
    }

    private void checkoutSelectedProperty() {
        int selectedIndex = propertyList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Өтінеміз, сатып алу үшін мүлікті таңдаңыз.");
            return;
        }

        Property selectedProperty = cartProperties.get(selectedIndex);

        String updateStatusQuery = "UPDATE Property SET Status = 'sold' WHERE PropertyID = ?";
        String removeFromCartQuery = "DELETE FROM Cart WHERE UserID = ? AND PropertyID = ?";

        try (PreparedStatement updateStmt = connection.prepareStatement(updateStatusQuery);
             PreparedStatement removeStmt = connection.prepareStatement(removeFromCartQuery)) {

            // Update property status to 'sold'
            updateStmt.setInt(1, selectedProperty.getId());
            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated > 0) {
                // If the status update is successful, remove property from cart
                removeStmt.setInt(1, userID);
                removeStmt.setInt(2, selectedProperty.getId());
                int rowsRemoved = removeStmt.executeUpdate();

                if (rowsRemoved > 0) {
                    JOptionPane.showMessageDialog(this, "Сатып алу сәтті аяқталды. Мүлік сатылды.");
                    displayCartProperties(); // Refresh the cart
                } else {
                    JOptionPane.showMessageDialog(this, "Мүлікті себеттен жоюда қате кетті.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Мүлік статусты жаңартуда қате кетті.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Сатып алу кезінде қате орын алды.");
        }
    }




    private void removeSelectedPropertyFromCart() {
        int selectedIndex = propertyList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Өтінеміз, жою үшін мүлікті таңдаңыз.");
            return;
        }

        Property selectedProperty = cartProperties.get(selectedIndex);
        String query = "DELETE FROM Cart WHERE UserID = ? AND PropertyID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userID);
            pstmt.setInt(2, selectedProperty.getId());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Мүлік себеттен жойылды.");
                displayCartProperties();
            } else {
                JOptionPane.showMessageDialog(this, "Мүлікті жоюда қате кетті.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Мүлікті жоюда қате кетті.");
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        return button;
    }


}







