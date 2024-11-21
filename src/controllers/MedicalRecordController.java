package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.MedicalRecord;
import utils.DatabaseConnection;
import javafx.scene.control.cell.TextFieldTableCell;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class MedicalRecordController {

    @FXML
    private TableView<MedicalRecord> RecordTableView;
    @FXML
    private TableColumn<MedicalRecord, Integer> Record_col_id;
    @FXML
    private TableColumn<MedicalRecord, String> Record_col_patientName;
    @FXML
    private TableColumn<MedicalRecord, String> Record_col_medicalDoctor;
    @FXML
    private TableColumn<MedicalRecord, Timestamp> Record_col_scheduledTime;
    @FXML
    private TableColumn<MedicalRecord, String> Record_col_diagnosis;
    @FXML
    private TableColumn<MedicalRecord, String> Record_col_treatment;
    @FXML
    private TableColumn<MedicalRecord, String> Record_col_result;
    @FXML
    private TableColumn<MedicalRecord, String> Record_col_action;

    private ObservableList<MedicalRecord> medicalRecords = FXCollections.observableArrayList();

    public void initialize() {
        RecordTableView.setEditable(true);
        // Gắn dữ liệu cột từ đối tượng MedicalRecord
        Record_col_id.setCellValueFactory(new PropertyValueFactory<>("recordID"));
        Record_col_patientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        Record_col_medicalDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        Record_col_scheduledTime.setCellValueFactory(new PropertyValueFactory<>("scheduledTime"));
        Record_col_diagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        Record_col_treatment.setCellValueFactory(new PropertyValueFactory<>("treatment"));
        Record_col_result.setCellValueFactory(new PropertyValueFactory<>("result"));

        // Thiết lập tính năng chỉnh sửa các cột
        setUpEditCommitHandlers();

        loadMedicalRecords(); // Tải dữ liệu từ cơ sở dữ liệu
        RecordTableView.setItems(medicalRecords); // Đặt dữ liệu vào TableView

        setUpDeleteButton(); // Thêm nút xóa vào cột Action
    }

    private void setUpDeleteButton() {

        Record_col_action.setCellFactory(new Callback<TableColumn<MedicalRecord, String>, TableCell<MedicalRecord, String>>() {
            @Override
            public TableCell<MedicalRecord, String> call(TableColumn<MedicalRecord, String> param) {
                return new TableCell<MedicalRecord, String>() {
                    final Button deleteButton = new Button("Delete");

                    {
                        deleteButton.setStyle("-fx-background-color: #ff4f4f; -fx-text-fill: white;");
                        deleteButton.setOnAction(event -> {
                            MedicalRecord record = getTableView().getItems().get(getIndex());
                            deleteMedicalRecord(record);
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        });
    }


    private void loadMedicalRecords() {
        String query = """
        SELECT m.recordID, a.appointmentID, p.name AS patientName, d.name AS doctorName, a.scheduledTime,
               m.diagnosis, m.treatment, m.result
        FROM medicalrecord m
        JOIN appointment a ON m.appointmentID = a.appointmentID
        JOIN patient p ON a.patientID = p.patientID
        JOIN doctor d ON a.doctorID = d.doctorID
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int medicalRecordID = rs.getInt("recordID");
                int appointmentID = rs.getInt("appointmentID");
                String patientName = rs.getString("patientName");
                String doctorName = rs.getString("doctorName");
                Timestamp scheduledTime = rs.getTimestamp("scheduledTime");
                String diagnosis = rs.getString("diagnosis");
                String treatment = rs.getString("treatment");
                String result = rs.getString("result");

                // Tạo đối tượng MedicalRecord và thêm vào danh sách
                MedicalRecord record = new MedicalRecord(medicalRecordID, appointmentID, patientName, doctorName, scheduledTime, diagnosis, treatment, result);
                medicalRecords.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setUpEditCommitHandlers() {
        Record_col_diagnosis.setCellFactory(TextFieldTableCell.forTableColumn());
        Record_col_diagnosis.setOnEditCommit(event -> updateMedicalRecordField(event.getRowValue(), "diagnosis", event.getNewValue()));

        Record_col_treatment.setCellFactory(TextFieldTableCell.forTableColumn());
        Record_col_treatment.setOnEditCommit(event -> updateMedicalRecordField(event.getRowValue(), "treatment", event.getNewValue()));

        Record_col_result.setCellFactory(TextFieldTableCell.forTableColumn());
        Record_col_result.setOnEditCommit(event -> updateMedicalRecordField(event.getRowValue(), "result", event.getNewValue()));
    }

    private void updateMedicalRecordField(MedicalRecord record, String field, String newValue) {
        if (newValue != null && !newValue.trim().isEmpty()) {
            switch (field) {
                case "diagnosis":
                    record.setDiagnosis(newValue); break;
                case "treatment":
                    record.setTreatment(newValue); break;
                case "result":
                    record.setResult(newValue); break;
                default: throw new IllegalArgumentException("Unknown field: " + field);
            }

            if (updateMedicalRecordInDatabase(record)) {
                System.out.println("Medical record updated successfully: " + record);
                RecordTableView.refresh();
            }
        } else {
            showErrorDialog("Invalid Input", "The value cannot be empty or whitespace.");
        }
    }

    private boolean updateMedicalRecordInDatabase(MedicalRecord record) {
        String query = "UPDATE medicalrecord SET diagnosis = ?, treatment = ?, result = ? WHERE recordID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, record.getDiagnosis());
            pstmt.setString(2, record.getTreatment());
            pstmt.setString(3, record.getResult());
            pstmt.setInt(4, record.getRecordID());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showErrorDialog(String title, String message) {
        // Hiển thị hộp thoại lỗi nếu giá trị nhập vào không hợp lệ
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void senseAddForm(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/Dashboard/AddMedicalRecordForm.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML file.");
        }
    }

    private void deleteMedicalRecord(MedicalRecord record) {
        String query = "DELETE FROM medicalrecord WHERE recordID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, record.getRecordID());
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                medicalRecords.remove(record); // Xóa bản ghi khỏi danh sách
                System.out.println("Record deleted successfully.");
            } else {
                showErrorDialog("Deletion Failed", "Unable to delete the record.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "There was an error deleting the record.");
        }
    }
}
