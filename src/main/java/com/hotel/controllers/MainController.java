package com.hotel.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;


public class MainController {

    @FXML private StackPane contentArea;
    @FXML private Label userRoleLabel;
    
    // Navigation buttons
    @FXML private Button roomsBtn;
    @FXML private Button bookingsBtn;
    @FXML private Button customersBtn;
    @FXML private Button staffBtn;
    @FXML private Button availableServicesBtn;
    @FXML private Button bookingServicesBtn;
    @FXML private Button invoicesBtn;
    @FXML private Button paymentsBtn;

    @FXML private Button dashboardBtn;
    @FXML private Button feedbackBtn;
    
    private Button currentActiveButton;


    public void initialize() {
        System.out.println("MainController initialized.");
        
        // Set Dashboard as default active view
        setActiveButton(dashboardBtn);
        loadView("DashboardTab.fxml");
    }

    public void setUserRole(String role) {
        if (userRoleLabel != null) {
            userRoleLabel.setText("[" + role + "]");
        }
        System.out.println("User role set to: " + role);
    }
    
    /**
     * Sets the active navigation button style
     * 
     * @param button The button to set as active
     */
    private void setActiveButton(Button button) {
        // Remove active style from previous button
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("active");
        }
        
        // Add active style to current button
        button.getStyleClass().add("active");
        currentActiveButton = button;
    }
    
    /**
     * Loads a view into the content area
     * 
     * @param fxmlFile The FXML file to load
     */
    private void loadView(String fxmlFile) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/fxml/" + fxmlFile));
            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found: " + fxmlFile);
            }
            
            Node view = loader.load();
            
            // Clear content area and add the new view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            // Controller can be accessed here if needed
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error loading view: " + fxmlFile + "\nError: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Unexpected error loading view: " + fxmlFile + "\nError: " + e.getMessage());
        }
    }
    
    /**
     * Shows the Rooms view
     */
    @FXML
    public void showRooms(ActionEvent event) {
        setActiveButton(roomsBtn);
        loadView("RoomTab.fxml");
    }
    
    /**
     * Shows the Bookings view
     */
    @FXML
    public void showBookings(ActionEvent event) {
        setActiveButton(bookingsBtn);
        loadView("BookingTab.fxml");
    }
    
    /**
     * Shows the Customers view
     */
    @FXML
    public void showCustomers(ActionEvent event) {
        setActiveButton(customersBtn);
        loadView("CustomerTab.fxml");
    }
    /**
     * Shows the Staff view
     */
    @FXML
    public void showStaff(ActionEvent event) {
        setActiveButton(staffBtn);
        loadView("StaffTab.fxml");
    }
    

    /**
     * Shows the Dashboard view
     */
    @FXML
    public void showDashboard(ActionEvent event) {
        setActiveButton(dashboardBtn);
        loadView("DashboardTab.fxml");
    }
    
    /**
     * Shows the Available Services view
     */
    @FXML
    public void showAvailableServices(ActionEvent event) {
        setActiveButton(availableServicesBtn);
        loadView("AvailableServicesTab.fxml");
    }
    
    /**
     * Shows the Booking Services view
     */
    @FXML
    public void showBookingServices(ActionEvent event) {
        setActiveButton(bookingServicesBtn);
        loadView("BookingServicesTab.fxml");
    }

    /**
     * Shows the Feedback view
     */
    @FXML
    public void showFeedback(ActionEvent event) {
        setActiveButton(feedbackBtn);
        loadView("FeedbackTab.fxml");
    }
    
    /**
     * Shows the Invoices view
     */
    @FXML
    public void showInvoices(ActionEvent event) {
        setActiveButton(invoicesBtn);
        loadView("InvoiceTab.fxml");
    }
    
    /**
     * Shows the Payments view
     */
    @FXML
    public void showPayments(ActionEvent event) {
        setActiveButton(paymentsBtn);
        loadView("PaymentTab.fxml");
    }



    /**
     * Handles the About action, displaying information about the application.
     *
     * @param event The ActionEvent triggered by the user action.
     */
    @FXML
    public void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Hotel Management System");
        alert.setContentText("Version 1.0\nDeveloped by [Your Name]\n 2025");
        alert.showAndWait();
    }

    /**
     * Handles the Exit action, closing the application.
     *
     * @param event The ActionEvent triggered by the user action.
     */
    @FXML
    public void handleExit(ActionEvent event) {
        Platform.exit();
    }
    
    /**
     * Shows an error alert with the given message
     * 
     * @param message The error message to display
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles the Logout action, returning to the login screen.
     *
     * @param event The ActionEvent triggered by the user action.
     */
    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/fxml/LoginView.fxml"));
            Parent loginView = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) contentArea.getScene().getWindow();
            
            // Set the login scene
            Scene scene = new Scene(loginView);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error returning to login screen");
        }
    }
}