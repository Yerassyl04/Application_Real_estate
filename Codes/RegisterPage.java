import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterPage extends JPanel {
    private JTextField snameField, mnameField, lnameField, passportField, emailField, cityField, phoneField, addressField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public RegisterPage(CardLayout cardLayout, JPanel container) {
        setLayout(new BorderLayout());

        // Navigation panel at the top
        NavigationPanel navigationPanel = new NavigationPanel(cardLayout, container);
        navigationPanel.setBounds(0, 0, 1940, 50); // Full-width navigation bar
        add(navigationPanel, BorderLayout.NORTH);

        // Background panel for gradient
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

        // Center panel for register form (aligned to the top)
        JPanel formPanel = new JPanel();
        formPanel.setBounds(700, 50, 450, 880); // Start from top and adjust height to fit all fields and button
        formPanel.setLayout(null);
        formPanel.setBackground(new Color(255, 255, 255, 220)); // Semi-transparent white background
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));

        // Title label
        JLabel titleLabel = new JLabel("Тіркелу", JLabel.CENTER); // Title in Kazakh
        titleLabel.setBounds(100, 20, 250, 50);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 30));
        titleLabel.setForeground(new Color(33, 150, 243));
        formPanel.add(titleLabel);

        // First Name
        JLabel snameLabel = createLabel("Аты:");
        snameLabel.setBounds(50, 100, 350, 20);
        snameField = createTextField();
        snameField.setBounds(50, 130, 350, 40);
        formPanel.add(snameLabel);
        formPanel.add(snameField);

        // Middle Name
        JLabel mnameLabel = createLabel("Әкесінің аты:");
        mnameLabel.setBounds(50, 180, 350, 20);
        mnameField = createTextField();
        mnameField.setBounds(50, 210, 350, 40);
        formPanel.add(mnameLabel);
        formPanel.add(mnameField);

        // Last Name
        JLabel lnameLabel = createLabel("Тегі:");
        lnameLabel.setBounds(50, 260, 350, 20);
        lnameField = createTextField();
        lnameField.setBounds(50, 290, 350, 40);
        formPanel.add(lnameLabel);
        formPanel.add(lnameField);

        // Passport ID
        JLabel passportLabel = createLabel("Паспорт ID:");
        passportLabel.setBounds(50, 340, 350, 20);
        passportField = createTextField();
        passportField.setBounds(50, 370, 350, 40);
        formPanel.add(passportLabel);
        formPanel.add(passportField);

        // Email
        JLabel emailLabel = createLabel("Email:");
        emailLabel.setBounds(50, 420, 350, 20);
        emailField = createTextField();
        emailField.setBounds(50, 450, 350, 40);
        formPanel.add(emailLabel);
        formPanel.add(emailField);

        // Password
        JLabel passwordLabel = createLabel("Құпиясөз:");
        passwordLabel.setBounds(50, 500, 350, 20);
        passwordField = createPasswordField();
        passwordField.setBounds(50, 530, 350, 40);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        // City
        JLabel cityLabel = createLabel("Қала:");
        cityLabel.setBounds(50, 580, 350, 20);
        cityField = createTextField();
        cityField.setBounds(50, 610, 350, 40);
        formPanel.add(cityLabel);
        formPanel.add(cityField);

        // Phone
        JLabel phoneLabel = createLabel("Телефон:");
        phoneLabel.setBounds(50, 660, 350, 20);
        phoneField = createTextField();
        phoneField.setBounds(50, 690, 350, 40);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);

        // Address
        JLabel addressLabel = createLabel("Мекен-жай:");
        addressLabel.setBounds(50, 740, 350, 20);
        addressField = createTextField();
        addressField.setBounds(50, 770, 350, 40);
        formPanel.add(addressLabel);
        formPanel.add(addressField);

        // Register Button
        JButton registerButton = new JButton("Тіркелу");
        registerButton.setBounds(50, 820, 350, 50);
        registerButton.setFont(new Font("Roboto", Font.BOLD, 16));
        registerButton.setBackground(new Color(33, 150, 243));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> {
            String sname = snameField.getText();
            String mname = mnameField.getText();
            String lname = lnameField.getText();
            String passportId = passportField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String city = cityField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();
            registerUser(sname, mname, lname, passportId, email, password, city, phone, address);
        });

        // Message label for feedback
        messageLabel = new JLabel("", JLabel.CENTER);
        messageLabel.setBounds(50, 880, 350, 30);
        messageLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(255, 0, 0));

        // Add components to form panel
        formPanel.add(registerButton);
        formPanel.add(messageLabel);

        backgroundPanel.add(formPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Roboto", Font.BOLD, 14));
        label.setForeground(new Color(80, 80, 80));
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Roboto", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(33, 150, 243), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Roboto", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(33, 150, 243), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return passwordField;
    }

    private void registerUser(String sname, String mname, String lname, String passportId, String email, String password, String city, String phone, String address) {
        String sql = "INSERT INTO \"User\" (Sname, Mname, Lname, Passport_id, Email, Password, City, Phone, Address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sname);
            pstmt.setString(2, mname);
            pstmt.setString(3, lname);
            pstmt.setString(4, passportId);
            pstmt.setString(5, email);
            pstmt.setString(6, password);
            pstmt.setString(7, city);
            pstmt.setString(8, phone);
            pstmt.setString(9, address);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Қолданушы сәтті тіркелді!");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Тіркеу кезінде қате пайда болды: " + ex.getMessage());
        }
    }
}






















