package model;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin {

    // Phương thức kiểm tra thông tin đăng nhập
    public boolean validateLogin(String email, String password) {
        String query = "SELECT * FROM admin WHERE email = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();  // Nếu có kết quả, tài khoản hợp lệ
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;  // Nếu không tìm thấy, tài khoản không hợp lệ
    }
}
