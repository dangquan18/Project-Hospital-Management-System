package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import utils.DatabaseConnection;

public class Doctor {

    private int id;
    private String name;
    private int specialtyID;
    private String workSchedule;
    private String contactNumber;
    private String email;
    private String status;

    // Constructor
    public Doctor(int id, String name, int specialtyID, String workSchedule, String contactNumber, String email , String status) {
        this.id = id;
        this.name = name;
        this.specialtyID = specialtyID;
        this.workSchedule = workSchedule;
        this.contactNumber = contactNumber;
        this.email = email;
        this.status = "Active";

    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpecialtyID() {
        return specialtyID;
    }

    public void setSpecialtyID(int specialtyID) {
        this.specialtyID = specialtyID;
    }

    public String getWorkSchedule() {
        return workSchedule;
    }

    public void setWorkSchedule(String workSchedule) {
        this.workSchedule = workSchedule;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Kết nối đến cơ sở dữ liệu MySQL


    // Lấy danh sách bác sĩ
    public static ObservableList<Doctor> getDoctors() {
        ObservableList<Doctor> doctorList = FXCollections.observableArrayList();
        String query = "SELECT * FROM doctor"; // Thay đổi bảng và cột cho phù hợp

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                doctorList.add(new Doctor(
                        rs.getInt("doctorID"),
                        rs.getString("name"),
                        rs.getInt("specialtyID"),
                        rs.getString("workSchedule"),
                        rs.getString("contactNumber"),
                        rs.getString("email"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctorList;
    }

    // Thêm bác sĩ
    public static boolean addDoctor(Doctor doctor) {
        String query = "INSERT INTO doctor (name, specialtyID, workSchedule, contactNumber, email , status) VALUES (?, ?, ?, ?, ?, 'Active')";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, doctor.getName());
            pstmt.setInt(2, doctor.getSpecialtyID());
            pstmt.setString(3, doctor.getWorkSchedule());
            pstmt.setString(4, doctor.getContactNumber());
            pstmt.setString(5, doctor.getEmail());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật bác sĩ
    public static boolean updateDoctor(Doctor doctor) {
        String query = "UPDATE doctor SET name = ?, specialtyID = ?, workSchedule = ?, contactNumber = ?, email = ? WHERE doctorID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, doctor.getName());
            pstmt.setInt(2, doctor.getSpecialtyID());
            pstmt.setString(3, doctor.getWorkSchedule());
            pstmt.setString(4, doctor.getContactNumber());
            pstmt.setString(5, doctor.getEmail());
            pstmt.setInt(6, doctor.getId());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa bác sĩ
    public static boolean deleteDoctor(int doctorId) {
        String query = "DELETE FROM doctor WHERE doctorID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, doctorId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}