package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import utils.DatabaseConnection;

public class Appointment {

    private int appointmentID;
    private String patientName;
    private String symptoms;
    private String specialtyName;
    private String doctorName;
    private Timestamp scheduledTime;
    private  String status;

    // Constructor
    public Appointment(int appointmentID, String patientName, String symptoms, String specialtyName, String doctorName, Timestamp scheduledTime , String status) {
        this.appointmentID = appointmentID;
        this.patientName = patientName;
        this.symptoms = symptoms;
        this.specialtyName = specialtyName;
        this.doctorName = doctorName;
        this.scheduledTime = scheduledTime;
        this.status = status;
    }

    // Getters
    public int getAppointmentID() {
        return appointmentID;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getSpecialtyName() {
        return specialtyName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public Timestamp getScheduledTime() {
        return scheduledTime;
    }

    // Setters
    public void setScheduledTime(Timestamp scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    // Xóa cuộc hẹn
    public static boolean deleteAppointment(int appointmentID) {
        String query = "DELETE FROM appointment WHERE appointmentID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, appointmentID);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}