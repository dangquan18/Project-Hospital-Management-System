package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.Appointment;
import model.Doctor;
import utils.DatabaseConnection;

import java.sql.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Appointment;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AppointmentController {

    @FXML
    private TableView<Appointment> AppointTableView;

    @FXML
    private TableColumn<Appointment, Integer> AppointID;
    @FXML
    private TableColumn<Appointment, String> AppointPatientName;
    @FXML
    private TableColumn<Appointment, String> AppointSymptoms;
    @FXML
    private TableColumn<Appointment, String> AppointSheduledTime;
    @FXML
    private TableColumn<Appointment, Void> AppointAction; // Cột chứa nút "Assign"

    private ObservableList<Appointment> appointments;

    public void initialize() {
        loadAppointments();
    }

    private void loadAppointments() {
        String query = "SELECT a.appointmentID, p.name AS patientName, a.symptoms, a.scheduledTime, a.status " +
                "FROM appointment a " +
                "JOIN patient p ON a.patientID = p.patientID " +
                "WHERE a.status = 'Pending'";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            appointments = FXCollections.observableArrayList();

            while (rs.next()) {
                int appointmentID = rs.getInt("appointmentID");
                String patientName = rs.getString("patientName");
                String symptoms = rs.getString("symptoms");
                String scheduledTime = rs.getString("scheduledTime");

                appointments.add(new Appointment(appointmentID,  patientName,  symptoms,"","", java.sql.Timestamp.valueOf(scheduledTime), "Pending"));
            }

            // Gán dữ liệu cho các cột
            AppointID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            AppointPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
            AppointSymptoms.setCellValueFactory(new PropertyValueFactory<>("symptoms"));
            AppointSheduledTime.setCellValueFactory(new PropertyValueFactory<>("scheduledTime"));

            // Thêm cột Action với nút "Assign"
            AppointAction.setCellFactory(param -> new TableCell<>() {
                private final Button assignButton = new Button("Assign");
                private final Button DeleteButton = new Button("Delete");


                {
                    DeleteButton.setStyle(
                            "-fx-background-color: #f44336;" +
                                    "    -fx-text-fill: white;" +
                                    "    -fx-font-size: 14px;" +
                                    "    -fx-padding: 5px 20px;" +
                                    "    -fx-border-radius: 5px;" +
                                    "    -fx-cursor: hand;"        /* Bo góc nền */
                    );
                    assignButton.setOnAction(event -> {
                        Appointment selectedAppointment = getTableView().getItems().get(getIndex());
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard/Assign.fxml"));
                            Parent root = loader.load();
                            AssignDoctorController controller = loader.getController();
                            controller.setAppointmentID(selectedAppointment.getAppointmentID());
                            Stage currentStage = (Stage) AppointTableView.getScene().getWindow();
                            Scene newScene = new Scene(root);
                            currentStage.setScene(newScene);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    DeleteButton.setOnAction(event -> {
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
                        HBox hbox = new HBox(10, assignButton, DeleteButton);
                        setGraphic(hbox);
                    }
                }
            });

            AppointTableView.setItems(appointments);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML

    private void handleDeleteAppointment(Appointment appointment) {
        if (appointment != null && Appointment.deleteAppointment(appointment.getAppointmentID())) {
            loadAppointments();
        } else {
            showErrorDialog("Delete Failed", "Failed to delete Appointment.");
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