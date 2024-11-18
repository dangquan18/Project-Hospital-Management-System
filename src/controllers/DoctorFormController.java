package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Doctor;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
public class DoctorFormController {
    @FXML
    private TableView<Doctor> doctorsTableView;
    @FXML
    private TableColumn<Doctor, Integer> doctors_col_id;
    @FXML
    private TableColumn<Doctor, String> doctors_col_name;
    @FXML
    private TableColumn<Doctor, String> doctors_col_specialty;
    @FXML
    private TableColumn<Doctor, String> doctors_col_workSchedule;
    @FXML
    private TableColumn<Doctor, String> doctors_col_contactNumber;
    @FXML
    private TableColumn<Doctor, String> doctors_col_email;
    @FXML
    private TableColumn<Doctor, String> doctors_col_action;
    @FXML


    private ObservableList<Doctor> doctorList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        doctorsTableView.setEditable(true);
        doctors_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        doctors_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        doctors_col_specialty.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        doctors_col_workSchedule.setCellValueFactory(new PropertyValueFactory<>("workSchedule"));
        doctors_col_contactNumber.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        doctors_col_email.setCellValueFactory(new PropertyValueFactory<>("email"));

        setUpEditCommitHandlers(); // Enable editing for columns
        setUpActionColumn(); // Setup action column with delete button

        loadDoctorData(); // Load initial doctor data
    }



    private void setUpActionColumn() {
        doctors_col_action.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Doctor, String> call(TableColumn<Doctor, String> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            deleteButton.setOnAction(event -> {
                                Doctor doctor = getTableView().getItems().get(getIndex());
                                handleDeleteDoctor(doctor);
                            });

                            HBox hbox = new HBox(10, deleteButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }

    private void setUpEditCommitHandlers() {
        doctors_col_name.setCellFactory(TextFieldTableCell.forTableColumn());
        doctors_col_name.setOnEditCommit(event -> updateDoctorField(event.getRowValue(), "name", event.getNewValue()));

        doctors_col_specialty.setCellFactory(TextFieldTableCell.forTableColumn());
        doctors_col_specialty.setOnEditCommit(event -> updateDoctorField(event.getRowValue(), "specialty", event.getNewValue()));

        doctors_col_workSchedule.setCellFactory(TextFieldTableCell.forTableColumn());
        doctors_col_workSchedule.setOnEditCommit(event -> updateDoctorField(event.getRowValue(), "workSchedule", event.getNewValue()));

        doctors_col_contactNumber.setCellFactory(TextFieldTableCell.forTableColumn());
        doctors_col_contactNumber.setOnEditCommit(event -> updateDoctorField(event.getRowValue(), "contactNumber", event.getNewValue()));

        doctors_col_email.setCellFactory(TextFieldTableCell.forTableColumn());
        doctors_col_email.setOnEditCommit(event -> updateDoctorField(event.getRowValue(), "email", event.getNewValue()));
    }

    private void updateDoctorField(Doctor doctor, String field, String newValue) {
        if (newValue != null && !newValue.trim().isEmpty()) {
            switch (field) {
                case "name": doctor.setName(newValue); break;
                case "specialty": doctor.setSpecialty(newValue); break;
                case "workSchedule": doctor.setWorkSchedule(newValue); break;
                case "contactNumber": doctor.setContactNumber(newValue); break;
                case "email": doctor.setEmail(newValue); break;
                default: throw new IllegalArgumentException("Unknown field: " + field);
            }

            if (Doctor.updateDoctor(doctor)) {
                System.out.println("Doctor updated successfully: " + doctor);
                doctorsTableView.refresh();
            } else {
                showErrorDialog("Update Failed", "Failed to update the doctor information.");
            }
        } else {
            showErrorDialog("Invalid Input", "The value cannot be empty or whitespace.");
        }
    }

    private void loadDoctorData() {
        doctorList = Doctor.getDoctors();
        doctorsTableView.setItems(doctorList);
    }

    @FXML
    private void handleAddDoctor() {
        Doctor newDoctor = new Doctor(0, "New Doctor", "Specialty", "Work Schedule", "Contact", "email@example.com");
        if (Doctor.addDoctor(newDoctor)) {
            loadDoctorData();
        } else {
            showErrorDialog("Add Failed", "Failed to add new doctor.");
        }
    }

    private void handleDeleteDoctor(Doctor doctor) {
        if (doctor != null && Doctor.deleteDoctor(doctor.getId())) {
            loadDoctorData();
        } else {
            showErrorDialog("Delete Failed", "Failed to delete doctor.");
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void senceAddForm(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/Dashboard/AddDoctorForm.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading FXML file.");
        }
    }
}