package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Config {
    private static Connection connection;

    public static Connection getConnection() {
        String url = "jdbc:h2:tcp://localhost:9092/~/tmp/h2dbs/testdb";
        String user = "sa";
        String password = "";
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
