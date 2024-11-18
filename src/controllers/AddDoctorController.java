package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import model.Doctor;

import java.io.IOException;

public class AddDoctorController {

    @FXML private TextField doctorNameField;
    @FXML private TextField doctorSpecialtyField;
    @FXML private TextField doctorScheduledField;
    @FXML private TextField doctorContactField;
    @FXML private TextField doctorEmailField;
    @FXML private Button addButton;
    @FXML private Button cancelButton;

    private Doctor doctor;

    public void initialize() {
        addButton.setOnAction(event -> addDoctor());
        cancelButton.setOnAction(event -> cancelAddDoctor());
    }

    private void addDoctor() {
        String name = doctorNameField.getText();
        String specialty = doctorSpecialtyField.getText();
        String workSchedule = doctorScheduledField.getText();
        String contact = doctorContactField.getText();
        String email = doctorEmailField.getText();

        if (name.isEmpty() || specialty.isEmpty() || workSchedule.isEmpty() || contact.isEmpty() || email.isEmpty()) {
            System.out.println("Please fill all fields!");
            return;
        }

        doctor = new Doctor(0, name, specialty, workSchedule, contact, email);
        boolean success = Doctor.addDoctor(doctor);

        if (success) {
            System.out.println("Doctor added successfully!");
            goToDoctorList(); // Chuyển về danh sách bác sĩ sau khi thêm thành công
        } else {
            System.out.println("Failed to add doctor!");
        }
    }

    private void goToDoctorList() {
        try {
            // Mở màn hình danh sách bác sĩ
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard/DoctorForm.fxml"));
            Scene doctorListScene = new Scene(loader.load());

            // Lấy Stage hiện tại và chuyển sang màn hình mới
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(doctorListScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cancelAddDoctor() {
        clearFields();
        // Có thể đóng cửa sổ AddDoctor hoặc không làm gì tùy vào yêu cầu.

    }

    private void clearFields() {
        doctorNameField.clear();
        doctorSpecialtyField.clear();
        doctorScheduledField.clear();
        doctorContactField.clear();
        doctorEmailField.clear();
    }
}
