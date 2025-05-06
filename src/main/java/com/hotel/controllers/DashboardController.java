package com.hotel.controllers;

import com.hotel.dao.RoomDAO;
import com.hotel.dao.BookingDAO;
import com.hotel.dao.impl.RoomDAOImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML private Label totalRoomsLabel;
    @FXML private Label availableRoomsLabel;
    @FXML private Label occupiedRoomsLabel;
    @FXML private Label checkedInGuestsLabel;
    @FXML private Label expectedCheckInsLabel;
    @FXML private Label expectedCheckOutsLabel;
    @FXML private Label newBookingsTodayLabel;
    @FXML private Label revenueTodayLabel;
    @FXML private Label lastUpdatedLabel;

    private RoomDAO roomDAO;
    private BookingDAO bookingDAO;
    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize DAOs
        roomDAO = new RoomDAOImpl();
        // bookingDAO = new BookingDAOImpl(); // Uncomment when BookingDAO is implemented

        // Load initial dashboard data
        refreshDashboard();
    }

    @FXML
    private void handleRefreshDashboard() {
        refreshDashboard();
    }

    private void refreshDashboard() {
        try {
            // Update room statistics
            updateRoomStatistics();
            
            // Update guest statistics
            updateGuestStatistics();
            
            // Update today's statistics
            updateTodayStatistics();
            
            // Update last refreshed time
            lastUpdatedLabel.setText(LocalDateTime.now().format(TIME_FORMATTER));
            
        } catch (SQLException e) {
            showError("Database Error", "Failed to refresh dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateRoomStatistics() throws SQLException {
        try {
            int totalRooms = roomDAO.getTotalRoomCount();
            int availableRooms = roomDAO.getAvailableRoomCount();
            int occupiedRooms = roomDAO.getOccupiedRoomCount();

            totalRoomsLabel.setText(String.valueOf(totalRooms));
            availableRoomsLabel.setText(String.valueOf(availableRooms));
            occupiedRoomsLabel.setText(String.valueOf(occupiedRooms));
        } catch (SQLException e) {
            throw new SQLException("Error updating room statistics: " + e.getMessage(), e);
        }
    }

    private void updateGuestStatistics() throws SQLException {
        // These methods need to be added to BookingDAO
        // Uncomment when BookingDAO is implemented
        /*
        int checkedInGuests = bookingDAO.getCheckedInGuestsCount();
        int expectedCheckIns = bookingDAO.getExpectedCheckInsToday();
        int expectedCheckOuts = bookingDAO.getExpectedCheckOutsToday();

        checkedInGuestsLabel.setText(String.valueOf(checkedInGuests));
        expectedCheckInsLabel.setText(String.valueOf(expectedCheckIns));
        expectedCheckOutsLabel.setText(String.valueOf(expectedCheckOuts));
        */

        // Temporary placeholder values
        checkedInGuestsLabel.setText("0");
        expectedCheckInsLabel.setText("0");
        expectedCheckOutsLabel.setText("0");
    }

    private void updateTodayStatistics() throws SQLException {
        // These methods need to be added to BookingDAO
        // Uncomment when BookingDAO is implemented
        /*
        int newBookings = bookingDAO.getNewBookingsToday();
        double revenueToday = bookingDAO.getRevenueToday();

        newBookingsTodayLabel.setText(String.valueOf(newBookings));
        revenueTodayLabel.setText(String.format("$%.2f", revenueToday));
        */

        // Temporary placeholder values
        newBookingsTodayLabel.setText("0");
        revenueTodayLabel.setText("$0.00");
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 