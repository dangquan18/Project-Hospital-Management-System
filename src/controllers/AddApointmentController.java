package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import model.Appointment;
import model.Doctor;
import utils.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class AddApointmentController {
    @FXML
    private TextField PatientID;
    @FXML
    private TextField Symptoms;
    @FXML
    private DatePicker ScheduledTime;
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;
    private Appointment appointment;

    public void initialize() {
        addButton.setOnAction(event -> AddAppointment());
        cancelButton.setOnAction(event -> cancelAppointment());
    }

    private void AddAppointment() {
        String query = "INSERT INTO appointment (PatientID, Symptoms, ScheduledTime, status) VALUES ('" + PatientID.getText() + "', '" + Symptoms.getText() + "', '" + ScheduledTime.getValue() + "', 'Pending')";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
            goToAppointMentList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cancelAppointment() {
        clearFields();
    }

    private void clearFields() {
        PatientID.clear();
        Symptoms.clear();
        ScheduledTime.setValue(null);
    }

    private void goToAppointMentList(){
        try {
            // Mở màn hình danh sách bác sĩ
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard/AppointmentForm.fxml"));
            Scene appointmentListScene = new Scene(loader.load());

            // Lấy Stage hiện tại và chuyển sang màn hình mới
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(appointmentListScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
