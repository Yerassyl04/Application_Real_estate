import javax.swing.*;
import java.awt.*;
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Жылжымайтын мүлік");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1940, 1040);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        CardLayout cardLayout = new CardLayout();
        JPanel container = new JPanel(cardLayout);
        container.setLayout(cardLayout);
        // pages
        HomePage homePage = new HomePage(cardLayout, container);
        AboutPage aboutPage = new AboutPage(cardLayout, container);
        LoginPage loginPage = new LoginPage(cardLayout, container);
        RegisterPage registerPage = new RegisterPage(cardLayout, container);
        // add container
        container.add(homePage, "Home");
        container.add(aboutPage, "About");
        container.add(loginPage, "Login");
        container.add(registerPage, "Register");
        container.add(new AdminPage(cardLayout, container), "AdminPage");

        // mainn
        cardLayout.show(container, "Home");

        frame.add(container);
        frame.setVisible(true);
    }
}


