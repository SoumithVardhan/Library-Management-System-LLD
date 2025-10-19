package com.library.model;

import java.util.Objects;

/**
 * Represents a Book in the library system.
 */
public class Book {
    private final String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private BookStatus status;
    private String currentBranchId;
    
    public Book(String isbn, String title, String author, int publicationYear, String branchId) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.status = BookStatus.AVAILABLE;
        this.currentBranchId = branchId;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public int getPublicationYear() {
        return publicationYear;
    }
    
    public BookStatus getStatus() {
        return status;
    }
    
    public String getCurrentBranchId() {
        return currentBranchId;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }
    
    public void setStatus(BookStatus status) {
        this.status = status;
    }
    
    public void setCurrentBranchId(String currentBranchId) {
        this.currentBranchId = currentBranchId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publicationYear=" + publicationYear +
                ", status=" + status +
                ", currentBranchId='" + currentBranchId + '\'' +
                '}';
    }
}
