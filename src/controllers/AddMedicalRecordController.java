package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.MedicalRecord;

import java.io.IOException;
import java.sql.SQLException;

public class AddMedicalRecordController {

    @FXML
    private TextField AppointmentIDField;
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


    // Hàm thiết lập controller MedicalRecordController để quay lại trang danh sách
    public void initialize() {

        addButton.setOnAction(event -> addMedicalRecord());
        cancelButton.setOnAction(event -> cancelAdd());
    }

    // Sự kiện thêm hồ sơ bệnh án
    @FXML
    public void addMedicalRecord() {
        try {
            int appointmentID = Integer.parseInt(AppointmentIDField.getText());
            String diagnosis = diagnosisField.getText();
            String treatment = treatmentField.getText();
            String result = resultField.getText();

            // Kiểm tra các trường thông tin
            if (diagnosis.isEmpty() || treatment.isEmpty() || result.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled.");
                return;
            }

            // Thêm hồ sơ bệnh án mới vào cơ sở dữ liệu
            MedicalRecord newRecord = new MedicalRecord(0, appointmentID, "", "", null,diagnosis,treatment,result);
            boolean isAdded = newRecord.addMedicalRecord();

            if (isAdded) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Medical Record added successfully.");
                goToMedicalList();
                cancelAdd();  // Đóng form thêm hồ sơ bệnh án
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add Medical Record.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Appointment ID must be a number.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while accessing the database.");
        }
    }

    // Sự kiện hủy thêm hồ sơ bệnh án và quay lại trang MedicalRecordForm
    @FXML
    private void cancelAdd() {
        AppointmentIDField.clear();
        diagnosisField.clear();
        treatmentField.clear();
        resultField.clear();
        // Quay lại trang MedicalRecordForm (sử dụng phương thức openAddMedicalRecordForm trong MedicalRecordController)
    }


    // Hàm hiển thị hộp thoại thông báo
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
        //
}}