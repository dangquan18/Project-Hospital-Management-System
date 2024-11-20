package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class Patient {

    private int id;
    private String name;
    private int age;
    private String contactNumber;
    private String email;

    // Constructor
    public Patient(int id, String name, int age, String contactNumber, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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
        String url = "jdbc:mysql://localhost:3306/hospital_management"; // Thay "your_database_name" với tên cơ sở dữ liệu của bạn
        String user = "root"; // Tên người dùng của cơ sở dữ liệu (thường là "root")
        String password = ""; // Mật khẩu của người dùng
        return DriverManager.getConnection(url, user, password);
    }

    // Lấy danh sách bệnh nhân
    public static ObservableList<Patient> getPatients() {
        ObservableList<Patient> patientList = FXCollections.observableArrayList();
        String query = "SELECT * FROM patient"; // Thay đổi bảng và cột cho phù hợp

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                patientList.add(new Patient(
                        rs.getInt("patientID"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("contactNumber"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patientList;
    }

    // Thêm bệnh nhân
    public static boolean addPatient(Patient patient) {
        String query = "INSERT INTO patient (name, age, contactNumber, email) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getContactNumber());
            pstmt.setString(4, patient.getEmail());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật thông tin bệnh nhân
    public static boolean updatePatient(Patient patient) {
        String query = "UPDATE patient SET name = ?, age = ?, contactNumber = ?, email = ? WHERE patientID = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getContactNumber());
            pstmt.setString(4, patient.getEmail());
            pstmt.setInt(5, patient.getId());
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa bệnh nhân
    public static boolean deletePatient(int patientId) {
        String query = "DELETE FROM patient WHERE patientID = ?";
        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, patientId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
