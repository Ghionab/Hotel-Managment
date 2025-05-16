package com.hotel.controllers;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.model.Customer;
import com.hotel.dao.BookingDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.dao.CustomerDAO;
import com.hotel.dao.impl.BookingDAOImpl;
import com.hotel.dao.impl.RoomDAOImpl;
import com.hotel.dao.impl.CustomerDAOImpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BookingController {

    @FXML private TextField bookingIdField;
    @FXML private ComboBox<Room> roomNumberComboBox;
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private DatePicker checkInDatePicker;
    @FXML private DatePicker checkOutDatePicker;
    @FXML private Spinner<Integer> numberOfAdultsSpinner;
    @FXML private Spinner<Integer> numberOfKidsSpinner;
    @FXML private Label statusMessageLabel;
    @FXML private TableView<Booking> bookingsTableView;
    @FXML private TableColumn<Booking, Integer> colBookingId;
    @FXML private TableColumn<Booking, Integer> colRoomId;
    @FXML private TableColumn<Booking, Integer> colCustomerId;
    @FXML private TableColumn<Booking, LocalDate> colCheckIn;
    @FXML private TableColumn<Booking, LocalDate> colCheckOut;
    @FXML private TextField searchField;
    @FXML private DatePicker filterStartDate;
    @FXML private DatePicker filterEndDate;

    private BookingDAO bookingDAO;
    private RoomDAO roomDAO;
    private CustomerDAO customerDAO;

    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();
    private ObservableList<Room> availableRoomList = FXCollections.observableArrayList();
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    public void initialize() {
        // Instantiate DAOs
        bookingDAO = new BookingDAOImpl();
        roomDAO = new RoomDAOImpl();
        customerDAO = new CustomerDAOImpl();
        // roomDAO = new RoomDAOImpl();
        // customerDAO = new CustomerDAOImpl();

        statusMessageLabel.setText("");

        // Setup TableView columns
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        bookingsTableView.setItems(bookingList);

        // Add listener to TableView selection
        bookingsTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> showBookingDetails(newSelection));

        // Add filter listeners
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterStartDate.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        filterEndDate.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        // Add listeners to date pickers to refresh available rooms
        checkInDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> loadAvailableRooms());
        checkOutDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> loadAvailableRooms());

        // Setup ComboBoxes
        roomNumberComboBox.setItems(availableRoomList);
        customerComboBox.setItems(customerList);
        // Set converters to display Room/Customer info correctly in ComboBox
        roomNumberComboBox.setConverter(new RoomStringConverter());
        customerComboBox.setConverter(new CustomerStringConverter());

        // Load initial data
        loadInitialData();
        System.out.println("BookingController initialized.");
    }

    private void loadInitialData() {
        loadBookings();
        loadAvailableRooms();
        loadCustomers();
    }

    private void loadBookings() {
        bookingList.clear();
        statusMessageLabel.setText("");
        try {
            if (bookingDAO == null) {
                setStatusMessage("Error: Booking service not available.", false);
                return;
            }
            List<Booking> bookings = bookingDAO.findAll();
            bookingList.setAll(bookings);
        } catch (SQLException e) {
            setStatusMessage("Error loading bookings: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void loadAvailableRooms() {
        availableRoomList.clear();
        try {
            if (bookingDAO == null) {
                setStatusMessage("Error: Booking service not available.", false);
                return;
            }
            LocalDate checkIn = checkInDatePicker.getValue();
            LocalDate checkOut = checkOutDatePicker.getValue();
            
            // If dates aren't selected, use current date and next day
            if (checkIn == null) checkIn = LocalDate.now();
            if (checkOut == null) checkOut = checkIn.plusDays(1);
            
            List<Room> rooms = bookingDAO.getAllAvailableRooms(checkIn, checkOut);
            availableRoomList.addAll(rooms);
            if (rooms.isEmpty()) {
                setStatusMessage("No available rooms found for selected dates.", false);
            }
        } catch (SQLException e) {
            setStatusMessage("Error loading available rooms: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void loadCustomers() {
        customerList.clear();
        try {
            if (bookingDAO == null) {
                setStatusMessage("Error: Booking service not available.", false);
                return;
            }
            List<Customer> customers = bookingDAO.getAllCustomers();
            customerList.addAll(customers);
            if (customers.isEmpty()) {
                setStatusMessage("No customers found.", false);
            }
        } catch (SQLException e) {
            setStatusMessage("Error loading customers: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void showBookingDetails(Booking booking) {
        if (booking != null) {
            bookingIdField.setText(String.valueOf(booking.getBookingId()));
            try {
                // Find and select the room
                List<Booking> roomBookings = bookingDAO.findByRoomId(booking.getRoomId());
                if (!roomBookings.isEmpty()) {
                    Room room = roomDAO.findById(booking.getRoomId()).orElse(null);
                    if (room != null) {
                        roomNumberComboBox.setValue(room);
                    }
                }
                
                // Find and select the customer
                List<Booking> customerBookings = bookingDAO.findByCustomerId(booking.getCustomerId());
                if (!customerBookings.isEmpty()) {
                    Customer customer = customerDAO.findById(booking.getCustomerId()).orElse(null);
                    if (customer != null) {
                        customerComboBox.setValue(customer);
                    }
                }
                
                checkInDatePicker.setValue(booking.getCheckInDate());
                checkOutDatePicker.setValue(booking.getCheckOutDate());
                numberOfAdultsSpinner.getValueFactory().setValue(booking.getNumberOfAdults());
                numberOfKidsSpinner.getValueFactory().setValue(booking.getNumberOfKids());
            } catch (SQLException e) {
                setStatusMessage("Error loading booking details: " + e.getMessage(), false);
            }
        } else {
            handleClearFields();
        }
    }

    @FXML
    private void handleAddBooking() {
        if (!validateInput()) {
            return;
        }

        try {
            Booking newBooking = new Booking();
            newBooking.setRoomId(roomNumberComboBox.getValue().getRoomId());
            newBooking.setCustomerId(customerComboBox.getValue().getCustomerId());
            newBooking.setCheckInDate(checkInDatePicker.getValue());
            newBooking.setCheckOutDate(checkOutDatePicker.getValue());
            newBooking.setBookingStatus("Confirmed");
            newBooking.setNumberOfAdults(numberOfAdultsSpinner.getValue());
            newBooking.setNumberOfKids(numberOfKidsSpinner.getValue());

            if (bookingDAO.addBooking(newBooking)) {
                setStatusMessage("Booking added successfully.", true);
                loadBookings();
                handleClearFields();
            } else {
                setStatusMessage("Failed to add booking.", false);
            }
        } catch (SQLException e) {
            setStatusMessage("Error adding booking: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateBooking() {
        if (!validateInput()) {
            return;
        }

        String bookingIdText = bookingIdField.getText();
        if (bookingIdText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a booking to update.");
            return;
        }

        try {
            int bookingId = Integer.parseInt(bookingIdText);
            Optional<Booking> existingBooking = bookingDAO.findById(bookingId);

            if (existingBooking.isPresent()) {
                // Check if the room is available for the new dates
                List<Booking> roomBookings = bookingDAO.findByRoomId(roomNumberComboBox.getValue().getRoomId());
                boolean isRoomAvailable = true;

                for (Booking booking : roomBookings) {
                    if (booking.getBookingId() != bookingId) { // Skip current booking
                        LocalDate newCheckIn = checkInDatePicker.getValue();
                        LocalDate newCheckOut = checkOutDatePicker.getValue();
                        LocalDate existingCheckIn = booking.getCheckInDate();
                        LocalDate existingCheckOut = booking.getCheckOutDate();

                        if ((newCheckIn.isBefore(existingCheckOut) || newCheckIn.isEqual(existingCheckOut)) &&
                            (newCheckOut.isAfter(existingCheckIn) || newCheckOut.isEqual(existingCheckIn))) {
                            isRoomAvailable = false;
                            break;
                        }
                    }
                }

                if (!isRoomAvailable) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Room is not available for the selected dates.");
                    return;
                }

                Booking updatedBooking = existingBooking.get();
                updatedBooking.setRoomId(roomNumberComboBox.getValue().getRoomId());
                updatedBooking.setCustomerId(customerComboBox.getValue().getCustomerId());
                updatedBooking.setCheckInDate(checkInDatePicker.getValue());
                updatedBooking.setCheckOutDate(checkOutDatePicker.getValue());

                if (bookingDAO.updateBooking(updatedBooking)) {
                    setStatusMessage("Booking updated successfully.", true);
                    loadBookings();
                    handleClearFields();
                } else {
                    setStatusMessage("Failed to update booking.", false);
                }
            } else {
                setStatusMessage("Booking not found.", false);
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid booking ID format.");
        } catch (SQLException e) {
            setStatusMessage("Error updating booking: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteBooking() {
        Booking selectedBooking = bookingsTableView.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking to delete.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Booking");
        confirmDialog.setContentText("Are you sure you want to delete this booking?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (bookingDAO.deleteBooking(selectedBooking.getBookingId())) {
                        setStatusMessage("Booking deleted successfully.", true);
                        loadBookings();
                        handleClearFields();
                    } else {
                        setStatusMessage("Failed to delete booking.", false);
                    }
                } catch (SQLException e) {
                    setStatusMessage("Database error deleting booking: " + e.getMessage(), false);
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void handleClearFields() {
        bookingsTableView.getSelectionModel().clearSelection();
        bookingIdField.clear();
        roomNumberComboBox.getSelectionModel().clearSelection();
        customerComboBox.getSelectionModel().clearSelection();
        checkInDatePicker.setValue(null);
        checkOutDatePicker.setValue(null);
        numberOfAdultsSpinner.getValueFactory().setValue(1);
        numberOfKidsSpinner.getValueFactory().setValue(0);
        statusMessageLabel.setText("");
    }

    private boolean validateInput() {
        String errorMessage = "";
        if (roomNumberComboBox.getValue() == null) {
            errorMessage += "Room must be selected.\n";
        }
        if (customerComboBox.getValue() == null) {
            errorMessage += "Customer must be selected.\n";
        }
        if (checkInDatePicker.getValue() == null) {
            errorMessage += "Check-in date is required.\n";
        }
        if (checkOutDatePicker.getValue() == null) {
            errorMessage += "Check-out date is required.\n";
        }
        if (checkInDatePicker.getValue() != null && checkOutDatePicker.getValue() != null) {
            if (checkOutDatePicker.getValue().isBefore(checkInDatePicker.getValue()) || checkOutDatePicker.getValue().isEqual(checkInDatePicker.getValue())) {
                errorMessage += "Check-out date must be after check-in date.\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", errorMessage);
            return false;
        }
    }

    private void setStatusMessage(String message, boolean success) {
         statusMessageLabel.setText(message);
         statusMessageLabel.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
     }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- Helper Classes for ComboBox Display ---
    private static class RoomStringConverter extends javafx.util.StringConverter<Room> {
        @Override
        public String toString(Room room) {
            return (room == null) ? null : "Room " + room.getRoomNumber() + " (" + room.getType() + ", " + room.getDescription() + ")";
        }

        @Override
        public Room fromString(String string) {
            // Not needed if ComboBox is not editable
            return null;
        }
    }

    private static class CustomerStringConverter extends javafx.util.StringConverter<Customer> {
        @Override
        public String toString(Customer customer) {
            return (customer == null) ? null : customer.getFullName() + " (ID: " + customer.getCustomerId() + ")";
        }

        @Override
        public Customer fromString(String string) {
             // Not needed if ComboBox is not editable
            return null;
        }
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        filterStartDate.setValue(null);
        filterEndDate.setValue(null);
        loadBookings();
    }

    @FXML
    private void handleRefresh() {
        loadInitialData();
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        LocalDate startDate = filterStartDate.getValue();
        LocalDate endDate = filterEndDate.getValue();

        ObservableList<Booking> filteredList = bookingList.filtered(booking -> {
            boolean matchesSearch = searchText.isEmpty() ||
                String.valueOf(booking.getBookingId()).contains(searchText) ||
                String.valueOf(booking.getRoomId()).contains(searchText) ||
                String.valueOf(booking.getCustomerId()).contains(searchText);

            boolean matchesDateRange = true;
            if (startDate != null && endDate != null) {
                matchesDateRange = !booking.getCheckOutDate().isBefore(startDate) &&
                    !booking.getCheckInDate().isAfter(endDate);
            } else if (startDate != null) {
                matchesDateRange = !booking.getCheckOutDate().isBefore(startDate);
            } else if (endDate != null) {
                matchesDateRange = !booking.getCheckInDate().isAfter(endDate);
            }

            return matchesSearch && matchesDateRange;
        });

        bookingsTableView.setItems(filteredList);
    }
} 