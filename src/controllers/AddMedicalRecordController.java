package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.MedicalRecord;

import java.io.IOException;

public class AddMedicalRecordController {

    private int appointmentID; // Biến lưu appointmentID được truyền vào

    @FXML
    private TextField diagnosisField;
    @FXML
    private TextField treatmentField;
    @FXML
    private TextField resultField;

    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;

    // Phương thức khởi tạo
    public void initialize() {
        addButton.setOnAction(event -> addMedicalRecord());
        cancelButton.setOnAction(event -> cancelAdd());
    }

    // Setter để nhận appointmentID từ CompletedController
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    // Sự kiện thêm hồ sơ bệnh án
    @FXML
    public void addMedicalRecord() {
        try {
            String diagnosis = diagnosisField.getText();
            String treatment = treatmentField.getText();
            String result = resultField.getText();

            // Kiểm tra các trường thông tin
            if (diagnosis.isEmpty() || treatment.isEmpty() || result.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled.");
                return;
            }

            // Thêm hồ sơ bệnh án mới vào cơ sở dữ liệu
            MedicalRecord newRecord = new MedicalRecord(0, appointmentID, "", "", null, diagnosis, treatment, result);
            boolean isAdded = newRecord.addMedicalRecord();

            if (isAdded) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Medical Record added successfully.");
                goToMedicalList(); // Đóng form
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add Medical Record.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while accessing the database.");
        }
    }

    // Sự kiện hủy và đóng form
    @FXML


    // Đóng cửa sổ hiện tại

    // Hiển thị thông báo
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void goToMedicalList() {
        try {
            // Mở màn hình danh sách bác sĩ
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard/MedicalRecordForm.fxml"));
            Scene doctorListScene = new Scene(loader.load());

            // Lấy Stage hiện tại và chuyển sang màn hình mới
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(doctorListScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void cancelAdd() {
        diagnosisField.clear();
        treatmentField.clear();
        resultField.clear();
    }
}
