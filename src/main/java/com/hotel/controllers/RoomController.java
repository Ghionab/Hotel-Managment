package com.hotel.controllers;

import com.hotel.model.Room;
import com.hotel.dao.impl.RoomDAOImpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class RoomController implements Initializable {
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, Integer> roomNumberColumn;
    @FXML private TableColumn<Room, String> typeColumn;
    @FXML private TableColumn<Room, String> statusColumn;
    @FXML private TableColumn<Room, Integer> floorColumn;
    @FXML private TableColumn<Room, Double> priceColumn;

    // Detail view controls
    @FXML private Label roomNumberLabel;
    @FXML private Label typeLabel;
    @FXML private Label floorLabel;
    @FXML private Label priceLabel;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button updateStatusButton;
    @FXML private Label messageLabel;

    private RoomDAOImpl roomDAO;
    private ObservableList<Room> roomList;
    private final ObservableList<String> statusOptions = FXCollections.observableArrayList(
        "Available", "Booked", "Cleaning", "Out of Service"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the DAO
        roomDAO = new RoomDAOImpl();

        // Initialize the status options
        statusComboBox.setItems(statusOptions);

        // Set up table columns
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        floorColumn.setCellValueFactory(new PropertyValueFactory<>("floor"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Add selection listener to table
        roomTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showRoomDetails(newValue));

        // Load initial data
        loadRoomData();
    }

    private void loadRoomData() {
        try {
            List<Room> rooms = roomDAO.findAll();
            roomList = FXCollections.observableArrayList(rooms);
            roomTable.setItems(roomList);
            clearMessage();
        } catch (SQLException e) {
            showError("Error loading rooms: " + e.getMessage());
        }
    }

    private void showRoomDetails(Room room) {
        if (room != null) {
            roomNumberLabel.setText(String.valueOf(room.getRoomNumber()));
            typeLabel.setText(room.getType());
            floorLabel.setText(String.valueOf(room.getFloor()));
            priceLabel.setText(String.format("$%.2f", room.getPrice()));
            statusComboBox.setValue(room.getStatus());
            updateStatusButton.setDisable(false);
        } else {
            roomNumberLabel.setText("");
            typeLabel.setText("");
            floorLabel.setText("");
            priceLabel.setText("");
            statusComboBox.setValue(null);
            updateStatusButton.setDisable(true);
        }
    }

    @FXML
    private void handleUpdateStatusButton() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showError("Please select a room first");
            return;
        }

        String newStatus = statusComboBox.getValue();
        if (newStatus == null || newStatus.trim().isEmpty()) {
            showError("Please select a status");
            return;
        }

        try {
            if (roomDAO.updateRoomStatus(selectedRoom.getRoomNumber(), newStatus)) {
                loadRoomData(); // Refresh the table
                showSuccess("Room status updated successfully");
            } else {
                showError("Failed to update room status");
            }
        } catch (SQLException e) {
            showError("Error updating room status: " + e.getMessage());
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    private void clearMessage() {
        messageLabel.setText("");
    }
} 