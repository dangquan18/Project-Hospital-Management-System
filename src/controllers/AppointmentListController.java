package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.HBox;
import model.Appointment;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class AppointmentListController {

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
        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

    private void loadAppointments() {
        String query = "SELECT a.appointmentID, p.name AS patientName, a.symptoms, s.name AS specialtyName, d.name AS doctorName, a.scheduledTime " +
                "FROM appointment a " +
                "JOIN patient p ON a.patientID = p.patientID " +
                "JOIN doctor d ON a.doctorID = d.doctorID " +
                "JOIN specialty s ON d.specialtyID = s.specialtyID " +
                "WHERE a.status = 'Assigned'";

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

                appointments.add(new Appointment(appointmentID, patientName, symptoms, specialtyName, doctorName, Timestamp.valueOf(scheduledTime), "Assigned"));
            }

            // Gán dữ liệu cho các cột trong bảng
            AppointID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAppointmentID()).asObject());
            AppointListPatientName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatientName()));
            AppointListSymptoms.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSymptoms()));
            AppointListSpecialty.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialtyName()));
            AppointListDoctorName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDoctorName()));
            AppointListScheduledTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getScheduledTime().toString()));
            AppointListAction.setCellFactory(param -> new TableCell<Appointment, Void>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.setStyle(
                            "-fx-background-color: #f44336;" +
                                    "    -fx-text-fill: white;" +
                                    "    -fx-font-size: 14px;" +
                                    "    -fx-padding: 5px 20px;" +
                                    "    -fx-border-radius: 5px;" +
                                    "    -fx-cursor: hand;"
                    );
                    deleteButton.setOnAction(event -> {
                        Appointment appointment = getTableView().getItems().get(getIndex());
                        handleDeleteAppointment(appointment);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox hbox = new HBox(10, deleteButton);
                        setGraphic(hbox);
                    }
                }
            });

            // Hiển thị danh sách các cuộc hẹn trong bảng
            AppointListTableView.setItems(appointments);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "An error occurred while accessing the database.");
        }
    }

    private void handleDeleteAppointment(Appointment appointment) {
        try {
            if (appointment != null && Appointment.deleteAppointment(appointment.getAppointmentID())) {
                loadAppointments();
            } else {
                showErrorDialog("Delete Failed", "Failed to delete Appointment.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "An error occurred while accessing the database.");
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}