package DAO.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String url = "jdbc:postgresql://localhost:5432/bankGhazal";
    private static final String password = "Jppp5734";
    private static final String username = "postgres";

    public static Connection getConnection()throws SQLException {
        return DriverManager.getConnection(url,username,password);
    }
}
