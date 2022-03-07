package jdbc;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;

public class Statements {
    private static Connection connection;

    static {
        try {
            connection = H2Config.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertAverageRates(Map<String, String> rates) {
        String query = "INSERT INTO AVERAGE_RATES(CURRENCY_SYMBOL, AVERAGE_RATE, CREATED_AT) VALUES (?, ?, ?)";

        rates.forEach((key, value) -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, key);
                preparedStatement.setString(2, value);
                preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                System.out.println(exception.getMessage());
            }
        });
    }

    public static void insertBuyAndSellRates(Map<String, String> rates) {
        String query = "INSERT INTO BUY_SELL_RATES(CURRENCY_SYMBOL, BUY_RATE, SELL_RATE, CREATED_AT) VALUES (?, ?, ?, ?)";

        rates.forEach((key, value) -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, key);
                preparedStatement.setString(2, value.split(";")[0]);
                preparedStatement.setString(3, value.split(";")[1]);
                preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                System.out.println(exception.getMessage());
            }
        });
    }

    public static void printAllResults(String tableName) {
        String query = "SELECT * FROM " + tableName;

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    System.out.println(resultSetMetaData.getColumnName(i) + ": " + resultSet.getString(i));
                }
            }
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
