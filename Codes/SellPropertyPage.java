import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class SellPropertyPage extends JPanel {
    private JTextField addressField, priceField, sizeField, numRoomsField, numBathsField, builtYearField;
    private JTextField deletePropertyIdField;
    private JCheckBox parkingBox;
    private JComboBox<String> propertyTypeComboBox, cityComboBox;
    private JButton sellButton, deleteButton, backButton;

    public SellPropertyPage(CardLayout cardLayout, JPanel container, int userID) {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 245, 245)); // Light background color

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 123, 255), 2),
                "Мүлікті сату", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 20), new Color(0, 123, 255)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add fields and labels with increased font size (4 points)
        addFormField(formPanel, "Мекен-жай:", addressField = new JTextField(20), 0, gbc);
        addFormField(formPanel, "Бағасы:", priceField = new JTextField(20), 1, gbc);
        addFormField(formPanel, "Қала:", cityComboBox = new JComboBox<>(loadCitiesFromDatabase()), 2, gbc);
        addFormField(formPanel, "Мүлік типі:", propertyTypeComboBox = new JComboBox<>(loadPropertyTypesFromDatabase()), 3, gbc);
        addFormField(formPanel, "Өлшемі (кв. м):", sizeField = new JTextField(20), 4, gbc);
        addFormField(formPanel, "Бөлме саны:", numRoomsField = new JTextField(20), 5, gbc);
        addFormField(formPanel, "Жуыну бөлме саны:", numBathsField = new JTextField(20), 6, gbc);
        addFormField(formPanel, "Салынған жылы:", builtYearField = new JTextField(20), 7, gbc);

// Parking checkbox with larger font and increased size
        gbc.gridx = 0;
        gbc.gridy = 8;
        parkingBox = new JCheckBox("Парковка");
        parkingBox.setFont(new Font("Arial", Font.PLAIN, 25)); // Increased font size for the checkbox
        parkingBox.setBackground(Color.WHITE); // Set background color to white
        parkingBox.setPreferredSize(new Dimension(350, 50)); // Increase checkbox size
        formPanel.add(parkingBox, gbc);


        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        sellButton = createButton("Сату");
        deleteButton = createButton("Мүлікті өшіру");
        backButton = createButton("Артқа");

        buttonPanel.add(sellButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        // Add form and buttons to the main panel
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        backButton.addActionListener(e -> cardLayout.show(container, "Dashboard"));

        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPropertyToDatabase(userID);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePropertyFromDatabase();
            }
        });
    }

    private void addFormField(JPanel panel, String labelText, JTextField field, int gridY, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = gridY;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 22)); // Increased font size for labels (4 points)
        panel.add(label, gbc);

        gbc.gridx = 1;
        field.setFont(new Font("Arial", Font.PLAIN, 22)); // Increased font size for fields (4 points)
        field.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 1)); // Reduced border width to 1
        panel.add(field, gbc);
    }

    private void addFormField(JPanel panel, String labelText, JComboBox<String> comboBox, int gridY, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = gridY;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 22)); // Increased font size for labels (4 points)
        panel.add(label, gbc);

        gbc.gridx = 1;
        comboBox.setFont(new Font("Arial", Font.PLAIN, 22)); // Increased font size for combo boxes (4 points)
        panel.add(comboBox, gbc);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 22)); // Increased font size for buttons (4 points)
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 90, 200), 2));
        return button;
    }

    private void addPropertyToDatabase(int userID) {
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "INSERT INTO Property (CityID, PropertyTypeID, OwnerID, Price, Address, Status) VALUES (?, ?, ?, ?, ?, 'selling')";
                PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, cityComboBox.getSelectedIndex() + 1);
                stmt.setInt(2, propertyTypeComboBox.getSelectedIndex() + 1);
                stmt.setInt(3, userID);
                stmt.setBigDecimal(4, new BigDecimal(priceField.getText()));
                stmt.setString(5, addressField.getText());
                stmt.executeUpdate();

                // Get the last inserted Property ID
                int propertyId = 0;
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    propertyId = generatedKeys.getInt(1);
                }

                // Insert property details
                String detailsQuery = "INSERT INTO PropertyDetails (PropertyID, Size, P_description, Parking, Num_rooms, Num_bath, Built_year) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement detailsStmt = connection.prepareStatement(detailsQuery);
                detailsStmt.setInt(1, propertyId);
                detailsStmt.setBigDecimal(2, new BigDecimal(sizeField.getText()));
                detailsStmt.setString(3, "Описание недвижимости");
                detailsStmt.setBoolean(4, parkingBox.isSelected());
                detailsStmt.setInt(5, Integer.parseInt(numRoomsField.getText()));
                detailsStmt.setInt(6, Integer.parseInt(numBathsField.getText()));
                detailsStmt.setInt(7, Integer.parseInt(builtYearField.getText()));
                detailsStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Мүлік сәтті қосылды!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Мүлікті қосу кезіңде қателік туды");
            }
        }
    }

    private void deletePropertyFromDatabase() {
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                int propertyId = Integer.parseInt(deletePropertyIdField.getText().trim());
                // Delete property details and then property
                String detailsQuery = "DELETE FROM PropertyDetails WHERE PropertyID = ?";
                PreparedStatement detailsStmt = connection.prepareStatement(detailsQuery);
                detailsStmt.setInt(1, propertyId);
                detailsStmt.executeUpdate();

                String query = "DELETE FROM Property WHERE PropertyID = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, propertyId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Мүлік сәтті өшірілді");
                } else {
                    JOptionPane.showMessageDialog(this, "Мұндай ID бар мүлік табылмады.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Мүлікті өшіруде қателік туды");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Мүлік ID дұрыс еңгізіңіз");
            }
        }
    }

    private String[] loadCitiesFromDatabase() {
        ArrayList<String> cities = new ArrayList<>();
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT CityName FROM City";
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    cities.add(rs.getString("CityName"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Қалалар деректер қорынан жүктелмеді");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Деректер қоырна қосыла алмадық.");
        }
        return cities.toArray(new String[0]);
    }

    private String[] loadPropertyTypesFromDatabase() {
        ArrayList<String> propertyTypes = new ArrayList<>();
        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT TypeName FROM PropertyType";
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    propertyTypes.add(rs.getString("TypeName"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Мүліктердің типін деректер қорынан жүктей алмадық");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Деректер қоырна қосыла алмадық.");
        }
        return propertyTypes.toArray(new String[0]);
    }
}










