import javax.swing.*;
import java.awt.*;

public class AboutPage extends JPanel {
    private Image backgroundImage; // Переменная для хранения фонового изображения

    public AboutPage(CardLayout cardLayout, JPanel container) {
        setLayout(null); // Ручная компоновка

        // Загрузка фонового изображения
        backgroundImage = new ImageIcon(getClass().getResource("/aboutus.png")).getImage(); // Путь к изображению

        // Создаем общий навигационный панель
        NavigationPanel navigationPanel = new NavigationPanel(cardLayout, container);
        navigationPanel.setBounds(0, 0, 1940, 50); // Координаты и размер навигационной панели
        add(navigationPanel);

        // Установите размеры панели
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


