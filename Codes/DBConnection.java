import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/RealEstate";  // Ensure database name is correct
    private static final String USER = "postgres"; // Ensure username is correct
    private static final String PASSWORD = "admin"; // Ensure password is correct

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");

            System.out.println("Attempting to connect to database...");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Database connection established successfully!");
            return connection;

        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found. Please ensure the driver is added to your classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection failed. SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}



