package controllers;

import utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.DoctorHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorHistoryController {

    @FXML
    private TableView<DoctorHistory> HistoryTableView;

    @FXML
    private TableColumn<DoctorHistory, Integer> history_col_id;

    @FXML
    private TableColumn<DoctorHistory, String> history_col_doctorName;

    @FXML
    private TableColumn<DoctorHistory, String> history_col_Specialty;

    @FXML
    private TableColumn<DoctorHistory, String> history_col_patientName;

    @FXML
    private TableColumn<DoctorHistory, Timestamp> history_col_scheduledTime;

    // Phương thức để hiển thị dữ liệu vào TableView
    public void initialize() {
        ObservableList<DoctorHistory> doctorHistoryList = FXCollections.observableArrayList(getDoctorHistoryFromDatabase());

        history_col_id.setCellValueFactory(new PropertyValueFactory<>("historyId"));
        history_col_doctorName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        history_col_Specialty.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        history_col_patientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        history_col_scheduledTime.setCellValueFactory(new PropertyValueFactory<>("scheduledTime"));

        HistoryTableView.setItems(doctorHistoryList);
    }

    // Phương thức lấy dữ liệu từ database và trả về một danh sách các DoctorHistory
    private List<DoctorHistory> getDoctorHistoryFromDatabase() {
        List<DoctorHistory> doctorHistoryList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Truy vấn kết hợp bảng doctorhistory, doctor, appointment, patient và specialty
            String query = "SELECT dh.historyID, dh.doctorID, dh.appointmentID, d.name AS doctorName, " +
                    "       s.name AS specialtyName, p.name AS patientName, a.scheduledTime " +
                    "FROM doctorhistory dh " +
                    "JOIN doctor d ON dh.doctorID = d.doctorID " +
                    "JOIN appointment a ON dh.appointmentID = a.appointmentID " +
                    "JOIN patient p ON a.patientID = p.patientID " +
                    "JOIN specialty s ON d.specialtyID = s.specialtyID;"; // Kết nối với bảng specialty

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int historyID = resultSet.getInt("historyID");
                String doctorName = resultSet.getString("doctorName");
                String specialty = resultSet.getString("specialtyName"); // Lấy thông tin chuyên khoa từ bảng specialty
                String patientName = resultSet.getString("patientName");
                Timestamp scheduledTime = resultSet.getTimestamp("scheduledTime");

                DoctorHistory doctorHistory = new DoctorHistory(historyID, doctorName, specialty, patientName, scheduledTime);
                doctorHistoryList.add(doctorHistory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doctorHistoryList;
    }}
