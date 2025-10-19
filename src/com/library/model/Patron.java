package com.library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Patron (library member).
 */
public class Patron {
    private final String patronId;
    private String name;
    private String email;
    private String phone;
    private List<BorrowingRecord> borrowingHistory;
    private List<String> currentBorrowedBooks;
    private List<String> reservedBooks;
    private PatronType patronType;
    
    public Patron(String patronId, String name, String email, String phone, PatronType patronType) {
        this.patronId = patronId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.patronType = patronType;
        this.borrowingHistory = new ArrayList<>();
        this.currentBorrowedBooks = new ArrayList<>();
        this.reservedBooks = new ArrayList<>();
    }
    
    public String getPatronId() {
        return patronId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public List<BorrowingRecord> getBorrowingHistory() {
        return new ArrayList<>(borrowingHistory);
    }
    
    public List<String> getCurrentBorrowedBooks() {
        return new ArrayList<>(currentBorrowedBooks);
    }
    
    public List<String> getReservedBooks() {
        return new ArrayList<>(reservedBooks);
    }
    
    public PatronType getPatronType() {
        return patronType;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public void setPatronType(PatronType patronType) {
        this.patronType = patronType;
    }
    
    public void addBorrowingRecord(BorrowingRecord record) {
        this.borrowingHistory.add(record);
    }
    
    public void addCurrentBorrowedBook(String isbn) {
        this.currentBorrowedBooks.add(isbn);
    }
    
    public void removeCurrentBorrowedBook(String isbn) {
        this.currentBorrowedBooks.remove(isbn);
    }
    
    public void addReservedBook(String isbn) {
        this.reservedBooks.add(isbn);
    }
    
    public void removeReservedBook(String isbn) {
        this.reservedBooks.remove(isbn);
    }
    
    public boolean canBorrowMoreBooks() {
        return currentBorrowedBooks.size() < patronType.getMaxBooksAllowed();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patron patron = (Patron) o;
        return Objects.equals(patronId, patron.patronId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(patronId);
    }
    
    @Override
    public String toString() {
        return "Patron{" +
                "patronId='" + patronId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", patronType=" + patronType +
                ", currentBorrowedBooks=" + currentBorrowedBooks.size() +
                '}';
    }
}
