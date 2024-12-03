import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {
    private Image backgroundImage;

    public HomePage(CardLayout cardLayout, JPanel container) {
        setLayout(null);

        // Загрузка фонового изображения
        backgroundImage = new ImageIcon(getClass().getResource("/mainhomepage.png")).getImage(); // Путь к изображению

        // Создаем общий навигационный панель
        NavigationPanel navigationPanel = new NavigationPanel(cardLayout, container);
        navigationPanel.setBounds(0, 0, 1940, 50);
        add(navigationPanel);

        JLabel label = new JLabel("Қош келдіңіз", JLabel.CENTER);
        label.setBounds(600, 100, 600, 50);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label);

        setPreferredSize(new Dimension(1940, 1080));

        // Обновите интерфейс
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Рисуем фоновое изображение
    }
}



