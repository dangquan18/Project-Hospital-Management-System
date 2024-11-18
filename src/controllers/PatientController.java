package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import model.Patient;

import java.io.IOException;
import java.util.Objects;

public class PatientController {
    @FXML
    private TableView<Patient> PatientTableView;
    @FXML
    private TableColumn<Patient, Integer> patients_col_patientID;
    @FXML
    private TableColumn<Patient, String> patients_col_name;
    @FXML
    private TableColumn<Patient, Integer> patients_col_age;
    @FXML
    private TableColumn<Patient, String> patients_col_contactNumber;
    @FXML
    private TableColumn<Patient, String> patients_col_email;
    @FXML
    private TableColumn<Patient, String> patients_col_action;
    @FXML
    private Button addPatientButton;

    private ObservableList<Patient> patientList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        addPatientButton.setOnAction(event -> loadPage("/views/Dashboard/AddPatientForm.fxml"));
        PatientTableView.setEditable(true);
        patients_col_patientID.setCellValueFactory(new PropertyValueFactory<>("id"));
        patients_col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        patients_col_age.setCellValueFactory(new PropertyValueFactory<>("age"));
        patients_col_contactNumber.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        patients_col_email.setCellValueFactory(new PropertyValueFactory<>("email"));

        setUpEditCommitHandlers(); // Enable editing for columns
        setUpActionColumn(); // Setup action column with delete button

        loadPatientData(); // Load initial patient data
    }

    private void loadPage(String fxmlFile) {
        try {
            Stage stage = (Stage) addPatientButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpActionColumn() {
        patients_col_action.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Patient, String> call(TableColumn<Patient, String> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Delete");

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            deleteButton.setOnAction(event -> {
                                Patient patient = getTableView().getItems().get(getIndex());
                                handleDeletePatient(patient);
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
        patients_col_name.setCellFactory(TextFieldTableCell.forTableColumn());
        patients_col_name.setOnEditCommit(event -> updatePatientField(event.getRowValue(), "name", event.getNewValue()));

        patients_col_age.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        patients_col_age.setOnEditCommit(event -> updatePatientField(event.getRowValue(), "age", String.valueOf(event.getNewValue())));

        patients_col_contactNumber.setCellFactory(TextFieldTableCell.forTableColumn());
        patients_col_contactNumber.setOnEditCommit(event -> updatePatientField(event.getRowValue(), "contactNumber", event.getNewValue()));

        patients_col_email.setCellFactory(TextFieldTableCell.forTableColumn());
        patients_col_email.setOnEditCommit(event -> updatePatientField(event.getRowValue(), "email", event.getNewValue()));
    }

    private void updatePatientField(Patient patient, String field, String newValue) {
        if (newValue != null && !newValue.trim().isEmpty()) {
            switch (field) {
                case "name": patient.setName(newValue); break;
                case "age": patient.setAge(Integer.parseInt(newValue)); break;

                case "contactNumber": patient.setContactNumber(newValue); break;
                case "email": patient.setEmail(newValue); break;
                default: throw new IllegalArgumentException("Unknown field: " + field);
            }

            if (Patient.updatePatient(patient)) {
                System.out.println("Patient updated successfully: " + patient);
                PatientTableView.refresh();
            } else {
                showErrorDialog("Update Failed", "Failed to update the patient information.");
            }
        } else {
            showErrorDialog("Invalid Input", "The value cannot be empty or whitespace.");
        }
    }

    private void loadPatientData() {
        patientList = Patient.getPatients();
        PatientTableView.setItems(patientList);
    }

    @FXML


    private void handleDeletePatient(Patient patient) {
        if (patient != null && Patient.deletePatient(patient.getId())) {
            loadPatientData();
        } else {
            showErrorDialog("Delete Failed", "Failed to delete patient.");
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void senseAddForm(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/Dashboard/AddPatientForm.fxml")));
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