import javax.swing.*;
import java.awt.*;

public class NavigationPanel extends JPanel {
    public NavigationPanel(CardLayout cardLayout, JPanel container) {
        setLayout(new FlowLayout(FlowLayout.CENTER));

        // Создание кнопки с изображением
        JButton imageButton = createImageButton("C:\\Users\\asus\\IdeaProjects\\RealEstate\\src\\housetemplate.png");

        // Создание кнопок с измененным цветом
        JButton homeButton = createNavButton("Басты бет");
        JButton aboutButton = createNavButton("Біз жайлы");
        JButton loginButton = createNavButton("Кіру");
        JButton registerButton = createNavButton("Тіркелу");

        // Добавление кнопок на панель
        add(imageButton);
        add(homeButton);
        add(aboutButton);
        add(loginButton);
        add(registerButton);

        imageButton.addActionListener(e -> System.out.println("Image button clicked!"));
        homeButton.addActionListener(e -> cardLayout.show(container, "Home"));
        aboutButton.addActionListener(e -> cardLayout.show(container, "About"));
        loginButton.addActionListener(e -> cardLayout.show(container, "Login"));
        registerButton.addActionListener(e -> cardLayout.show(container, "Register"));
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));

        // Увеличиваем шрифт кнопки
        button.setFont(new Font("Arial", Font.BOLD, 16));

        return button;
    }

    private JButton createImageButton(String imagePath) {
        JButton button = new JButton();
        button.setFocusPainted(false);

        // Загрузка изображения и масштабирование
        ImageIcon icon = new ImageIcon(imagePath);
        Image scaledImage = icon.getImage().getScaledInstance(70, 40, Image.SCALE_SMOOTH); // Scale image to 40x40
        icon = new ImageIcon(scaledImage);
        button.setIcon(icon);

        // Задаем размер кнопки
        button.setPreferredSize(new Dimension(70, 50));

        // Убираем рамку вокруг кнопки
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);

        return button;
    }
}





