package com.library.repository;

import com.library.model.Patron;
import java.util.*;

/**
 * Repository for managing Patron data.
 */
public class PatronRepository {
    private final Map<String, Patron> patrons;
    
    public PatronRepository() {
        this.patrons = new HashMap<>();
    }
    
    public void save(Patron patron) {
        patrons.put(patron.getPatronId(), patron);
    }
    
    public Optional<Patron> findById(String patronId) {
        return Optional.ofNullable(patrons.get(patronId));
    }
    
    public List<Patron> findAll() {
        return new ArrayList<>(patrons.values());
    }
    
    public boolean delete(String patronId) {
        return patrons.remove(patronId) != null;
    }
    
    public boolean exists(String patronId) {
        return patrons.containsKey(patronId);
    }
    
    public int count() {
        return patrons.size();
    }
}
