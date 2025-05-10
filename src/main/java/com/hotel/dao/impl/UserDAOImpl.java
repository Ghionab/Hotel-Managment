package com.hotel.dao.impl;

import com.hotel.dao.UserDAO;
import com.hotel.model.User;
import com.hotel.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        // TODO: Implement actual database query logic
        System.out.println("UserDAOImpl: findByUsername called (placeholder)");
        String sql = "SELECT user_id, username, password, role FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password")); // Remember: Hash in real app
                user.setRole(rs.getString("role"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
             System.err.println("SQL Error in findByUsername: " + e.getMessage());
             throw e; // Re-throw exception after logging
        }
        return Optional.empty(); // Return empty if not found or error occurred before return
    }

    // TODO: Implement other UserDAO methods if added to the interface (addUser, updateUser, deleteUser)
    @Override
    public boolean addUser(User user) throws SQLException {
        // Ensure password hashing is implemented in a real application before storing
        String sql = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword()); // Store hashed password in production!
            pstmt.setString(3, user.getRole());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("SQL Error in addUser: " + e.getMessage());
            // Consider more specific error handling (e.g., duplicate username)
            throw e;
        }
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
         // Ensure password hashing is implemented in a real application
        String sql = "UPDATE Users SET username = ?, password = ?, role = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword()); // Store hashed password in production!
            pstmt.setString(3, user.getRole());
            pstmt.setInt(4, user.getUserId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("SQL Error in updateUser: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("SQL Error in deleteUser: " + e.getMessage());
            // Handle potential foreign key constraints if users are linked elsewhere
            throw e;
        }
    }
} 