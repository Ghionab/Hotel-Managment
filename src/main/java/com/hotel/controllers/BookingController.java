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

public class BookingController {

    @FXML private TextField bookingIdField;
    @FXML private ComboBox<Room> roomNumberComboBox;
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private DatePicker checkInDatePicker;
    @FXML private DatePicker checkOutDatePicker;
    @FXML private Label statusMessageLabel;
    @FXML private TableView<Booking> bookingsTableView;
    @FXML private TableColumn<Booking, Integer> colBookingId;
    @FXML private TableColumn<Booking, Integer> colRoomNumber;
    @FXML private TableColumn<Booking, Integer> colCustomerId;
    @FXML private TableColumn<Booking, LocalDate> colCheckIn;
    @FXML private TableColumn<Booking, LocalDate> colCheckOut;

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
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        bookingsTableView.setItems(bookingList);

        // Add listener to TableView selection
        bookingsTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> showBookingDetails(newSelection));

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
            if (roomDAO == null) {
                setStatusMessage("Error: Room service not available.", false);
                return;
            }
            List<Room> rooms = roomDAO.findAvailableRooms();
            availableRoomList.setAll(rooms);
        } catch (SQLException e) {
            setStatusMessage("Error loading rooms: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void loadCustomers() {
        customerList.clear();
        try {
            if (customerDAO == null) {
                setStatusMessage("Error: Customer service not available.", false);
                return;
            }
            List<Customer> customers = customerDAO.findAll();
            customerList.setAll(customers);
        } catch (SQLException e) {
            setStatusMessage("Error loading customers: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void showBookingDetails(Booking booking) {
        if (booking != null) {
            bookingIdField.setText(String.valueOf(booking.getBookingId()));
            checkInDatePicker.setValue(booking.getCheckInDate());
            checkOutDatePicker.setValue(booking.getCheckOutDate());

            // Select room in ComboBox (find matching room object)
            Room selectedRoom = availableRoomList.stream()
                                    .filter(r -> r.getRoomNumber() == booking.getRoomNumber())
                                    .findFirst().orElse(null);
            // If the booked room wasn't in the 'available' list initially, add it temporarily or handle differently
            if(selectedRoom == null) {
                // Fetch the specific room? Or add a placeholder? Handle this case.
                 System.out.println("Booked room "+ booking.getRoomNumber() +" not in available list. Add handling.");
            }
            roomNumberComboBox.setValue(selectedRoom);

            // Select customer in ComboBox
            Customer selectedCustomer = customerList.stream()
                                          .filter(c -> c.getCustomerId() == booking.getCustomerId())
                                          .findFirst().orElse(null);
            customerComboBox.setValue(selectedCustomer);

        } else {
            handleClearFields();
        }
    }

    @FXML
    private void handleAddBooking() {
        if (!validateInput()) return;

        Room selectedRoom = roomNumberComboBox.getValue();
        Customer selectedCustomer = customerComboBox.getValue();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        Booking newBooking = new Booking(0, selectedRoom.getRoomNumber(), selectedCustomer.getCustomerId(), checkIn, checkOut);
        
        if (bookingDAO == null) {
            setStatusMessage("Error: Booking service not available.", false);
            return;
        }
        
        try {
            // Check if room is available for the dates before adding
            List<Booking> existingBookings = bookingDAO.findByRoomNumber(selectedRoom.getRoomNumber());
            boolean hasConflict = existingBookings.stream().anyMatch(b -> 
                (checkIn.isBefore(b.getCheckOutDate()) || checkIn.isEqual(b.getCheckOutDate())) &&
                (checkOut.isAfter(b.getCheckInDate()) || checkOut.isEqual(b.getCheckInDate())));
            
            if (hasConflict) {
                setStatusMessage("Room is not available for selected dates.", false);
                return;
            }
            
            boolean success = bookingDAO.addBooking(newBooking);
            if (success) {
                // Update Room status to 'occupied'
                roomDAO.updateRoomStatus(selectedRoom.getRoomNumber(), "occupied");
                loadInitialData(); // Reload lists
                handleClearFields();
                setStatusMessage("Booking added successfully!", true);
            } else {
                setStatusMessage("Failed to add booking.", false);
            }
        } catch (SQLException e) {
            setStatusMessage("Database error adding booking: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateBooking() {
        Booking selectedBooking = bookingsTableView.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a booking to update.");
            return;
        }
        if (!validateInput()) return;

        Room selectedRoom = roomNumberComboBox.getValue();
        Customer selectedCustomer = customerComboBox.getValue();
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        Booking updatedBooking = new Booking(selectedBooking.getBookingId(), selectedRoom.getRoomNumber(), 
            selectedCustomer.getCustomerId(), checkIn, checkOut);

        if (bookingDAO == null) {
            setStatusMessage("Error: Booking service not available.", false);
            return;
        }
        
        try {
            // Check for conflicts only if room or dates changed
            if (selectedBooking.getRoomNumber() != selectedRoom.getRoomNumber() ||
                !selectedBooking.getCheckInDate().equals(checkIn) ||
                !selectedBooking.getCheckOutDate().equals(checkOut)) {
                
                List<Booking> existingBookings = bookingDAO.findByRoomNumber(selectedRoom.getRoomNumber());
                boolean hasConflict = existingBookings.stream()
                    .filter(b -> b.getBookingId() != selectedBooking.getBookingId())
                    .anyMatch(b -> 
                        (checkIn.isBefore(b.getCheckOutDate()) || checkIn.isEqual(b.getCheckOutDate())) &&
                        (checkOut.isAfter(b.getCheckInDate()) || checkOut.isEqual(b.getCheckInDate())));
                
                if (hasConflict) {
                    setStatusMessage("Room is not available for selected dates.", false);
                    return;
                }
            }
            
            boolean success = bookingDAO.updateBooking(updatedBooking);
            if (success) {
                // Update room statuses if room changed
                if (selectedBooking.getRoomNumber() != selectedRoom.getRoomNumber()) {
                    roomDAO.updateRoomStatus(selectedBooking.getRoomNumber(), "available");
                    roomDAO.updateRoomStatus(selectedRoom.getRoomNumber(), "occupied");
                }
                loadInitialData();
                setStatusMessage("Booking updated successfully!", true);
            } else {
                setStatusMessage("Failed to update booking.", false);
            }
        } catch (SQLException e) {
            setStatusMessage("Database error updating booking: " + e.getMessage(), false);
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
                if (bookingDAO == null) {
                    setStatusMessage("Error: Booking service not available.", false);
                    return;
                }
                
                try {
                    boolean success = bookingDAO.deleteBooking(selectedBooking.getBookingId());
                    if (success) {
                        // Update Room status to 'available' if no other active bookings exist
                        List<Booking> roomBookings = bookingDAO.findByRoomNumber(selectedBooking.getRoomNumber());
                        if (roomBookings.isEmpty()) {
                            roomDAO.updateRoomStatus(selectedBooking.getRoomNumber(), "available");
                        }
                        loadInitialData();
                        handleClearFields();
                        setStatusMessage("Booking deleted successfully!", true);
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --- Helper Classes for ComboBox Display ---
    private static class RoomStringConverter extends javafx.util.StringConverter<Room> {
        @Override
        public String toString(Room room) {
            return (room == null) ? null : "Room " + room.getRoomNumber() + " (" + room.getType() + ")";
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
} 