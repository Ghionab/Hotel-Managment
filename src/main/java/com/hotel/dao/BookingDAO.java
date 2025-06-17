package com.hotel.dao;

import com.hotel.model.Booking;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingDAO {
    Optional<Booking> findById(int bookingId) throws SQLException;
    List<Booking> findAll() throws SQLException;
    boolean addBooking(Booking booking) throws SQLException;
    boolean updateBooking(Booking booking) throws SQLException;
    boolean deleteBooking(int bookingId) throws SQLException;
    List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException; // For reports
    List<Booking> findByCustomerId(int customerId) throws SQLException;
    List<Booking> findByRoomNumber(int roomNumber) throws SQLException;
    // Dashboard methods
    
    int getCheckedInGuestsCount() throws SQLException;
    int getExpectedCheckInsToday() throws SQLException;
    int getExpectedCheckOutsToday() throws SQLException;
    int getNewBookingsToday() throws SQLException;
    double getRevenueToday() throws SQLException;
} 
