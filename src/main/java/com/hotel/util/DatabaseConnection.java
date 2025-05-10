package com.hotel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // --- IMPORTANT: Replace with your actual database details! ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "qwert";
    // ---------------------------------------------------------------

    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    /**
     * Gets a connection to the database.
     * If a connection does not exist or is closed, it attempts to create a new one.
     *
     * @return A Connection object to the database.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the MySQL JDBC driver
                // Class.forName("com.mysql.cj.jdbc.Driver"); // Usually not needed with modern JDBC

                // Establish the connection
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connection successful!"); // For debugging
            } catch (SQLException e) {
                System.err.println("Database Connection Error: " + e.getMessage());
                // In a real app, provide better error handling or logging
                throw e; // Re-throw the exception so calling code knows about the failure
            }
        }
        return connection;
    }

    /**
     * Closes the database connection if it's open.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed."); // For debugging
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
} 