package com.library.repository;

import com.library.model.Book;
import com.library.model.BookStatus;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for managing Book data (Repository Pattern).
 */
public class BookRepository {
    private final Map<String, Book> books;
    
    public BookRepository() {
        this.books = new HashMap<>();
    }
    
    public void save(Book book) {
        books.put(book.getIsbn(), book);
    }
    
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }
    
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }
    
    public List<Book> findByBranch(String branchId) {
        return books.values().stream()
                .filter(book -> book.getCurrentBranchId().equals(branchId))
                .collect(Collectors.toList());
    }
    
    public List<Book> findByStatus(BookStatus status) {
        return books.values().stream()
                .filter(book -> book.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    public boolean delete(String isbn) {
        return books.remove(isbn) != null;
    }
    
    public boolean exists(String isbn) {
        return books.containsKey(isbn);
    }
    
    public int count() {
        return books.size();
    }
}
