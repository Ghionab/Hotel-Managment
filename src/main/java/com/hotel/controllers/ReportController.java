package com.hotel.controllers;

import com.hotel.model.Booking; // Import Booking model
// Import necessary models (e.g., a custom ReportRow class or use Booking)
// Import DAOs (BookingDAO, RoomDAO, CustomerDAO)

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReportController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<Booking> reportTableView; // Or use a custom ReportRow class
    @FXML private Label statusMessageLabel;

    // Define TableColumns based on ReportView.fxml fx:id attributes
    @FXML private TableColumn<Booking, Integer> colReportBookingId; // Adjust type if using custom class
    @FXML private TableColumn<Booking, Integer> colReportRoomNumber;
    @FXML private TableColumn<Booking, String> colReportCustomerName; // This will require a join or lookup
    @FXML private TableColumn<Booking, LocalDate> colReportCheckIn;
    @FXML private TableColumn<Booking, LocalDate> colReportCheckOut;

    // private BookingDAO bookingDAO;
    // private CustomerDAO customerDAO; // Needed to get customer names

    private ObservableList<Booking> reportData = FXCollections.observableArrayList(); // Or ObservableList<ReportRow>

    public void initialize() {
        // Instantiate necessary DAOs
        // bookingDAO = new BookingDAOImpl();
        // customerDAO = new CustomerDAOImpl();

        statusMessageLabel.setText("");

        // Setup TableView columns - Adjust PropertyValueFactory based on the class used (Booking or ReportRow)
        colReportBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colReportRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colReportCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colReportCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        // Special handling for Customer Name (if not directly in Booking model)
        // This might require a custom CellValueFactory or a dedicated ReportRow class
        // that includes the customer name obtained via a join.
        // Simple placeholder - requires implementation:
        // colReportCustomerName.setCellValueFactory(cellData -> {
        //     int customerId = cellData.getValue().getCustomerId();
        //     // TODO: Look up customer name using customerDAO (inefficient here, better in query)
        //     return new javafx.beans.property.SimpleStringProperty("Customer " + customerId);
        // });


        reportTableView.setItems(reportData);
        System.out.println("ReportController initialized.");
    }

    @FXML
    private void handleGenerateReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // Basic validation
        if (startDate == null || endDate == null) {
            showAlert(Alert.AlertType.WARNING, "Input Needed", "Please select both start and end dates.");
            return;
        }
        if (endDate.isBefore(startDate)) {
             showAlert(Alert.AlertType.WARNING, "Invalid Dates", "End date cannot be before start date.");
            return;
        }

        System.out.println("Generating report for dates: " + startDate + " to " + endDate);
        loadReportData(startDate, endDate);
    }

    private void loadReportData(LocalDate startDate, LocalDate endDate) {
        reportData.clear();
        statusMessageLabel.setText("");
        // TODO: Implement DAO logic to fetch report data (likely Bookings within date range)
        // You'll need a query that potentially joins Bookings, Customers, Rooms
         setStatusMessage("Placeholder: Load report data here (Bookings from " + startDate + " to " + endDate + ")", true);

        /*
        if (bookingDAO == null) { setStatusMessage("Error: Booking service not available.", false); return; }
        try {
            // Ideally, have a DAO method that fetches data specifically for the report, perhaps joining tables.
            // Example using existing DAO method (might need refinement):
            List<Booking> bookings = bookingDAO.findByDateRange(startDate, endDate);

            // If you need customer names or room types, you'd either:
            // 1. Modify the DAO query to return a custom object/map with joined data.
            // 2. Perform lookups here (less efficient for large datasets).

            reportData.setAll(bookings); // Use bookings directly if TableView is <Booking>

             if (reportData.isEmpty()) {
                 setStatusMessage("No bookings found for the selected date range.", true);
             } else {
                 setStatusMessage("Report generated successfully.", true);
             }

        } catch (SQLException e) {
            setStatusMessage("Database error generating report: " + e.getMessage(), false);
            e.printStackTrace();
        }
        */
    }


    private void setStatusMessage(String message, boolean success) {
         statusMessageLabel.setText(message);
         statusMessageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
     }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Optional: Define a simple inner class or separate class for report rows if needed
    /*
    public static class ReportRow {
        private final int bookingId;
        private final int roomNumber;
        private final String customerName;
        private final LocalDate checkInDate;
        private final LocalDate checkOutDate;
        // Add more fields as needed

        public ReportRow(int bookingId, int roomNumber, String customerName, LocalDate checkInDate, LocalDate checkOutDate) {
            this.bookingId = bookingId;
            this.roomNumber = roomNumber;
            this.customerName = customerName;
            this.checkInDate = checkInDate;
            this.checkOutDate = checkOutDate;
        }

        // Add Getters
        public int getBookingId() { return bookingId; }
        public int getRoomNumber() { return roomNumber; }
        public String getCustomerName() { return customerName; }
        public LocalDate getCheckInDate() { return checkInDate; }
        public LocalDate getCheckOutDate() { return checkOutDate; }
    }
    */
} 