package model;
import java.sql.Timestamp;

public class DoctorHistory {
    private int historyId;
    private String doctorName;
    private String specialty;
    private String patientName;
    private Timestamp scheduledTime;  // Sử dụng Timestamp để lưu trữ thời gian

    public DoctorHistory(int historyId, String doctorName, String specialty, String patientName, Timestamp scheduledTime) {
        this.historyId = historyId;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.patientName = patientName;
        this.scheduledTime = scheduledTime;
    }

    // Getter và Setter
    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Timestamp getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Timestamp scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
