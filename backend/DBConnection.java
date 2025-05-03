import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sorting_visualizer";
    private static final String USER = "root";
    private static final String PASSWORD = "123@tk";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
