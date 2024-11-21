package model;

import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecord {

    private int recordID;        // Mã hồ sơ bệnh án
    private int appointmentID;    // Mã lịch khám
    private String diagnosis;    // Chẩn đoán
    private String treatment;    // Phương pháp điều trị
    private String result;       // Kết quả điều trị
    private String patientName;  // Tên bệnh nhân
    private String doctorName;   // Tên bác sĩ
    private Timestamp scheduledTime; // Thời gian lịch khám

    // Constructor
    public MedicalRecord(int recordID, int appointmentID, String patientName, String doctorName,
                         Timestamp scheduledTime, String diagnosis,  String treatment, String result) {
        this.recordID = recordID;
        this.appointmentID = appointmentID;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.result = result;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.scheduledTime = scheduledTime;
    }

    // Getter and Setter methods
    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Timestamp getScheduledTime() {
        return scheduledTime;
    }

        public void setScheduledTime(Timestamp scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    // Phương thức lấy danh sách hồ sơ bệnh án từ cơ sở dữ liệu


    // Phương thức xóa hồ sơ bệnh án từ cơ sở dữ liệu
    public static boolean deleteMedicalRecord(int recordID) {
        String query = "DELETE FROM medicalrecord WHERE recordID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, recordID);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Phương thức thêm hồ sơ bệnh án mới vào cơ sở dữ liệu
    public boolean addMedicalRecord() {
        String query = "INSERT INTO medicalrecord (appointmentID, diagnosis, treatment, result) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, this.appointmentID);
            pstmt.setString(2, this.diagnosis);
            pstmt.setString(3, this.treatment);
            pstmt.setString(4, this.result);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
