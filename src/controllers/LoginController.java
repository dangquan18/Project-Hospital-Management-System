package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Admin;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    // Phương thức xử lý khi người dùng nhấn Login
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Kiểm tra email và mật khẩu
        Admin admin = new Admin();
        boolean isValid = admin.validateLogin(email, password);  // Kiểm tra hợp lệ đăng nhập

        if (isValid) {
            // Nếu đăng nhập thành công, chuyển hướng tới trang Dashboard
            try {
                // Tải giao diện dashboard.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard/DoctorForm.fxml"));
                BorderPane dashboardPane = loader.load();

                // Tạo cảnh mới cho trang Dashboard
                Stage stage = (Stage) emailField.getScene().getWindow();
                Scene dashboardScene = new Scene(dashboardPane);

                // Chuyển sang trang Dashboard
                stage.setScene(dashboardScene);
                stage.setTitle("Dashboard");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Error", "Không thể mở trang Dashboard.");
            }
        } else {
            // Nếu thông tin đăng nhập không hợp lệ
            showErrorAlert("Login Failed", "Email hoặc mật khẩu không đúng.");
        }
    }

    // Phương thức hiển thị thông báo lỗi
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
