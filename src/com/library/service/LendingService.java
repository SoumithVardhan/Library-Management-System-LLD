package com.library.service;

import com.library.model.*;
import com.library.observer.Observer;
import com.library.observer.Subject;
import com.library.repository.BookRepository;
import com.library.repository.BorrowingRecordRepository;
import com.library.repository.PatronRepository;
import com.library.util.IdGenerator;
import com.library.util.Logger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing book lending operations.
 * Demonstrates Subject in Observer pattern.
 */
public class LendingService implements Subject {
    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;
    private final BorrowingRecordRepository recordRepository;
    private final Logger logger;
    private final List<Observer> observers;
    
    public LendingService(BookRepository bookRepository, 
                         PatronRepository patronRepository,
                         BorrowingRecordRepository recordRepository) {
        this.bookRepository = bookRepository;
        this.patronRepository = patronRepository;
        this.recordRepository = recordRepository;
        this.logger = Logger.getInstance();
        this.observers = new ArrayList<>();
    }
    
    /**
     * Checkout a book to a patron
     */
    public BorrowingRecord checkoutBook(String patronId, String isbn, String branchId) {
        // Validate patron
        Optional<Patron> patronOpt = patronRepository.findById(patronId);
        if (patronOpt.isEmpty()) {
            logger.error("Patron not found: " + patronId);
            throw new IllegalArgumentException("Patron not found");
        }
        
        Patron patron = patronOpt.get();
        
        // Check if patron can borrow more books
        if (!patron.canBorrowMoreBooks()) {
            logger.warn("Patron " + patronId + " has reached borrowing limit");
            throw new IllegalStateException("Patron has reached maximum borrowing limit");
        }
        
        // Validate book
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        if (bookOpt.isEmpty()) {
            logger.error("Book not found: " + isbn);
            throw new IllegalArgumentException("Book not found");
        }
        
        Book book = bookOpt.get();
        
        // Check if book is available
        if (book.getStatus() != BookStatus.AVAILABLE) {
            logger.warn("Book is not available: " + isbn + " (Status: " + book.getStatus() + ")");
            throw new IllegalStateException("Book is not available for checkout");
        }
        
        // Create borrowing record
        LocalDate checkoutDate = LocalDate.now();
        LocalDate dueDate = checkoutDate.plusDays(patron.getPatronType().getMaxBorrowDays());
        String recordId = IdGenerator.generateRecordId();
        
        BorrowingRecord record = new BorrowingRecord(
            recordId, patronId, isbn, checkoutDate, dueDate, branchId
        );
        
        // Update book status
        book.setStatus(BookStatus.CHECKED_OUT);
        bookRepository.save(book);
        
        // Update patron records
        patron.addCurrentBorrowedBook(isbn);
        patron.addBorrowingRecord(record);
        patronRepository.save(patron);
        
        // Save borrowing record
        recordRepository.save(record);
        
        logger.info("Book checked out: " + book.getTitle() + " to " + patron.getName());
        
        // Notify observers
        String message = "Book '" + book.getTitle() + "' checked out successfully. Due date: " + dueDate;
        notifyObservers(message);
        
        return record;
    }
    
    /**
     * Return a book
     */
    public void returnBook(String isbn, String patronId) {
        // Validate patron
        Optional<Patron> patronOpt = patronRepository.findById(patronId);
        if (patronOpt.isEmpty()) {
            logger.error("Patron not found: " + patronId);
            throw new IllegalArgumentException("Patron not found");
        }
        
        Patron patron = patronOpt.get();
        
        // Validate book
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        if (bookOpt.isEmpty()) {
            logger.error("Book not found: " + isbn);
            throw new IllegalArgumentException("Book not found");
        }
        
        Book book = bookOpt.get();
        
        // Find active borrowing record
        List<BorrowingRecord> records = recordRepository.findByPatronId(patronId);
        BorrowingRecord activeRecord = records.stream()
            .filter(r -> r.getIsbn().equals(isbn) && !r.isReturned())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No active borrowing record found"));
        
        // Update return date
        activeRecord.setReturnDate(LocalDate.now());
        recordRepository.save(activeRecord);
        
        // Update book status
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);
        
        // Update patron records
        patron.removeCurrentBorrowedBook(isbn);
        patronRepository.save(patron);
        
        logger.info("Book returned: " + book.getTitle() + " by " + patron.getName());
        
        // Check if overdue
        if (activeRecord.isOverdue()) {
            String message = "Book '" + book.getTitle() + "' was returned late. Please check for any late fees.";
            notifyObservers(message);
        } else {
            String message = "Book '" + book.getTitle() + "' returned successfully. Thank you!";
            notifyObservers(message);
        }
    }
    
    /**
     * Get borrowing history for a patron
     */
    public List<BorrowingRecord> getPatronBorrowingHistory(String patronId) {
        return recordRepository.findByPatronId(patronId);
    }
    
    /**
     * Get all active borrowing records
     */
    public List<BorrowingRecord> getActiveBorrowings() {
        return recordRepository.findActiveRecords();
    }
    
    /**
     * Get all overdue borrowing records
     */
    public List<BorrowingRecord> getOverdueBorrowings() {
        return recordRepository.findOverdueRecords();
    }
    
    /**
     * Renew a book
     */
    public void renewBook(String isbn, String patronId) {
        // Find active borrowing record
        List<BorrowingRecord> records = recordRepository.findByPatronId(patronId);
        BorrowingRecord activeRecord = records.stream()
            .filter(r -> r.getIsbn().equals(isbn) && !r.isReturned())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No active borrowing record found"));
        
        // Check if book is reserved by someone else
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        if (bookOpt.isEmpty()) {
            throw new IllegalArgumentException("Book not found");
        }
        
        Book book = bookOpt.get();
        
        // Extend due date
        Optional<Patron> patronOpt = patronRepository.findById(patronId);
        if (patronOpt.isEmpty()) {
            throw new IllegalArgumentException("Patron not found");
        }
        
        Patron patron = patronOpt.get();
        LocalDate newDueDate = activeRecord.getDueDate().plusDays(patron.getPatronType().getMaxBorrowDays());
        activeRecord.setDueDate(newDueDate);
        recordRepository.save(activeRecord);
        
        logger.info("Book renewed: " + book.getTitle() + " for " + patron.getName() + ". New due date: " + newDueDate);
        
        String message = "Book '" + book.getTitle() + "' renewed successfully. New due date: " + newDueDate;
        notifyObservers(message);
    }
    
    // Observer pattern implementation
    @Override
    public void attach(Observer observer) {
        observers.add(observer);
        logger.info("Observer attached");
    }
    
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
        logger.info("Observer detached");
    }
    
    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
