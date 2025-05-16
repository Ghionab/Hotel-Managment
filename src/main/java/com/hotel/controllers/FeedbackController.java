package com.hotel.controllers;

import com.hotel.dao.CustomerDAO;
import com.hotel.dao.BookingDAO;
import com.hotel.dao.FeedbackDAO;
import com.hotel.dao.impl.CustomerDAOImpl;
import com.hotel.dao.impl.BookingDAOImpl;
import com.hotel.dao.impl.FeedbackDAOImpl;
import com.hotel.model.Customer;
import com.hotel.model.Booking;
import com.hotel.models.Feedback;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import java.util.stream.Collectors;
import java.util.List;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.sql.SQLException;

public class FeedbackController implements Initializable {
    
    @FXML private ComboBox<String> customerComboBox;
    @FXML private ComboBox<String> bookingComboBox;
    @FXML private ComboBox<Integer> ratingComboBox;
    @FXML private TextArea commentsArea;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, Timestamp> dateColumn;
    @FXML private TableColumn<Feedback, String> customerNameColumn;
    @FXML private TableColumn<Feedback, Integer> bookingIdColumn;
    @FXML private TableColumn<Feedback, Integer> ratingColumn;
    @FXML private TableColumn<Feedback, String> commentsColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<Integer> filterRatingComboBox;
    
    // ObservableList to hold the filtered feedback
    private ObservableList<Feedback> feedbackList = FXCollections.observableArrayList();

    private final FeedbackDAO feedbackDAO = new FeedbackDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final BookingDAO bookingDAO = new BookingDAOImpl();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupComboBoxes();
        loadFeedbackData();
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("feedbackDate"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));
        
        // Add custom cell factories for formatting if needed
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toLocalDateTime().toString());
                }
            }
        });
    }

    private void setupComboBoxes() {
        // Setup rating options (1-5)
        ObservableList<Integer> ratingOptions = FXCollections.observableArrayList(1, 2, 3, 4, 5);
        ratingComboBox.setItems(ratingOptions);
        filterRatingComboBox.setItems(ratingOptions);
        
        // Setup search listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterFeedback();
        });
        
        // Setup rating filter listener
        filterRatingComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterFeedback();
        });
        
        // Load customer data
        try {
            List<Customer> customers = customerDAO.findAll();
            ObservableList<String> customerOptions = FXCollections.observableArrayList();
            customerOptions.add("Select Customer");
            customers.forEach(customer -> 
                customerOptions.add(customer.getFirstName() + " " + customer.getLastName()));
            customerComboBox.setItems(customerOptions);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load customers: " + e.getMessage());
        }
        
        // Load booking data
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            ObservableList<String> bookingOptions = FXCollections.observableArrayList();
            bookingOptions.add("Select Booking");
            bookings.forEach(booking -> 
                bookingOptions.add("Booking ID: " + booking.getBookingId() + 
                    " (" + booking.getCustomerName() + ", Room: " + booking.getRoomNumber() + ")"));
            bookingComboBox.setItems(bookingOptions);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load bookings: " + e.getMessage());
        }
    }

    private void loadFeedbackData() {
        List<Feedback> feedbacks = feedbackDAO.getAllFeedback();
        feedbackList.setAll(feedbacks);
        feedbackTable.setItems(feedbackList);
    }
    
    private void filterFeedback() {
        String searchText = searchField.getText().toLowerCase();
        Integer selectedRating = filterRatingComboBox.getValue();
        
        List<Feedback> filteredList = feedbackDAO.getAllFeedback().stream()
            .filter(feedback -> {
                // Filter by search text (in customer name or comments)
                boolean matchesSearch = searchText.isEmpty() ||
                    (feedback.getCustomerName() != null && feedback.getCustomerName().toLowerCase().contains(searchText)) ||
                    (feedback.getComments() != null && feedback.getComments().toLowerCase().contains(searchText));
                
                // Filter by rating if selected
                boolean matchesRating = selectedRating == null || feedback.getRating() == selectedRating;
                
                return matchesSearch && matchesRating;
            })
            .collect(Collectors.toList());
            
        feedbackList.setAll(filteredList);
    }

    @FXML
    private void handleSubmitFeedback() {
        // Validate input
        if (customerComboBox.getValue() == null || ratingComboBox.getValue() == null) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        try {
            Feedback feedback = new Feedback();
            // Get customer ID from selected customer
            String selectedCustomer = customerComboBox.getValue();
            if (selectedCustomer != null && !selectedCustomer.equals("Select Customer")) {
                // Extract customer ID from the display string
                List<Customer> customers = customerDAO.findAll();
                Customer selectedCustomerObj = customers.stream()
                    .filter(c -> (c.getFirstName() + " " + c.getLastName()).equals(selectedCustomer))
                    .findFirst().orElse(null);
                if (selectedCustomerObj != null) {
                    feedback.setCustomerId(selectedCustomerObj.getCustomerId());
                }
            }
            
            // Get booking ID if a booking is selected
            if (bookingComboBox.getValue() != null && !bookingComboBox.getValue().equals("Select Booking")) {
                // Extract booking ID from the display string
                String bookingIdStr = bookingComboBox.getValue().split(" ")[2]; // Get "ID:123" part
                int bookingId = Integer.parseInt(bookingIdStr.split("ID:")[1]);
                feedback.setBookingId(bookingId);
            }
            feedback.setRating(ratingComboBox.getValue());
            feedback.setComments(commentsArea.getText());
            feedback.setFeedbackDate(Timestamp.valueOf(LocalDateTime.now()));

            feedbackDAO.addFeedback(feedback);
            
            // Clear form and refresh table
            clearForm();
            loadFeedbackData();
            
            showAlert("Success", "Feedback submitted successfully!");
        } catch (Exception e) {
            showAlert("Error", "Failed to submit feedback: " + e.getMessage());
        }
    }

    private void clearForm() {
        customerComboBox.setValue(null);
        bookingComboBox.setValue(null);
        ratingComboBox.setValue(null);
        commentsArea.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh() {
        loadFeedbackData();
    }

    @FXML
    private void handleClearFilters() {
        // Clear search field and rating filter
        searchField.clear();
        filterRatingComboBox.setValue(null);
        filterFeedback(); // Apply empty filters to show all data
    }
}
