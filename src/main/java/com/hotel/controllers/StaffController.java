package com.hotel.controllers;

import com.hotel.model.Staff;
import com.hotel.dao.impl.StaffDAOImpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StaffController implements Initializable {
    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, Integer> colUserId;
    @FXML private TableColumn<Staff, String> colFirstName;
    @FXML private TableColumn<Staff, String> colLastName;
    @FXML private TableColumn<Staff, String> colPosition;
    @FXML private TableColumn<Staff, String> colPhone;
    @FXML private TableColumn<Staff, String> colEmail;
    @FXML private TableColumn<Staff, LocalDate> colHireDate;
    @FXML private TableColumn<Staff, Double> colSalary;

    @FXML private TextField userIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> positionComboBox;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private DatePicker hireDatePicker;
    @FXML private TextField salaryField;
    @FXML private TextArea addressField;
    @FXML private Label messageLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterPositionComboBox;

    private StaffDAOImpl staffDAO;
    private ObservableList<Staff> staffList;
    private final ObservableList<String> positions = FXCollections.observableArrayList(
        "Admin", "Manager", "Receptionist", "Housekeeper", "Maintenance", "Chef"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        staffDAO = new StaffDAOImpl();

        // Initialize position combo boxes
        positionComboBox.setItems(positions);
        filterPositionComboBox.setItems(positions);

        // Add filter listeners
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterPositionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        // Initialize table columns
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colHireDate.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        // Add selection listener
        staffTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showStaffDetails(newValue));

        // Load initial data
        loadStaffData();
    }

    @FXML
    private void handleAddStaff() {
        if (!validateInputFields()) {
            return;
        }

        try {
            Staff staff = new Staff();
            populateStaffFromFields(staff);

            if (staffDAO.addStaff(staff)) {
                loadStaffData();
                clearFields();
                showSuccess("Staff member added successfully");
            } else {
                showError("Failed to add staff member");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for User ID and Salary");
        }
    }

    @FXML
    private void handleUpdateStaff() {
        Staff selectedStaff = staffTable.getSelectionModel().getSelectedItem();
        if (selectedStaff == null) {
            showError("Please select a staff member to update");
            return;
        }

        if (!validateInputFields()) {
            return;
        }

        try {
            populateStaffFromFields(selectedStaff);

            if (staffDAO.updateStaff(selectedStaff)) {
                loadStaffData();
                clearFields();
                showSuccess("Staff member updated successfully");
            } else {
                showError("Failed to update staff member");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for User ID and Salary");
        }
    }

    @FXML
    private void handleDeleteStaff() {
        Staff selectedStaff = staffTable.getSelectionModel().getSelectedItem();
        if (selectedStaff == null) {
            showError("Please select a staff member to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Staff");
        alert.setHeaderText("Delete Staff Member");
        alert.setContentText("Are you sure you want to delete this staff member?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                if (staffDAO.deleteStaff(selectedStaff.getUserId())) {
                    loadStaffData();
                    clearFields();
                    showSuccess("Staff member deleted successfully");
                } else {
                    showError("Failed to delete staff member");
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
    }

    private void loadStaffData() {
        try {
            staffList = FXCollections.observableArrayList(staffDAO.findAll());
            staffTable.setItems(staffList);
        } catch (SQLException e) {
            showError("Error loading staff data: " + e.getMessage());
        }
    }

    private void showStaffDetails(Staff staff) {
        if (staff != null) {
            userIdField.setText(String.valueOf(staff.getUserId()));
            firstNameField.setText(staff.getFirstName());
            lastNameField.setText(staff.getLastName());
            positionComboBox.setValue(staff.getPosition());
            phoneField.setText(staff.getPhoneNumber());
            emailField.setText(staff.getEmail());
            hireDatePicker.setValue(staff.getHireDate());
            salaryField.setText(String.format("%.2f", staff.getSalary()));
            addressField.setText(staff.getAddress());
        } else {
            clearFields();
        }
    }

    private boolean validateInputFields() {
        if (userIdField.getText().trim().isEmpty() ||
            firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            positionComboBox.getValue() == null) {
            showError("Required fields: User ID, First Name, Last Name, and Position");
            return false;
        }
        return true;
    }

    private void populateStaffFromFields(Staff staff) {
        staff.setUserId(Integer.parseInt(userIdField.getText().trim()));
        staff.setFirstName(firstNameField.getText().trim());
        staff.setLastName(lastNameField.getText().trim());
        staff.setPosition(positionComboBox.getValue());
        staff.setPhoneNumber(phoneField.getText().trim());
        staff.setEmail(emailField.getText().trim());
        staff.setHireDate(hireDatePicker.getValue());
        staff.setSalary(Double.parseDouble(salaryField.getText().trim()));
        staff.setAddress(addressField.getText().trim());
    }

    private void clearFields() {
        userIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        positionComboBox.setValue(null);
        phoneField.clear();
        emailField.clear();
        hireDatePicker.setValue(null);
        salaryField.clear();
        addressField.clear();
        messageLabel.setText("");
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        filterPositionComboBox.setValue(null);
        loadStaffData();
    }

    @FXML
    private void handleRefresh() {
        loadStaffData();
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String selectedPosition = filterPositionComboBox.getValue();

        ObservableList<Staff> filteredList = staffList.filtered(staff -> {
            boolean matchesSearch = searchText.isEmpty() ||
                String.valueOf(staff.getUserId()).contains(searchText) ||
                staff.getFirstName().toLowerCase().contains(searchText) ||
                staff.getLastName().toLowerCase().contains(searchText) ||
                staff.getEmail().toLowerCase().contains(searchText) ||
                staff.getPhoneNumber().toLowerCase().contains(searchText);

            boolean matchesPosition = selectedPosition == null || staff.getPosition().equals(selectedPosition);

            return matchesSearch && matchesPosition;
        });

        staffTable.setItems(filteredList);
    }
}
