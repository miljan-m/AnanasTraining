package jdbc;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class H2Config {
    private static BasicDataSource dataSource;

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            dataSource = new BasicDataSource();
            dataSource.setUrl("jdbc:h2:tcp://localhost:9092/~/tmp/h2dbs/testdb");
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUsername("sa");
            dataSource.setPassword("");
        }
        return dataSource.getConnection();
    }
}
