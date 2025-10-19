package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a borrowing record for a book checkout.
 */
public class BorrowingRecord {
    private final String recordId;
    private final String patronId;
    private final String isbn;
    private final LocalDate checkoutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String branchId;
    
    public BorrowingRecord(String recordId, String patronId, String isbn, 
                          LocalDate checkoutDate, LocalDate dueDate, String branchId) {
        this.recordId = recordId;
        this.patronId = patronId;
        this.isbn = isbn;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.branchId = branchId;
    }
    
    public String getRecordId() {
        return recordId;
    }
    
    public String getPatronId() {
        return patronId;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public String getBranchId() {
        return branchId;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }
    
    public boolean isReturned() {
        return returnDate != null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowingRecord that = (BorrowingRecord) o;
        return Objects.equals(recordId, that.recordId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(recordId);
    }
    
    @Override
    public String toString() {
        return "BorrowingRecord{" +
                "recordId='" + recordId + '\'' +
                ", patronId='" + patronId + '\'' +
                ", isbn='" + isbn + '\'' +
                ", checkoutDate=" + checkoutDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", branchId='" + branchId + '\'' +
                '}';
    }
}
