package com.hotel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Invoice {
    private int invoiceId;
    private int bookingId;
    private int customerId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private double totalAmount;
    private double paidAmount;
    private double balanceDue;
    private String invoiceStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Invoice() {}

    // Constructor with required fields
    public Invoice(int bookingId, int customerId, LocalDate issueDate, double totalAmount) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.issueDate = issueDate;
        this.totalAmount = totalAmount;
        this.paidAmount = 0.0;
        this.balanceDue = totalAmount;
        this.invoiceStatus = "Pending";
    }

    // Full constructor
    public Invoice(int invoiceId, int bookingId, int customerId, LocalDate issueDate, 
                  LocalDate dueDate, double totalAmount, double paidAmount, 
                  double balanceDue, String invoiceStatus, LocalDateTime createdAt, 
                  LocalDateTime updatedAt) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.balanceDue = balanceDue;
        this.invoiceStatus = invoiceStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { 
        this.totalAmount = totalAmount;
        updateBalanceDue();
    }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { 
        this.paidAmount = paidAmount;
        updateBalanceDue();
    }

    public double getBalanceDue() { return balanceDue; }
    public void setBalanceDue(double balanceDue) { this.balanceDue = balanceDue; }

    public String getInvoiceStatus() { return invoiceStatus; }
    public void setInvoiceStatus(String invoiceStatus) { this.invoiceStatus = invoiceStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper method to update balance due
    private void updateBalanceDue() {
        this.balanceDue = this.totalAmount - this.paidAmount;
        // Update status if fully paid
        if (this.balanceDue <= 0) {
            this.invoiceStatus = "Paid";
        }
    }

    @Override
    public String toString() {
        return String.format("Invoice #%d - Total: $%.2f, Status: %s", 
                           invoiceId, totalAmount, invoiceStatus);
    }
}
