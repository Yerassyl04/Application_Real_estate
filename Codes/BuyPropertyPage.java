import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

public class BuyPropertyPage extends JPanel {
    private JComboBox<String> cityFilter;
    private JComboBox<String> propertyTypeFilter;
    private JTextField minPriceField, maxPriceField;
    private JComboBox<String> sortByPrice;
    private JPanel propertyListPanel;
    private Connection connection;
    private Property selectedProperty;
    private int userID;


    private void showPropertyDetails(Property property) {
        // Create a new dialog to show detailed property information
        JDialog detailsDialog = new JDialog();
        detailsDialog.setTitle("Мүлік туралы толық ақпарат");
        detailsDialog.setSize(600, 500);
        detailsDialog.setModal(true);
        detailsDialog.setLayout(new BorderLayout(10, 10));

        // Fetch additional property details from the database
        PropertyDetails details = fetchPropertyDetails(property.getId());

        if (details == null) {
            JOptionPane.showMessageDialog(this, "Мүлік туралы қосымша мәліметтерді алу мүмкін болмады", "Қате", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Main details panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240)); // Light gray background


        // Property Basic Details
        JLabel titleLabel = new JLabel("Мүлік туралы толық ақпарат");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Details grid with better spacing
        JPanel detailsGrid = new JPanel(new GridLayout(0, 2, 15, 15));
        detailsGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsGrid.setBackground(new Color(240, 240, 240));

        // Add detailed property information with tooltips
        addDetailRow(detailsGrid, "Қала:", property.getCity(), new Font("Arial", Font.PLAIN, 16));
        addDetailRow(detailsGrid, "Мүлік түрі:", property.getType(), new Font("Arial", Font.PLAIN, 16));
        addDetailRow(detailsGrid, "Мекен-жайы:", property.getAddress(), new Font("Arial", Font.PLAIN, 16));
        addDetailRow(detailsGrid, "Бағасы:", new DecimalFormat("#,###.00").format(property.getPrice()) + " ₸", new Font("Arial", Font.BOLD, 16));

        // Additional details from PropertyDetails table
        addDetailRow(detailsGrid, "Көлемі:", details.getSize() + " шаршы метр", new Font("Arial", Font.PLAIN, 16));
        addDetailRow(detailsGrid, "Бөлмелер саны:", String.valueOf(details.getNumRooms()), new Font("Arial", Font.PLAIN, 16));
        addDetailRow(detailsGrid, "Жуынатын бөлмелер саны:", String.valueOf(details.getNumBath()), new Font("Arial", Font.PLAIN, 16));
        addDetailRow(detailsGrid, "Салынған жылы:", String.valueOf(details.getBuiltYear()), new Font("Arial", Font.PLAIN, 16));
        addDetailRow(detailsGrid, "Тұрақ:", details.hasParking() ? "Бар" : "Жоқ", new Font("Arial", Font.PLAIN, 16));

        mainPanel.add(detailsGrid);


        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));
        JButton closeButton = createStyledButton("Жабу");

        closeButton.addActionListener(e -> detailsDialog.dispose());

        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel);

        // Add scroll pane if needed
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        detailsDialog.add(scrollPane, BorderLayout.CENTER);

        // Center the dialog on screen
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value, Font font) {
        JLabel labelComponent = new JLabel(label);
        JLabel valueComponent = new JLabel(value);
        labelComponent.setFont(font);
        valueComponent.setFont(font);
        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    private PropertyDetails fetchPropertyDetails(int propertyId) {
        PropertyDetails details = null;
        try {
            String query = "SELECT * FROM PropertyDetails WHERE PropertyID = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, propertyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                details = new PropertyDetails(
                        rs.getInt("pdetailid"),
                        propertyId,
                        rs.getDouble("size"),
                        rs.getString("p_description"),
                        rs.getBoolean("parking"),
                        rs.getInt("num_rooms"),
                        rs.getInt("num_bath"),
                        rs.getInt("built_year")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }


    // Modify the existing selectProperty method to use showPropertyDetails
    private void selectProperty(Property property) {
        selectedProperty = property;
        showPropertyDetails(property);
    }

    // New PropertyDetails class to represent property details from database
    public class PropertyDetails {
        private int pDetailId;
        private int propertyId;
        private double size;
        private String description;
        private boolean parking;
        private int numRooms;
        private int numBath;
        private int builtYear;

        public PropertyDetails(int pDetailId, int propertyId, double size, String description,
                               boolean parking, int numRooms, int numBath, int builtYear) {
            this.pDetailId = pDetailId;
            this.propertyId = propertyId;
            this.size = size;
            this.description = description;
            this.parking = parking;
            this.numRooms = numRooms;
            this.numBath = numBath;
            this.builtYear = builtYear;
        }

        // Getters
        public int getPDetailId() { return pDetailId; }
        public int getPropertyId() { return propertyId; }
        public double getSize() { return size; }
        public String getDescription() { return description; }
        public boolean hasParking() { return parking; }
        public int getNumRooms() { return numRooms; }
        public int getNumBath() { return numBath; }
        public int getBuiltYear() { return builtYear; }
    }

    public BuyPropertyPage(CardLayout cardLayout, JPanel container, int userID) {
        this.userID = userID; // Initialize userID
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Set preferred size and background color
        setPreferredSize(new Dimension(500, 700));
        setBackground(Color.WHITE);

        // Header
        JLabel label = new JLabel("Жылжымайтын мүлік сатып алу");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; // Center in the grid
        gbc.gridy = 0; // Top of the grid
        gbc.gridwidth = 2; // Span 2 columns
        add(label, gbc);

        // Initialize database connection
        connection = DBConnection.getConnection();
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.", "Қате", JOptionPane.ERROR_MESSAGE);
            return; // Exit constructor if connection failed
        }

// Filters panel (arranged in a single line)
        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filtersPanel.setBackground(Color.WHITE);
        filtersPanel.setOpaque(true);// Horizontal layout with gaps

// City filter
        JLabel cityLabel = new JLabel("Қала:");
        cityLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        filtersPanel.add(cityLabel);

        cityFilter = new JComboBox<>(loadCitiesFromDatabase());
        cityFilter.setFont(new Font("Arial", Font.PLAIN, 18));
        filtersPanel.add(cityFilter);

// Property type filter
        JLabel propertyTypeLabel = new JLabel("Мүлік түрі:");
        propertyTypeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        filtersPanel.add(propertyTypeLabel);

        propertyTypeFilter = new JComboBox<>(loadPropertyTypesFromDatabase());
        propertyTypeFilter.setFont(new Font("Arial", Font.PLAIN, 18));
        filtersPanel.add(propertyTypeFilter);

// Price filters
        JLabel priceLabel = new JLabel("Бағасы:");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        filtersPanel.add(priceLabel);

        minPriceField = new JTextField(8);
        minPriceField.setFont(new Font("Arial", Font.PLAIN, 18));
        filtersPanel.add(minPriceField);

        JLabel toLabel = new JLabel("дейін");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        filtersPanel.add(toLabel);

        maxPriceField = new JTextField(8);
        maxPriceField.setFont(new Font("Arial", Font.PLAIN, 18));
        filtersPanel.add(maxPriceField);

// Sort options
        JLabel sortLabel = new JLabel("Бағасы бойынша:");
        sortLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        filtersPanel.add(sortLabel);

        sortByPrice = new JComboBox<>(new String[]{"Өсім", "Кему"});
        sortByPrice.setFont(new Font("Arial", Font.PLAIN, 18));
        filtersPanel.add(sortByPrice);

// Add the filters panel to the main layout
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span across both columns
        add(filtersPanel, gbc);

// Property list panel
        propertyListPanel = new JPanel();
        propertyListPanel.setLayout(new GridLayout(0, 2, 10, 10)); // 0 rows, 2 columns, with 10px gaps
        propertyListPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(propertyListPanel);
        scrollPane.setPreferredSize(new Dimension(1500, 800)); // Set a preferred size for the scroll pane
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

        gbc.gridx = 0; // Reset to column 0
        gbc.gridy = 6;
        gbc.gridwidth = 2; // Span 2 columns
        add(scrollPane, gbc);


        // Apply filters button
        JButton applyFiltersButton = createStyledButton("Шектеуді орындау");
        applyFiltersButton.setBackground(Color.BLUE);
        applyFiltersButton.setForeground(Color.WHITE);

        gbc.gridwidth = 1; // Reset to 1 column
        gbc.gridx = 0; // Reset to column 0
        gbc.gridy = 7;
        add(applyFiltersButton, gbc);

        applyFiltersButton.addActionListener(e -> applyFiltersAndSort());

        // Back button
        JButton backButton = createStyledButton("Артқа");
        gbc.gridx = 1; // Move to column 1
        add(backButton, gbc);

        backButton.addActionListener(e -> cardLayout.show(container, "Dashboard"));


        // Load and display all properties by default
        displayProperties(loadPropertiesFromDatabase(null, null, 0, Double.MAX_VALUE, "ASC"));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        return button;
    }





    private String[] loadCitiesFromDatabase() {
        ArrayList<String> cities = new ArrayList<>();
        cities.add("Все");
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.", "Қате", JOptionPane.ERROR_MESSAGE);
            return cities.toArray(new String[0]);
        }

        try {
            String query = "SELECT CityName FROM City";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                cities.add(rs.getString("CityName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities.toArray(new String[0]);
    }

    private String[] loadPropertyTypesFromDatabase() {
        ArrayList<String> propertyTypes = new ArrayList<>();
        propertyTypes.add("Все");
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.", "Қате", JOptionPane.ERROR_MESSAGE);
            return propertyTypes.toArray(new String[0]);
        }

        try {
            String query = "SELECT TypeName FROM PropertyType";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                propertyTypes.add(rs.getString("TypeName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return propertyTypes.toArray(new String[0]);
    }

    private List<Property> loadPropertiesFromDatabase(String city, String type, double minPrice, double maxPrice, String sortOrder) {
        List<Property> properties = new ArrayList<>();
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Деректер қорына қосыла алмадық. Өтінеміз, мәзірді тексеріңіз.", "Қате", JOptionPane.ERROR_MESSAGE);
            return properties;
        }

        try {
            StringBuilder query = new StringBuilder("SELECT p.PropertyID, c.CityName, pt.TypeName, p.Price, p.Address, p.OwnerID " +
                    "FROM Property p " +
                    "JOIN City c ON p.CityID = c.CityID " +
                    "JOIN PropertyType pt ON p.PropertyTypeID = pt.PropertyTypeID " +
                    "WHERE 1=1");
            if (!"Все".equals(city) && city != null) {
                query.append(" AND c.CityName = '").append(city).append("'");
            }
            if (!"Все".equals(type) && type != null) {
                query.append(" AND pt.TypeName = '").append(type).append("'");
            }
            if (minPrice >= 0) {
                query.append(" AND p.Price >= ").append(minPrice);
            }
            if (maxPrice < Double.MAX_VALUE) {
                query.append(" AND p.Price <= ").append(maxPrice);
            }
            query.append(" ORDER BY p.Price ").append(sortOrder);

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                int id = rs.getInt("PropertyID");
                String cityName = rs.getString("CityName"); // Get the city name from the City table
                String typeName = rs.getString("TypeName"); // Get the property type from the PropertyType table
                double price = rs.getDouble("Price");
                String address = rs.getString("Address");
                int ownerID = rs.getInt("OwnerID");
                properties.add(new Property(id, cityName, typeName, price, address, ownerID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private void displayProperties(List<Property> properties) {
        propertyListPanel.removeAll();
        DecimalFormat decimalFormat = new DecimalFormat("#,###.00"); // Format with commas and 2 decimal places

        for (Property property : properties) {
            // Create a panel for each property (card)
            JPanel propertyCard = new JPanel();
            propertyCard.setLayout(new BorderLayout());
            propertyCard.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            propertyCard.setBackground(Color.WHITE);
            propertyCard.setPreferredSize(new Dimension(650, 150)); // Fixed size for the card
            propertyCard.setMaximumSize(new Dimension(650, 150));

            // Image placeholder on the left side
            JLabel imageLabel = new JLabel();
            imageLabel.setPreferredSize(new Dimension(120, 120)); // Fixed size for the image
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.setVerticalAlignment(JLabel.CENTER);

            // Example: Placeholder image (use actual image URLs if available)
            imageLabel.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\asus\\IdeaProjects\\RealEstate\\src\\propertysell.png")
                    .getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));

            // Add image to the left side
            propertyCard.add(imageLabel, BorderLayout.WEST);

            // Details on the right
            JPanel detailsPanel = new JPanel();
            detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
            detailsPanel.setBackground(Color.WHITE);
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Property details
            JLabel cityLabel = new JLabel("Қала: " + property.getCity());
            cityLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            detailsPanel.add(cityLabel);

            JLabel typeLabel = new JLabel("Түрі: " + property.getType());
            typeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            detailsPanel.add(typeLabel);

            JLabel priceLabel = new JLabel("Бағасы: " + decimalFormat.format(property.getPrice()) + " ₸");
            priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
            priceLabel.setForeground(new Color(0, 128, 0));
            detailsPanel.add(priceLabel);

            JLabel addressLabel = new JLabel("Мекен-жайы: " + property.getAddress());
            addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            detailsPanel.add(addressLabel);

            // Add details to the center of the card
            propertyCard.add(detailsPanel, BorderLayout.CENTER);

            // Buttons panel
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonsPanel.setBackground(Color.WHITE);

            // "View" button
            JButton viewButton = new JButton("Қарау");
            viewButton.setFont(new Font("Arial", Font.BOLD, 14));
            viewButton.setBackground(new Color(111, 172, 224));
            viewButton.setForeground(Color.WHITE);
            viewButton.addActionListener(e -> selectProperty(property));
            buttonsPanel.add(viewButton);

            // "Add to Cart" button
            JButton addToCartButton = new JButton("Себетке қосу");
            addToCartButton.setFont(new Font("Arial", Font.BOLD, 14));
            addToCartButton.setBackground(new Color(52, 139, 215));
            addToCartButton.setForeground(Color.WHITE);
            addToCartButton.addActionListener(e -> {
                selectedProperty = property;
                addToCart();
            });
            buttonsPanel.add(addToCartButton);

            // "Add to Wishlist" button
            JButton addToWishlistButton = new JButton("Таңдалымға қосу");
            addToWishlistButton.setFont(new Font("Arial", Font.BOLD, 14));
            addToWishlistButton.setBackground(new Color(5, 115, 204));
            addToWishlistButton.setForeground(Color.WHITE);
            addToWishlistButton.addActionListener(e -> {
                selectedProperty = property;
                addToWishlist();
            });
            buttonsPanel.add(addToWishlistButton);

            // Add buttons panel to the bottom of the card
            propertyCard.add(buttonsPanel, BorderLayout.SOUTH);

            // Add the card to the list panel
            propertyListPanel.add(propertyCard);
        }
        propertyListPanel.revalidate();
        propertyListPanel.repaint();
    }



    private void applyFiltersAndSort() {
        String selectedCity = (String) cityFilter.getSelectedItem();
        String selectedType = (String) propertyTypeFilter.getSelectedItem();
        double minPrice = minPriceField.getText().isEmpty() ? 0 : Double.parseDouble(minPriceField.getText());
        double maxPrice = maxPriceField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceField.getText());
        String selectedSortOrder = sortByPrice.getSelectedItem().equals("По возрастанию") ? "ASC" : "DESC";

        List<Property> filteredProperties = loadPropertiesFromDatabase(selectedCity, selectedType, minPrice, maxPrice, selectedSortOrder);
        displayProperties(filteredProperties);
    }


    private void addToCart() {
        if (selectedProperty == null) {
            JOptionPane.showMessageDialog(this, "Өтініш, себетке қосу үшін таңдау жасаңыз", "Қате", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Insert the selected property into the Cart table instead of SalesRecord
            String query = "INSERT INTO Cart (UserID, PropertyID) VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userID); // UserID for the current user (buyer)
            pstmt.setInt(2, selectedProperty.getId()); // PropertyID for the selected property

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected); // Debugging output
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Мүлік себетке қосылды: " + selectedProperty.toString(), "Сәтті", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Мүлік себетке қосылмады", "Қате", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error for debugging
            JOptionPane.showMessageDialog(this, "Себетке қосу кезінде қателік туындады " + e.getMessage(), "Қате", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void addToWishlist() {
        if (selectedProperty == null) {
            JOptionPane.showMessageDialog(this, "Таңдалымға қосу үшін мүлікті таңдаңыз", "Қате", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // SQL logic to add selectedProperty to wishlist
        try {
            String query = "INSERT INTO Wishlist (UserID, PropertyID) VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, userID);
            pstmt.setInt(2, selectedProperty.getId()); // Assuming you have a getId() method in Property class

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Мүлік таңдалымға қосылды: " + selectedProperty.toString(), "Сәтті", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Мүлік таңдалымға қосылмады", "Қате", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Мүлікті таңдалымға қосу кезінде қателік орын алды: " + e.getMessage(), "Қате", JOptionPane.ERROR_MESSAGE);
        }
    }

}















