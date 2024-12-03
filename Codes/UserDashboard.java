import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserDashboard extends JPanel {
    private String username;
    private int userID;

    public UserDashboard(CardLayout cardLayout, JPanel container, String username, int userID) {
        this.username = username;
        this.userID = userID;
        setLayout(null);

        // Navigation Panel
        NavigationPanel navigationPanel = new NavigationPanel(cardLayout, container);
        navigationPanel.setBounds(0, 0, 1940, 50);
        add(navigationPanel);

        JLabel welcomeLabel = new JLabel("Қайта оралуыңызбен, " + username + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setBounds(650, 100, 700, 30);
        add(welcomeLabel);

        JLabel systemStatsLabel = new JLabel("Жүйе статикасы", JLabel.CENTER);
        systemStatsLabel.setFont(new Font("Arial", Font.PLAIN, 23));
        systemStatsLabel.setBounds(650, 140, 700, 30);
        add(systemStatsLabel);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBounds(50, 150, 200, 300);
        buttonPanel.setOpaque(false);
        add(buttonPanel);

        JButton sellButton = createStyledButton("Мүлікті сату", "sell_icon.png");
        JButton buyButton = createStyledButton("Мүлікті сатып-алу", "buy_icon.png");
        JButton favoriteButton = createStyledButton("Таңдалым", "favorite_icon.png");
        JButton cartButton = createStyledButton("Себет", "cart_icon.png");

        buttonPanel.add(sellButton);
        buttonPanel.add(buyButton);
        buttonPanel.add(favoriteButton);
        buttonPanel.add(cartButton);

        sellButton.addActionListener(e -> {
            SellPropertyPage sellPage = new SellPropertyPage(cardLayout, container, userID);
            container.add(sellPage, "SellPropertyPage");
            cardLayout.show(container, "SellPropertyPage");
        });

        buyButton.addActionListener(e -> {
            BuyPropertyPage buyPage = new BuyPropertyPage(cardLayout, container, userID);
            container.add(buyPage, "BuyPropertyPage");
            cardLayout.show(container, "BuyPropertyPage");
        });

        favoriteButton.addActionListener(e -> {
            FavoritesPage favoritesPage = new FavoritesPage(cardLayout, container, userID);
            container.add(favoritesPage, "FavoritesPage");
            cardLayout.show(container, "FavoritesPage");
        });

        cartButton.addActionListener(e -> {
            CartPage cartPage = new CartPage(cardLayout, container, userID);
            container.add(cartPage, "CartPage");
            cardLayout.show(container, "CartPage");
        });

        // Stats Panel: Card Layout
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(2, 3, 20, 20)); // Updated to 2x3 grid for 6 cards
        statsPanel.setBounds(400, 200, 1200, 400);
        statsPanel.setOpaque(false);
        add(statsPanel);

        // Create individual cards for each statistic
        JPanel usersCountCard = createStatsCard("Барлық тіркелген қолданушылар:", "0");
        JPanel housesSoldCard = createStatsCard("Сатылған үйлер саны:", "0");
        JPanel avgPriceCard = createStatsCard("Орташа үй бағасы:", "0.00");
        JPanel topCitiesCard = createStatsCard("Көшбасшы қала:", "Unknown");

        // Additional cards with just text
        JPanel tipsCard = createTextCard("Кеңес:", "Жылжымайтын мүлік сатып алу кезінде орынды бағалаңыз.");
        JPanel messageCard = createTextCard("Хабарлама:", "Сатылым маусымын жіберіп алмаңыз!");

        // Add cards to the panel
        statsPanel.add(usersCountCard);
        statsPanel.add(housesSoldCard);
        statsPanel.add(avgPriceCard);
        statsPanel.add(topCitiesCard);
        statsPanel.add(tipsCard);
        statsPanel.add(messageCard);

        // Load data to update the statistics
        loadData(usersCountCard, housesSoldCard, avgPriceCard, topCitiesCard);
    }

    private JPanel createStatsCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(255, 255, 255));
        card.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204), 2));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTextCard(String title, String message) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(255, 255, 255));
        card.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204), 2));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(messageLabel, BorderLayout.CENTER);

        return card;
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setIcon(new ImageIcon(iconPath));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 90, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }

    private void loadData(JPanel usersCountCard, JPanel housesSoldCard, JPanel avgPriceCard, JPanel topCitiesCard) {
        Connection connection = DBConnection.getConnection();
        try {
            String usersCountQuery = "SELECT COUNT(*) AS totalUsers FROM \"User\"";
            PreparedStatement usersCountStmt = connection.prepareStatement(usersCountQuery);
            ResultSet usersCountResult = usersCountStmt.executeQuery();
            if (usersCountResult.next()) {
                int totalUsers = usersCountResult.getInt("totalUsers");
                JLabel usersCountLabel = (JLabel) usersCountCard.getComponent(1);
                usersCountLabel.setText(String.valueOf(totalUsers));
            }

            String housesSoldQuery = "SELECT COUNT(*) AS totalSold FROM Property WHERE Status = 'sold'";
            PreparedStatement housesSoldStmt = connection.prepareStatement(housesSoldQuery);
            ResultSet housesSoldResult = housesSoldStmt.executeQuery();
            if (housesSoldResult.next()) {
                int totalSold = housesSoldResult.getInt("totalSold");
                JLabel housesSoldLabel = (JLabel) housesSoldCard.getComponent(1);
                housesSoldLabel.setText(String.valueOf(totalSold));
            }

            String avgPriceQuery = "SELECT AVG(Price) AS avgPrice FROM Property WHERE Status = 'sold'";
            PreparedStatement avgPriceStmt = connection.prepareStatement(avgPriceQuery);
            ResultSet avgPriceResult = avgPriceStmt.executeQuery();
            if (avgPriceResult.next()) {
                double avgPrice = avgPriceResult.getDouble("avgPrice");
                JLabel avgPriceLabel = (JLabel) avgPriceCard.getComponent(1);
                avgPriceLabel.setText(String.format("%.2f", avgPrice));
            }

            String topCitiesQuery = "SELECT c.CityName, COUNT(*) AS propertyCount " +
                    "FROM Property p " +
                    "JOIN City c ON p.CityID = c.CityID " +
                    "GROUP BY c.CityName " +
                    "ORDER BY propertyCount DESC " +
                    "LIMIT 1";
            PreparedStatement topCitiesStmt = connection.prepareStatement(topCitiesQuery);
            ResultSet topCitiesResult = topCitiesStmt.executeQuery();
            if (topCitiesResult.next()) {
                String topCity = topCitiesResult.getString("CityName");
                int propertyCount = topCitiesResult.getInt("propertyCount");
                JLabel topCitiesLabel = (JLabel) topCitiesCard.getComponent(1);
                topCitiesLabel.setText(topCity + " (" + propertyCount + " мүлік)");
            }

            usersCountResult.close();
            usersCountStmt.close();
            housesSoldResult.close();
            housesSoldStmt.close();
            avgPriceResult.close();
            avgPriceStmt.close();
            topCitiesResult.close();
            topCitiesStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}

















