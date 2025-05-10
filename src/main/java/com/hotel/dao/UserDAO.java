package com.hotel.dao;

import com.hotel.model.User;
import java.sql.SQLException;
import java.util.Optional;

public interface UserDAO {
    /**
     * Finds a user by username.
     * Used for login verification.
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found, otherwise empty.
     * @throws SQLException if a database access error occurs.
     */
    Optional<User> findByUsername(String username) throws SQLException;

    // Add other methods if needed, e.g., for user management (add, update, delete)
    boolean addUser(User user) throws SQLException;
    boolean updateUser(User user) throws SQLException;
    boolean deleteUser(int userId) throws SQLException;
} 