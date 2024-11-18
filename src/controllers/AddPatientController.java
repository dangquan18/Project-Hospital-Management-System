package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import model.Patient;

import java.io.IOException;

public class AddPatientController {

    @FXML private TextField patientNameField;
    @FXML private TextField patientAgeField;
    @FXML private TextField patientContactField;
    @FXML private TextField patientEmailField;
    @FXML private Button addButton;
    @FXML private Button cancelButton;

    private Patient patient;

    public void initialize() {
        addButton.setOnAction(event -> addPatient());
        cancelButton.setOnAction(event -> cancelAddPatient());
    }

    private void addPatient() {
        String name = patientNameField.getText();
        String age = patientAgeField.getText();
        String contact = patientContactField.getText();
        String email = patientEmailField.getText();

        if (name.isEmpty() || age.isEmpty() ||  contact.isEmpty() || email.isEmpty()) {
            System.out.println("Please fill all fields!");
            return;
        }

        patient = new Patient(0, name, Integer.parseInt(age), contact, email);
        boolean success = Patient.addPatient(patient);

        if (success) {
            System.out.println("Patient added successfully!");
            goToPatientList(); // Chuyển về danh sách bác sĩ sau khi thêm thành công
        } else {
            System.out.println("Failed to add Patient!");
        }
    }

    private void goToPatientList() {
        try {
            // Mở màn hình danh sách bác sĩ
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard/PatientForm.fxml"));
            Scene doctorListScene = new Scene(loader.load());

            // Lấy Stage hiện tại và chuyển sang màn hình mới
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(doctorListScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cancelAddPatient() {
        clearFields();
        // Có thể đóng cửa sổ AddDoctor hoặc không làm gì tùy vào yêu cầu.

    }

    private void clearFields() {
        patientNameField.clear();
        patientAgeField.clear();
        patientContactField.clear();
        patientEmailField.clear();
    }
}