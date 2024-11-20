package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SidebarController {

    @FXML
    private VBox sidebarVBox;
    @FXML
    private Button dashboardButton;
    @FXML
    private Button doctorsButton;
    @FXML
    private Button patientsButton;
    @FXML
    private Button appointmentButton;
    @FXML
    private Button medicalRecordsButton;
    @FXML
    private Button doctorHistoryButton;

    @FXML
    public void initialize() {
        // Đăng ký sự kiện cho các nút điều hướng
        dashboardButton.setOnAction(event -> loadPage("/views/Dashboard/DashBoardForm.fxml"));
        doctorsButton.setOnAction(event -> loadPage("/views/Dashboard/DoctorForm.fxml"));
        patientsButton.setOnAction(event -> loadPage("/views/Dashboard/PatientForm.fxml"));
        appointmentButton.setOnAction(event -> loadPage("/views/Dashboard/AppointmentForm.fxml"));
        medicalRecordsButton.setOnAction(event -> loadPage("/views/Dashboard/MedicalRecordForm.fxml"));
        doctorHistoryButton.setOnAction(event -> loadPage("/views/Dashboard/DoctorHistoryForm.fxml"));
    }

    private void loadPage(String fxmlFile) {
        try {
            // Lấy stage hiện tại từ một nút bất kỳ (ví dụ: dashboardButton)
            Stage stage = (Stage) dashboardButton.getScene().getWindow();
            // Tải file FXML và tạo Scene mới
            Parent root = FXMLLoader.load(getClass().getResource( fxmlFile));
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
