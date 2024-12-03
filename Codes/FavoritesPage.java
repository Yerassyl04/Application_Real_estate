import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritesPage extends JPanel {
    private Connection connection;
    private int userID;
    private List<Property> favoritesProperties;  // To keep track of the loaded properties
    private JList<String> propertyList;          // A list component to show property names
    private DefaultListModel<String> listModel;  // Model for managing list data
    private JButton deleteButton;                // Delete button

    public FavoritesPage(CardLayout cardLayout, JPanel mainPanel, int userID) {
        this.userID = userID;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Initialize database connection
        connection = DBConnection.getConnection();
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.", "Қате", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create list model and property list to display favorites
        listModel = new DefaultListModel<>();
        propertyList = new JList<>(listModel);
        propertyList.setFont(new Font("Arial", Font.PLAIN, 18));
        propertyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(propertyList);

        displayFavoritesProperties();

        add(scrollPane, BorderLayout.CENTER);

        // Create delete button
        deleteButton = createStyledButton("Өшіру");
        deleteButton.addActionListener(e -> deleteSelectedProperty());
        deleteButton.setEnabled(false);

        // Enable delete button when a selection is made
        propertyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && propertyList.getSelectedIndex() != -1) {
                deleteButton.setEnabled(true);
            }
        });

        // Create bottom panel with back and delete buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton backButton = createStyledButton("Артқа");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard")); // Show the dashboard panel

        bottomPanel.add(backButton);
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void displayFavoritesProperties() {
        favoritesProperties = loadFavoritesProperties();
        listModel.clear();
        if (favoritesProperties.isEmpty()) {
            listModel.addElement("Таңдалым жоқ.");
        } else {
            for (Property property : favoritesProperties) {
                listModel.addElement(property.toString());
            }
        }
    }

    private List<Property> loadFavoritesProperties() {
        List<Property> properties = new ArrayList<>();
        String query = "SELECT p.PropertyID, p.Price, p.Address, c.CityName, pt.TypeName, p.OwnerID " +
                "FROM Wishlist w " +
                "JOIN Property p ON w.PropertyID = p.PropertyID " +
                "JOIN City c ON p.CityID = c.CityID " +
                "JOIN PropertyType pt ON p.PropertyTypeID = pt.PropertyTypeID " +
                "WHERE w.UserID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("PropertyID");
                String address = rs.getString("Address");
                double price = rs.getDouble("Price");
                String cityName = rs.getString("CityName");
                String typeName = rs.getString("TypeName");
                int ownerID = rs.getInt("OwnerID");  // Get the ownerID
                properties.add(new Property(id, cityName, typeName, price, address, ownerID)); // Pass ownerID
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Мүлікті таңдалымнан жүктеуде қате кетті");
        }
        return properties;
    }

    private void deleteSelectedProperty() {
        int selectedIndex = propertyList.getSelectedIndex();
        if (selectedIndex != -1 && selectedIndex < favoritesProperties.size()) {
            Property selectedProperty = favoritesProperties.get(selectedIndex);
            int confirmation = JOptionPane.showConfirmDialog(this,
                    "Сіз таңдалған мүлікті таңдалымнан өшіргіңіз келеді ме?",
                    "Өшіруді орындау", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                removeFromWishlist(selectedProperty.getId());
                favoritesProperties.remove(selectedIndex);
                listModel.remove(selectedIndex);
                deleteButton.setEnabled(false);
            }
        }
    }

    private void removeFromWishlist(int propertyID) {
        String deleteQuery = "DELETE FROM Wishlist WHERE UserID = ? AND PropertyID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, userID);
            pstmt.setInt(2, propertyID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Мүлікті таңдалымнан өшіру кезіңде қателік кетті");
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






