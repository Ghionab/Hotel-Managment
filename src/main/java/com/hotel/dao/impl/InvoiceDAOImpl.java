package com.hotel.dao.impl;

import com.hotel.dao.InvoiceDAO;
import com.hotel.model.Invoice;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceDAOImpl implements InvoiceDAO {

    @Override
    public List<Invoice> findAll() throws SQLException {
        String sql = "SELECT * FROM invoices ORDER BY issue_date DESC";
        List<Invoice> invoices = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                invoices.add(mapRowToInvoice(rs));
            }
        }
        return invoices;
    }

    @Override
    public Optional<Invoice> findById(int invoiceId) throws SQLException {
        String sql = "SELECT * FROM invoices WHERE invoice_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, invoiceId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToInvoice(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Invoice> findByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT * FROM invoices WHERE customer_id = ? ORDER BY issue_date DESC";
        List<Invoice> invoices = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                invoices.add(mapRowToInvoice(rs));
            }
        }
        return invoices;
    }

    @Override
    public List<Invoice> findByBookingId(int bookingId) throws SQLException {
        String sql = "SELECT * FROM invoices WHERE booking_id = ? ORDER BY issue_date DESC";
        List<Invoice> invoices = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                invoices.add(mapRowToInvoice(rs));
            }
        }
        return invoices;
    }

    @Override
    public boolean addInvoice(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoices (booking_id, customer_id, issue_date, due_date, " +
                    "total_amount, paid_amount, balance_due, invoice_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setInvoiceParameters(pstmt, invoice);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        invoice.setInvoiceId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean updateInvoice(Invoice invoice) throws SQLException {
        String sql = "UPDATE invoices SET booking_id = ?, customer_id = ?, issue_date = ?, " +
                    "due_date = ?, total_amount = ?, paid_amount = ?, balance_due = ?, " +
                    "invoice_status = ? WHERE invoice_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setInvoiceParameters(pstmt, invoice);
            pstmt.setInt(9, invoice.getInvoiceId());
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteInvoice(int invoiceId) throws SQLException {
        String sql = "DELETE FROM invoices WHERE invoice_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, invoiceId);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateInvoiceStatus(int invoiceId, String status) throws SQLException {
        String sql = "UPDATE invoices SET invoice_status = ? WHERE invoice_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, invoiceId);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean recordPayment(int invoiceId, double amount) throws SQLException {
        String sql = "UPDATE invoices SET paid_amount = paid_amount + ?, " +
                    "balance_due = total_amount - (paid_amount + ?), " +
                    "invoice_status = CASE " +
                    "    WHEN (paid_amount + ?) >= total_amount THEN 'Paid' " +
                    "    ELSE 'Partially Paid' " +
                    "END " +
                    "WHERE invoice_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setDouble(2, amount);
            pstmt.setDouble(3, amount);
            pstmt.setInt(4, invoiceId);
            
            // Also insert into payments table
            if (pstmt.executeUpdate() > 0) {
                return insertPayment(conn, invoiceId, amount);
            }
        }
        return false;
    }

    private boolean insertPayment(Connection conn, int invoiceId, double amount) throws SQLException {
        String sql = "INSERT INTO payments (invoice_id, amount, payment_method) VALUES (?, ?, 'Cash')";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            pstmt.setDouble(2, amount);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Invoice mapRowToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(rs.getInt("invoice_id"));
        invoice.setBookingId(rs.getInt("booking_id"));
        invoice.setCustomerId(rs.getInt("customer_id"));
        invoice.setIssueDate(rs.getDate("issue_date").toLocalDate());
        if (rs.getDate("due_date") != null) {
            invoice.setDueDate(rs.getDate("due_date").toLocalDate());
        }
        invoice.setTotalAmount(rs.getDouble("total_amount"));
        invoice.setPaidAmount(rs.getDouble("paid_amount"));
        invoice.setBalanceDue(rs.getDouble("balance_due"));
        invoice.setInvoiceStatus(rs.getString("invoice_status"));
        invoice.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        invoice.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return invoice;
    }

    private void setInvoiceParameters(PreparedStatement pstmt, Invoice invoice) throws SQLException {
        pstmt.setInt(1, invoice.getBookingId());
        pstmt.setInt(2, invoice.getCustomerId());
        pstmt.setDate(3, Date.valueOf(invoice.getIssueDate()));
        pstmt.setDate(4, invoice.getDueDate() != null ? Date.valueOf(invoice.getDueDate()) : null);
        pstmt.setDouble(5, invoice.getTotalAmount());
        pstmt.setDouble(6, invoice.getPaidAmount());
        pstmt.setDouble(7, invoice.getBalanceDue());
        pstmt.setString(8, invoice.getInvoiceStatus());
    }
}
