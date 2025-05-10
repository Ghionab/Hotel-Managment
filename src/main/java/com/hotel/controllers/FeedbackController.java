package com.hotel.controllers;

import com.hotel.dao.FeedbackDAO;
import com.hotel.dao.impl.FeedbackDAOImpl;
import com.hotel.models.Feedback;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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

    private final FeedbackDAO feedbackDAO = new FeedbackDAOImpl();

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
        ratingComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        
        // TODO: Load customer and booking data from their respective DAOs
        // This is a placeholder - implement actual data loading
        customerComboBox.setItems(FXCollections.observableArrayList("Select Customer"));
        bookingComboBox.setItems(FXCollections.observableArrayList("Select Booking"));
    }

    private void loadFeedbackData() {
        feedbackTable.setItems(FXCollections.observableArrayList(feedbackDAO.getAllFeedback()));
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
            // TODO: Get actual customer ID from selection
            feedback.setCustomerId(1); // Placeholder
            // TODO: Get actual booking ID from selection if selected
            if (bookingComboBox.getValue() != null && !bookingComboBox.getValue().equals("Select Booking")) {
                feedback.setBookingId(1); // Placeholder
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
        loadFeedbackData(); // Reload all data
    }
}
