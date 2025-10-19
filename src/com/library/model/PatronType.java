package com.library.model;

/**
 * Enum representing different types of patrons with their borrowing limits.
 */
public enum PatronType {
    STUDENT(3, 14),
    FACULTY(10, 30),
    GENERAL(5, 21);
    
    private final int maxBooksAllowed;
    private final int maxBorrowDays;
    
    PatronType(int maxBooksAllowed, int maxBorrowDays) {
        this.maxBooksAllowed = maxBooksAllowed;
        this.maxBorrowDays = maxBorrowDays;
    }
    
    public int getMaxBooksAllowed() {
        return maxBooksAllowed;
    }
    
    public int getMaxBorrowDays() {
        return maxBorrowDays;
    }
}
