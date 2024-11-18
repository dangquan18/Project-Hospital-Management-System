package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Node;

public class HeaderController {

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Kiểm tra loại của đối tượng sự kiện
            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();

            // Tải FXML của trang login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login/Login.fxml"));
            StackPane loginPane = loader.load(); // Tải giao diện login

            // Đặt cảnh (Scene) mới với trang login
            Scene loginScene = new Scene(loginPane);
            currentStage.setScene(loginScene); // Chuyển sang cảnh mới

            // Hiển thị lại cửa sổ (Stage)
            currentStage.show();
        } catch (Exception e) {
            // Nếu có lỗi xảy ra khi tải trang login
            e.printStackTrace();  // In thông báo lỗi để kiểm tra

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to log out");
            alert.setContentText("An error occurred while logging out.");
            alert.showAndWait();
        }
    }

}