package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.HBox;
import model.Appointment;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class CompletedController {

    @FXML
    private TableView<Appointment> AppointListTableView;

    @FXML
    private TableColumn<Appointment, Integer> AppointID;
    @FXML
    private TableColumn<Appointment, String> AppointListPatientName;
    @FXML
    private TableColumn<Appointment, String> AppointListSymptoms;
    @FXML
    private TableColumn<Appointment, String> AppointListSpecialty;
    @FXML
    private TableColumn<Appointment, String> AppointListDoctorName;
    @FXML
    private TableColumn<Appointment, String> AppointListScheduledTime;
    @FXML
    private TableColumn<Appointment, Void> AppointListAction;

    private ObservableList<Appointment> appointments;

    public void initialize() {
        try {
            loadAppointments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAppointments() {
        String query = "SELECT a.appointmentID, p.name AS patientName, a.symptoms, s.name AS specialtyName, d.name AS doctorName, a.scheduledTime " +
                "FROM appointment a " +
                "JOIN patient p ON a.patientID = p.patientID " +
                "JOIN doctor d ON a.doctorID = d.doctorID " +
                "JOIN specialty s ON d.specialtyID = s.specialtyID " +
                "WHERE a.status = 'Completed'"
                + "ORDER BY a.scheduledTime DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            appointments = FXCollections.observableArrayList();

            while (rs.next()) {
                int appointmentID = rs.getInt("appointmentID");
                String patientName = rs.getString("patientName");
                String symptoms = rs.getString("symptoms");
                String specialtyName = rs.getString("specialtyName");
                String doctorName = rs.getString("doctorName");
                String scheduledTime = rs.getString("scheduledTime");

                appointments.add(new Appointment(appointmentID, patientName, symptoms, specialtyName, doctorName, Timestamp.valueOf(scheduledTime), "Completed"));
            }

            // Gán dữ liệu cho các cột trong bảng
            AppointID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAppointmentID()).asObject());
            AppointListPatientName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatientName()));
            AppointListSymptoms.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSymptoms()));
            AppointListSpecialty.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialtyName()));
            AppointListDoctorName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDoctorName()));
            AppointListScheduledTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getScheduledTime().toString()));
            AppointListAction.setCellFactory(param -> new TableCell<Appointment, Void>() {
                private final Button addRecord = new Button("addRecord");

                {
                    addRecord.setOnAction(event -> {
                        Appointment selectedAppointment = getTableView().getItems().get(getIndex());
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard/AddMedicalRecordForm.fxml"));
                            Parent root = loader.load();
                            AddMedicalRecordController controller = loader.getController();
                            controller.setAppointmentID(selectedAppointment.getAppointmentID());
                            Stage currentStage = (Stage) AppointListTableView.getScene().getWindow();
                            Scene newScene = new Scene(root);
                            currentStage.setScene(newScene);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Appointment appointment = getTableView().getItems().get(getIndex());
                        // Kiểm tra xem appointmentID có tồn tại trong medical_record không
                        if (isRecordExist(appointment.getAppointmentID())) {
                            setGraphic(null); // Nếu có bản ghi, ẩn nút
                        } else {
                            HBox hbox = new HBox(10, addRecord);
                            setGraphic(hbox); // Nếu không có bản ghi, hiển thị nút
                        }
                    }
                }
            });

            AppointListTableView.setItems(appointments);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Phương thức kiểm tra sự tồn tại của appointmentID trong bảng medical_record
    private boolean isRecordExist(int appointmentID) {
        String query = "SELECT COUNT(*) FROM medicalrecord WHERE appointmentID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, appointmentID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Nếu số lượng > 0 thì đã có bản ghi
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}
