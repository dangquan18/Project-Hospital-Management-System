package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hospital_management";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public DatabaseConnection() {
    }

    public static Connection getConnection() {
        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_management", "root", "");
        } catch (ClassNotFoundException var2) {
            ClassNotFoundException e = var2;
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException var3) {
            SQLException e = var3;
            System.out.println("Connection failed.");
            e.printStackTrace();
        } catch (Exception var4) {
            System.out.println("Failed to connect");
        }

        return connection;
    }
}