import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginPage(CardLayout cardLayout, JPanel container) {
        setLayout(new BorderLayout());

        // Navigation panel
        NavigationPanel navigationPanel = new NavigationPanel(cardLayout, container);
        navigationPanel.setBounds(0, 0, 1940, 50); // Full-width navigation bar
        add(navigationPanel, BorderLayout.NORTH);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(33, 150, 243), 0, getHeight(), new Color(3, 169, 244));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(null);
        add(backgroundPanel, BorderLayout.CENTER);

        // Center panel for login form
        JPanel formPanel = new JPanel();
        formPanel.setBounds(700, 200, 450, 420); // Adjusted height
        formPanel.setLayout(null);
        formPanel.setBackground(new Color(255, 255, 255, 220));
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));

        // Title label
        JLabel titleLabel = new JLabel("Кіру", JLabel.CENTER); // Title in Kazakh
        titleLabel.setBounds(100, 20, 250, 50);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 30));
        titleLabel.setForeground(new Color(33, 150, 243));
        formPanel.add(titleLabel);

        // Email label and field
        JLabel emailLabel = new JLabel("E-mail:");
        emailLabel.setBounds(50, 100, 350, 20);
        emailLabel.setFont(new Font("Roboto", Font.BOLD, 14));
        emailLabel.setForeground(new Color(80, 80, 80));

        emailField = new JTextField();
        emailField.setBounds(50, 130, 350, 40);
        emailField.setFont(new Font("Roboto", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(33, 150, 243), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Password label and field
        JLabel passwordLabel = new JLabel("Құпиясөз:");
        passwordLabel.setBounds(50, 180, 350, 20);
        passwordLabel.setFont(new Font("Roboto", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(80, 80, 80));

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 210, 350, 40);
        passwordField.setFont(new Font("Roboto", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(33, 150, 243), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Login button
        JButton loginButton = new JButton("Кіру");
        loginButton.setBounds(50, 280, 350, 50);
        loginButton.setFont(new Font("Roboto", Font.BOLD, 16));
        loginButton.setBackground(new Color(33, 150, 243));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> authenticateUser(cardLayout, container));

        // Message label
        messageLabel = new JLabel("", JLabel.CENTER);
        messageLabel.setBounds(50, 340, 350, 30);
        messageLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(255, 0, 0));

        // Add components to form panel
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);
        formPanel.add(messageLabel);

        backgroundPanel.add(formPanel);
    }

    private void authenticateUser(CardLayout cardLayout, JPanel container) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Check for admin credentials
        if (email.equals("admin") && password.equals("admin")) {
            AdminPage adminPage = new AdminPage(cardLayout, container);
            container.add(adminPage, "AdminPage");
            cardLayout.show(container, "AdminPage");
            return;
        }

        Connection connection = DBConnection.getConnection();
        if (connection != null) {
            try {
                String query = "SELECT * FROM \"User\" WHERE Email = ? AND Password = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String username = rs.getString("Sname") + " " + rs.getString("Lname");
                    int userID = rs.getInt("UserID");

                    UserDashboard dashboard = new UserDashboard(cardLayout, container, username, userID);
                    container.add(dashboard, "Dashboard");
                    cardLayout.show(container, "Dashboard");
                } else {
                    messageLabel.setText("E-mail немесе құпиясөз дұрыс емес.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                messageLabel.setText("Деректер қорына қосыла алмадық. Қайталап көріңіз.");
            }
        } else {
            messageLabel.setText("Деректер қорына қосыла алмадық.");
        }
    }
}






