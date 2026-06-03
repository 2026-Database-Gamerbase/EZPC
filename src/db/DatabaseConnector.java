package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost/EZPC";

    private static final String ROOT_USER = "root";
    private static final String ROOT_PASS = "";

    private static final String AUTH_USER = "ezpc_auth";
    private static final String AUTH_PASS = "";

    private static final String USER_USER = "ezpc_user";
    private static final String USER_PASS = "";

    private static final String OWNER_USER = "ezpc_owner";
    private static final String OWNER_PASS = "";

    public DatabaseConnector() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, ROOT_USER, ROOT_PASS);
    }

    public static Connection getAuthConnection() throws SQLException {
        return DriverManager.getConnection(URL, AUTH_USER, AUTH_PASS);
    }

    public static Connection getConnection(String memberType) throws SQLException {
        if ("owner".equalsIgnoreCase(memberType)) {
            return DriverManager.getConnection(URL, OWNER_USER, OWNER_PASS);
        }
        return DriverManager.getConnection(URL, USER_USER, USER_PASS);
    }
}
