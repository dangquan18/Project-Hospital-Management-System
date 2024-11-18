package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class Doctor {

    private int id;
    private String name;
    private String specialty;
    private String workSchedule;
    private String contactNumber;
    private String email;

    // Constructor
    public Doctor(int id, String name, String specialty, String workSchedule, String contactNumber, String email) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.workSchedule = workSchedule;
        this.contactNumber = contactNumber;
        this.email = email;
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
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
    private static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospitalmanagement"; // Thay "your_database_name" với tên cơ sở dữ liệu của bạn
        String user = "root"; // Tên người dùng của cơ sở dữ liệu (thường là "root")
        String password = ""; // Mật khẩu của người dùng
        return DriverManager.getConnection(url, user, password);
    }

    // Lấy danh sách bác sĩ
    public static ObservableList<Doctor> getDoctors() {
        ObservableList<Doctor> doctorList = FXCollections.observableArrayList();
        String query = "SELECT * FROM doctor"; // Thay đổi bảng và cột cho phù hợp

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                doctorList.add(new Doctor(
                        rs.getInt("doctorID"),
                        rs.getString("name"),
                        rs.getString("specialty"),
                        rs.getString("workSchedule"),
                        rs.getString("contactNumber"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctorList;
    }

    // Thêm bác sĩ
    public static boolean addDoctor(Doctor doctor) {
        String query = "INSERT INTO doctor (name, specialty, workSchedule, contactNumber, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getSpecialty());
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
        String query = "UPDATE doctor SET name = ?, specialty = ?, workSchedule = ?, contactNumber = ?, email = ? WHERE doctorID = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getSpecialty());
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
        try (Connection connection = getConnection();
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
