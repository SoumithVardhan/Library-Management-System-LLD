package com.library.service;

import com.library.model.Book;
import com.library.model.BookStatus;
import com.library.repository.BookRepository;
import com.library.strategy.SearchStrategy;
import com.library.util.Logger;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Book management operations.
 * Demonstrates Single Responsibility and Dependency Inversion principles.
 */
public class BookService {
    private final BookRepository bookRepository;
    private final Logger logger;
    private SearchStrategy searchStrategy;
    
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.logger = Logger.getInstance();
    }
    
    /**
     * Add a new book to the library inventory
     */
    public void addBook(Book book) {
        if (bookRepository.exists(book.getIsbn())) {
            logger.warn("Book with ISBN " + book.getIsbn() + " already exists");
            throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        bookRepository.save(book);
        logger.info("Book added: " + book.getTitle() + " (ISBN: " + book.getIsbn() + ")");
    }
    
    /**
     * Update book information
     */
    public void updateBook(String isbn, String title, String author, int publicationYear) {
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        if (bookOpt.isEmpty()) {
            logger.error("Book with ISBN " + isbn + " not found");
            throw new IllegalArgumentException("Book with ISBN " + isbn + " not found");
        }
        
        Book book = bookOpt.get();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublicationYear(publicationYear);
        bookRepository.save(book);
        logger.info("Book updated: " + book.getTitle() + " (ISBN: " + isbn + ")");
    }
    
    /**
     * Remove a book from the inventory
     */
    public void removeBook(String isbn) {
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        if (bookOpt.isEmpty()) {
            logger.error("Book with ISBN " + isbn + " not found");
            throw new IllegalArgumentException("Book with ISBN " + isbn + " not found");
        }
        
        Book book = bookOpt.get();
        if (book.getStatus() == BookStatus.CHECKED_OUT) {
            logger.warn("Cannot remove book that is currently checked out: " + isbn);
            throw new IllegalStateException("Cannot remove book that is currently checked out");
        }
        
        bookRepository.delete(isbn);
        logger.info("Book removed: " + book.getTitle() + " (ISBN: " + isbn + ")");
    }
    
    /**
     * Find a book by ISBN
     */
    public Optional<Book> findBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    /**
     * Get all books in the library
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    /**
     * Get books by branch
     */
    public List<Book> getBooksByBranch(String branchId) {
        return bookRepository.findByBranch(branchId);
    }
    
    /**
     * Get available books
     */
    public List<Book> getAvailableBooks() {
        return bookRepository.findByStatus(BookStatus.AVAILABLE);
    }
    
    /**
     * Set search strategy (Strategy Pattern)
     */
    public void setSearchStrategy(SearchStrategy strategy) {
        this.searchStrategy = strategy;
    }
    
    /**
     * Search books using the current strategy
     */
    public List<Book> searchBooks(String query) {
        if (searchStrategy == null) {
            logger.error("Search strategy not set");
            throw new IllegalStateException("Search strategy not set");
        }
        List<Book> results = searchStrategy.search(bookRepository.findAll(), query);
        logger.info("Search completed: Found " + results.size() + " books for query: " + query);
        return results;
    }
    
    /**
     * Update book status
     */
    public void updateBookStatus(String isbn, BookStatus status) {
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        if (bookOpt.isEmpty()) {
            logger.error("Book with ISBN " + isbn + " not found");
            throw new IllegalArgumentException("Book with ISBN " + isbn + " not found");
        }
        
        Book book = bookOpt.get();
        book.setStatus(status);
        bookRepository.save(book);
        logger.info("Book status updated: " + book.getTitle() + " -> " + status);
    }
    
    /**
     * Get total book count
     */
    public int getTotalBookCount() {
        return bookRepository.count();
    }
}
