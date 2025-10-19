package com.library.service;

import com.library.model.Patron;
import com.library.model.PatronType;
import com.library.repository.PatronRepository;
import com.library.util.Logger;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Patron management operations.
 * Demonstrates Single Responsibility Principle.
 */
public class PatronService {
    private final PatronRepository patronRepository;
    private final Logger logger;
    
    public PatronService(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
        this.logger = Logger.getInstance();
    }
    
    /**
     * Add a new patron to the system
     */
    public void addPatron(Patron patron) {
        if (patronRepository.exists(patron.getPatronId())) {
            logger.warn("Patron with ID " + patron.getPatronId() + " already exists");
            throw new IllegalArgumentException("Patron with ID " + patron.getPatronId() + " already exists");
        }
        patronRepository.save(patron);
        logger.info("Patron added: " + patron.getName() + " (ID: " + patron.getPatronId() + ")");
    }
    
    /**
     * Update patron information
     */
    public void updatePatron(String patronId, String name, String email, String phone) {
        Optional<Patron> patronOpt = patronRepository.findById(patronId);
        if (patronOpt.isEmpty()) {
            logger.error("Patron with ID " + patronId + " not found");
            throw new IllegalArgumentException("Patron with ID " + patronId + " not found");
        }
        
        Patron patron = patronOpt.get();
        patron.setName(name);
        patron.setEmail(email);
        patron.setPhone(phone);
        patronRepository.save(patron);
        logger.info("Patron updated: " + patron.getName() + " (ID: " + patronId + ")");
    }
    
    /**
     * Update patron type
     */
    public void updatePatronType(String patronId, PatronType patronType) {
        Optional<Patron> patronOpt = patronRepository.findById(patronId);
        if (patronOpt.isEmpty()) {
            logger.error("Patron with ID " + patronId + " not found");
            throw new IllegalArgumentException("Patron with ID " + patronId + " not found");
        }
        
        Patron patron = patronOpt.get();
        patron.setPatronType(patronType);
        patronRepository.save(patron);
        logger.info("Patron type updated: " + patron.getName() + " -> " + patronType);
    }
    
    /**
     * Find patron by ID
     */
    public Optional<Patron> findPatronById(String patronId) {
        return patronRepository.findById(patronId);
    }
    
    /**
     * Get all patrons
     */
    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }
    
    /**
     * Remove a patron
     */
    public void removePatron(String patronId) {
        Optional<Patron> patronOpt = patronRepository.findById(patronId);
        if (patronOpt.isEmpty()) {
            logger.error("Patron with ID " + patronId + " not found");
            throw new IllegalArgumentException("Patron with ID " + patronId + " not found");
        }
        
        Patron patron = patronOpt.get();
        if (!patron.getCurrentBorrowedBooks().isEmpty()) {
            logger.warn("Cannot remove patron with borrowed books: " + patronId);
            throw new IllegalStateException("Cannot remove patron with borrowed books");
        }
        
        patronRepository.delete(patronId);
        logger.info("Patron removed: " + patron.getName() + " (ID: " + patronId + ")");
    }
    
    /**
     * Get total patron count
     */
    public int getTotalPatronCount() {
        return patronRepository.count();
    }
}
