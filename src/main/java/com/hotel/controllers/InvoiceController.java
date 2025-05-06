package com.hotel.controllers;

import com.hotel.model.Invoice;
import com.hotel.dao.impl.InvoiceDAOImpl;

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

public class InvoiceController implements Initializable {
    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, Integer> colInvoiceId;
    @FXML private TableColumn<Invoice, Integer> colBookingId;
    @FXML private TableColumn<Invoice, Integer> colCustomerId;
    @FXML private TableColumn<Invoice, LocalDate> colIssueDate;
    @FXML private TableColumn<Invoice, LocalDate> colDueDate;
    @FXML private TableColumn<Invoice, Double> colTotalAmount;
    @FXML private TableColumn<Invoice, Double> colPaidAmount;
    @FXML private TableColumn<Invoice, Double> colBalanceDue;
    @FXML private TableColumn<Invoice, String> colStatus;

    @FXML private Label invoiceIdLabel;
    @FXML private TextField bookingIdField;
    @FXML private TextField customerIdField;
    @FXML private DatePicker issueDatePicker;
    @FXML private DatePicker dueDatePicker;
    @FXML private TextField totalAmountField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField paymentAmountField;
    @FXML private Label messageLabel;

    private InvoiceDAOImpl invoiceDAO;
    private ObservableList<Invoice> invoiceList;
    private final ObservableList<String> statuses = FXCollections.observableArrayList(
        "Pending", "Partially Paid", "Paid", "Overdue", "Cancelled"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        invoiceDAO = new InvoiceDAOImpl();

        // Initialize status combo box
        statusComboBox.setItems(statuses);

        // Initialize table columns
        colInvoiceId.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colPaidAmount.setCellValueFactory(new PropertyValueFactory<>("paidAmount"));
        colBalanceDue.setCellValueFactory(new PropertyValueFactory<>("balanceDue"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("invoiceStatus"));

        // Format currency columns
        colTotalAmount.setCellFactory(column -> new TableCell<Invoice, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });
        colPaidAmount.setCellFactory(colTotalAmount.getCellFactory());
        colBalanceDue.setCellFactory(colTotalAmount.getCellFactory());

        // Add selection listener
        invoiceTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showInvoiceDetails(newValue));

        // Load initial data
        loadInvoiceData();
    }

    @FXML
    private void handleCreateInvoice() {
        if (!validateInputFields()) {
            return;
        }

        try {
            Invoice invoice = new Invoice();
            populateInvoiceFromFields(invoice);

            if (invoiceDAO.addInvoice(invoice)) {
                loadInvoiceData();
                clearFields();
                showSuccess("Invoice created successfully");
            } else {
                showError("Failed to create invoice");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for amounts");
        }
    }

    @FXML
    private void handleUpdateInvoice() {
        Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
        if (selectedInvoice == null) {
            showError("Please select an invoice to update");
            return;
        }

        if (!validateInputFields()) {
            return;
        }

        try {
            populateInvoiceFromFields(selectedInvoice);

            if (invoiceDAO.updateInvoice(selectedInvoice)) {
                loadInvoiceData();
                clearFields();
                showSuccess("Invoice updated successfully");
            } else {
                showError("Failed to update invoice");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for amounts");
        }
    }

    @FXML
    private void handleDeleteInvoice() {
        Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
        if (selectedInvoice == null) {
            showError("Please select an invoice to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Invoice");
        alert.setHeaderText("Delete Invoice #" + selectedInvoice.getInvoiceId());
        alert.setContentText("Are you sure you want to delete this invoice?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                if (invoiceDAO.deleteInvoice(selectedInvoice.getInvoiceId())) {
                    loadInvoiceData();
                    clearFields();
                    showSuccess("Invoice deleted successfully");
                } else {
                    showError("Failed to delete invoice");
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRecordPayment() {
        Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
        if (selectedInvoice == null) {
            showError("Please select an invoice to record payment");
            return;
        }

        try {
            double paymentAmount = Double.parseDouble(paymentAmountField.getText().trim());
            if (paymentAmount <= 0) {
                showError("Payment amount must be greater than zero");
                return;
            }

            if (paymentAmount > selectedInvoice.getBalanceDue()) {
                showError("Payment amount cannot exceed balance due");
                return;
            }

            if (invoiceDAO.recordPayment(selectedInvoice.getInvoiceId(), paymentAmount)) {
                loadInvoiceData();
                paymentAmountField.clear();
                showSuccess("Payment recorded successfully");
            } else {
                showError("Failed to record payment");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Please enter a valid payment amount");
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
    }

    private void loadInvoiceData() {
        try {
            invoiceList = FXCollections.observableArrayList(invoiceDAO.findAll());
            invoiceTable.setItems(invoiceList);
        } catch (SQLException e) {
            showError("Error loading invoice data: " + e.getMessage());
        }
    }

    private void showInvoiceDetails(Invoice invoice) {
        if (invoice != null) {
            invoiceIdLabel.setText(String.valueOf(invoice.getInvoiceId()));
            bookingIdField.setText(String.valueOf(invoice.getBookingId()));
            customerIdField.setText(String.valueOf(invoice.getCustomerId()));
            issueDatePicker.setValue(invoice.getIssueDate());
            dueDatePicker.setValue(invoice.getDueDate());
            totalAmountField.setText(String.format("%.2f", invoice.getTotalAmount()));
            statusComboBox.setValue(invoice.getInvoiceStatus());
        } else {
            clearFields();
        }
    }

    private boolean validateInputFields() {
        if (bookingIdField.getText().trim().isEmpty() ||
            customerIdField.getText().trim().isEmpty() ||
            totalAmountField.getText().trim().isEmpty() ||
            issueDatePicker.getValue() == null ||
            statusComboBox.getValue() == null) {
            showError("Required fields: Booking ID, Customer ID, Total Amount, Issue Date, and Status");
            return false;
        }
        return true;
    }

    private void populateInvoiceFromFields(Invoice invoice) {
        invoice.setBookingId(Integer.parseInt(bookingIdField.getText().trim()));
        invoice.setCustomerId(Integer.parseInt(customerIdField.getText().trim()));
        invoice.setIssueDate(issueDatePicker.getValue());
        invoice.setDueDate(dueDatePicker.getValue());
        invoice.setTotalAmount(Double.parseDouble(totalAmountField.getText().trim()));
        invoice.setInvoiceStatus(statusComboBox.getValue());
    }

    private void clearFields() {
        invoiceIdLabel.setText("");
        bookingIdField.clear();
        customerIdField.clear();
        issueDatePicker.setValue(null);
        dueDatePicker.setValue(null);
        totalAmountField.clear();
        statusComboBox.setValue(null);
        paymentAmountField.clear();
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
}
