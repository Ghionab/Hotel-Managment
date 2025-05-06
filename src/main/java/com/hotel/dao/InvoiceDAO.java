package com.hotel.dao;

import com.hotel.model.Invoice;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface InvoiceDAO {
    List<Invoice> findAll() throws SQLException;
    Optional<Invoice> findById(int invoiceId) throws SQLException;
    List<Invoice> findByCustomerId(int customerId) throws SQLException;
    List<Invoice> findByBookingId(int bookingId) throws SQLException;
    boolean addInvoice(Invoice invoice) throws SQLException;
    boolean updateInvoice(Invoice invoice) throws SQLException;
    boolean deleteInvoice(int invoiceId) throws SQLException;
    boolean updateInvoiceStatus(int invoiceId, String status) throws SQLException;
    boolean recordPayment(int invoiceId, double amount) throws SQLException;
}
