package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.Appointment;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class AssignDoctorController {

    @FXML
    private ComboBox<String> specialComboBox;

    @FXML
    private ComboBox<String> doctorComboBox;
    private int appointmentID;
    private ObservableList<String> doctorList = FXCollections.observableArrayList();
    private HashMap<String, String> specialMap = new HashMap<>(); // Lưu ánh xạ giữa specialName và specialID

    public void initialize() {
        loadSpecials(); // Load danh sách chuyên khoa
        specialComboBox.setOnAction(event -> loadDoctors()); // Khi chọn chuyên khoa, load danh sách bác sĩ
    }
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
        System.out.println("Received Appointment ID: " + appointmentID);
    }

    // Load danh sách chuyên khoa (specialID và specialName)
    private void loadSpecials() {
        String query = "SELECT specialtyID, name AS specialName FROM specialty";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String specialID = rs.getString("specialtyID");
                String specialName = rs.getString("specialName");

                // Lưu ánh xạ name -> ID
                specialMap.put(specialName, specialID);
                specialComboBox.getItems().add(specialName); // Hiển thị tên trong ComboBox
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load danh sách bác sĩ theo chuyên khoa đã chọn
    private void loadDoctors() {
        doctorList.clear(); // Xóa danh sách bác sĩ cũ
        String selectedSpecialName = specialComboBox.getSelectionModel().getSelectedItem();

        if (selectedSpecialName != null) {
            String selectedSpecialID = specialMap.get(selectedSpecialName); // Lấy specialID từ name
            String query = "SELECT doctorID FROM doctor WHERE specialtyID  = ? AND status = 'Active'";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setString(1, selectedSpecialID);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    doctorList.add(rs.getString("doctorID"));
                }

                doctorComboBox.setItems(doctorList);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // Phân công bác sĩ
    @FXML
    private void assignDoctor() {
        String selectedSpecialName = specialComboBox.getSelectionModel().getSelectedItem();
        String selectedDoctor = doctorComboBox.getSelectionModel().getSelectedItem();

        if (selectedSpecialName == null || selectedDoctor == null) {
            showAlert("Lỗi", "Vui lòng chọn chuyên khoa và bác sĩ.", AlertType.ERROR);
            return;
        }

        String selectedSpecialID = specialMap.get(selectedSpecialName); // Lấy specialID từ name

        // Giả sử bạn có ID cuộc hẹn (appointmentID) cần phân công
        int appointmentID = this.appointmentID; // Thay bằng ID cuộc hẹn thực tế

        String query = "UPDATE appointment SET doctorID = ?, specialtyID = ?, status = 'Assigned' WHERE appointmentID = ?";
        String query2 = "UPDATE doctor SET status = 'Busy' WHERE doctorID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query))
        {

            pstmt.setString(1, selectedDoctor);
            pstmt.setString(2, selectedSpecialID); // Lưu specialID thay vì name
            pstmt.setInt(3, appointmentID);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Thành công", "Phân công bác sĩ thành công!", AlertType.INFORMATION);
            } else {
                showAlert("Thất bại", "Không thể phân công bác sĩ. Vui lòng thử lại.", AlertType.ERROR);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi xảy ra khi phân công bác sĩ.", AlertType.ERROR);
        }
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt2 = connection.prepareStatement(query2))
        {

            pstmt2.setString(1, selectedDoctor);


            int rowsAffected = pstmt2.executeUpdate();



        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi xảy ra khi phân công bác sĩ.", AlertType.ERROR);
        }
    }

    // Hiển thị thông báo
    private void showAlert(String title, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


}
