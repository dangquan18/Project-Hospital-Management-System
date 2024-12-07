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
    import java.sql.PreparedStatement;
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
                    private  final  Button CompletedButton = new Button("Completed");

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
                        CompletedButton.setOnAction(event -> {
                            Appointment appointment = getTableView().getItems().get(getIndex());
                            handleCompleteAppointment(appointment);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10, deleteButton , CompletedButton);
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
            String getDoctorIdQuery = "SELECT doctorID FROM appointment WHERE appointmentID = ?";
            String updateDoctorStatusQuery = "UPDATE doctor SET status = 'Active' WHERE doctorID = ?";
            String deleteAppointmentQuery = "DELETE FROM appointment WHERE appointmentID = ?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement getDoctorIdStmt = connection.prepareStatement(getDoctorIdQuery);
                 PreparedStatement updateDoctorStatusStmt = connection.prepareStatement(updateDoctorStatusQuery);
                 PreparedStatement deleteAppointmentStmt = connection.prepareStatement(deleteAppointmentQuery)) {

                // 1. Lấy doctorID từ appointmentID
                getDoctorIdStmt.setInt(1, appointment.getAppointmentID());
                ResultSet rs = getDoctorIdStmt.executeQuery();
                int doctorID = -1;
                if (rs.next()) {
                    doctorID = rs.getInt("doctorID");
                }

                // 2. Cập nhật trạng thái bác sĩ thành 'Active' nếu tìm thấy doctorID
                if (doctorID != -1) {
                    updateDoctorStatusStmt.setInt(1, doctorID);
                    updateDoctorStatusStmt.executeUpdate();
                }

                // 3. Xóa cuộc hẹn
                deleteAppointmentStmt.setInt(1, appointment.getAppointmentID());
                deleteAppointmentStmt.executeUpdate();

                // 4. Cập nhật giao diện TableView
                appointments.remove(appointment);
                AppointListTableView.refresh();

                System.out.println("Appointment deleted and doctor set to active.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void showErrorDialog(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
        private void handleCompleteAppointment(Appointment appointment) {
            String updateAppointmentStatusQuery = "UPDATE appointment SET status = 'Completed' WHERE appointmentID = ?";
            String updateDoctorStatusQuery = "UPDATE doctor SET status = 'Active' WHERE doctorID = ?";
            String insertDoctorHistoryQuery = "INSERT INTO doctorhistory (doctorID, appointmentID) VALUES (?, ?)";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement updateAppointmentStmt = connection.prepareStatement(updateAppointmentStatusQuery);
                 PreparedStatement updateDoctorStatusStmt = connection.prepareStatement(updateDoctorStatusQuery);
                 PreparedStatement insertDoctorHistoryStmt = connection.prepareStatement(insertDoctorHistoryQuery)) {

                // 1. Cập nhật trạng thái cuộc hẹn thành "Completed"
                updateAppointmentStmt.setInt(1, appointment.getAppointmentID());
                updateAppointmentStmt.executeUpdate();
                System.out.println("Updated appointment status to 'Completed'.");

                // 2. Lấy doctorID từ appointment
                int doctorId = getDoctorIdByAppointment(appointment.getAppointmentID());
                if (doctorId == -1) {
                    System.out.println("Doctor ID not found.");
                    showErrorDialog("Error", "Doctor ID not found for this appointment.");
                    return;
                }

                // 3. Cập nhật trạng thái bác sĩ thành "Active"
                updateDoctorStatusStmt.setInt(1, doctorId);
                updateDoctorStatusStmt.executeUpdate();
                System.out.println("Doctor status set to 'Active'.");

                // 4. Thêm bản ghi vào bảng doctorhistory
                insertDoctorHistoryStmt.setInt(1, doctorId);
                insertDoctorHistoryStmt.setInt(2, appointment.getAppointmentID());
                insertDoctorHistoryStmt.executeUpdate();
                System.out.println("Inserted doctor history record.");

                // 5. Tải lại danh sách cuộc hẹn từ cơ sở dữ liệu
                loadAppointments();
                System.out.println("Appointments reloaded.");

            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("Database Error", "An error occurred while completing the appointment.");
            }
        }


        private int getDoctorIdByAppointment(int appointmentID) {
            String query = "SELECT doctorID FROM appointment WHERE appointmentID = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, appointmentID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int doctorId = rs.getInt("doctorID");
                    System.out.println("Found doctorID: " + doctorId + " for appointmentID: " + appointmentID);
                    return doctorId;
                } else {
                    System.out.println("No doctorID found for appointmentID: " + appointmentID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("Database Error", "Unable to fetch doctor ID.");
            }
            return -1; // Trả về -1 nếu không tìm thấy
        }


    }