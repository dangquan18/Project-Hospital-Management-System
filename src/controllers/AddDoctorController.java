package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import model.Doctor;
import utils.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class AddDoctorController {

    @FXML private TextField doctorNameField;
    @FXML private ComboBox<String> specialtyComboBox; // ComboBox thay cho TextField
    @FXML private TextField doctorScheduledField;
    @FXML private TextField doctorContactField;
    @FXML private TextField doctorEmailField;
    @FXML private Button addButton;
    @FXML private Button cancelButton;

    private Doctor doctor;
    private Map<String, Integer> specialtyMap = new HashMap<>(); // Lưu trữ name -> ID của specialty

    public void initialize() {
        loadSpecialties(); // Tải danh sách chuyên khoa từ cơ sở dữ liệu

        addButton.setOnAction(event -> addDoctor());
        cancelButton.setOnAction(event -> cancelAddDoctor());
    }

    private void loadSpecialties() {
        String query = "SELECT * FROM specialty"; // Thay đổi tên bảng nếu cần
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ObservableList<String> specialties = FXCollections.observableArrayList();
            while (rs.next()) {
                int id = rs.getInt("specialtyID");
                String name = rs.getString("name");
                specialtyMap.put(name, id); // Map tên chuyên khoa với ID
                specialties.add(name);
            }
            specialtyComboBox.setItems(specialties); // Đặt danh sách vào ComboBox

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addDoctor() {
        String name = doctorNameField.getText();
        String specialty = specialtyComboBox.getSelectionModel().getSelectedItem(); // Lấy giá trị từ ComboBox
        Integer specialtyID = specialtyMap.get(specialty); // Lấy ID từ map
        String workSchedule = doctorScheduledField.getText();
        String contact = doctorContactField.getText();
        String email = doctorEmailField.getText();

        if (name.isEmpty() || specialty == null || workSchedule.isEmpty() || contact.isEmpty() || email.isEmpty()) {
            System.out.println("Please fill all fields!");
            return;
        }

        doctor = new Doctor(0, name, specialtyID, workSchedule, contact, email ,"Active");
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
        specialtyComboBox.getSelectionModel().clearSelection();
        doctorScheduledField.clear();
        doctorContactField.clear();
        doctorEmailField.clear();
    }
}
