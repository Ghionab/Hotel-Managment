package com.hotel.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.application.Platform;

public class MainController {

    @FXML private TabPane mainTabPane;

    private String currentUserRole;

    public void initialize() {
        System.out.println("MainController initialized.");
    }

    public void setUserRole(String role) {
        this.currentUserRole = role;
        System.out.println("User role set to: " + role);
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
}