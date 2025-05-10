package com.hotel.dao.impl;

import com.hotel.dao.BookingDAO;
import com.hotel.model.Booking;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingDAOImpl implements BookingDAO {

    @Override
    public Optional<Booking> findById(int bookingId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setRoomNumber(rs.getInt("room_id"));
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                return Optional.of(booking);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Booking> findAll() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY check_in_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setRoomNumber(rs.getInt("room_id"));
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                bookings.add(booking);
            }
        }
        return bookings;
    }

    @Override
    public boolean addBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (room_id, customer_id, check_in_date, check_out_date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, booking.getRoomNumber());
            stmt.setInt(2, booking.getCustomerId());
            stmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
            stmt.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    booking.setBookingId(generatedKeys.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean updateBooking(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET room_id = ?, customer_id = ?, check_in_date = ?, check_out_date = ? WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, booking.getRoomNumber());
            stmt.setInt(2, booking.getCustomerId());
            stmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
            stmt.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            stmt.setInt(5, booking.getBookingId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteBooking(int bookingId) throws SQLException {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE check_in_date >= ? AND check_out_date <= ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setRoomNumber(rs.getInt("room_id"));
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                bookings.add(booking);
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> findByCustomerId(int customerId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE customer_id = ? ORDER BY check_in_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setRoomNumber(rs.getInt("room_id"));
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                bookings.add(booking);
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> findByRoomNumber(int roomNumber) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE room_id = ? ORDER BY check_in_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setRoomNumber(rs.getInt("room_id"));
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                bookings.add(booking);
            }
        }
        return bookings;
    }

    @Override
    public int getCheckedInGuestsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE booking_status = 'Checked-in'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public int getExpectedCheckInsToday() throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE check_in_date = CURRENT_DATE AND booking_status = 'Confirmed'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public int getExpectedCheckOutsToday() throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE check_out_date = CURRENT_DATE AND booking_status = 'Checked-in'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public int getNewBookingsToday() throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE DATE(created_at) = CURRENT_DATE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public double getRevenueToday() throws SQLException {
        // Assuming there's a payment or invoice table linked to bookings
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM payments WHERE DATE(payment_date) = CURRENT_DATE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}