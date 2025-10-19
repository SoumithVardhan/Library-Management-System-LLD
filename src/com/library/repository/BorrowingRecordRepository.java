package com.library.repository;

import com.library.model.BorrowingRecord;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for managing BorrowingRecord data.
 */
public class BorrowingRecordRepository {
    private final Map<String, BorrowingRecord> records;
    
    public BorrowingRecordRepository() {
        this.records = new HashMap<>();
    }
    
    public void save(BorrowingRecord record) {
        records.put(record.getRecordId(), record);
    }
    
    public Optional<BorrowingRecord> findById(String recordId) {
        return Optional.ofNullable(records.get(recordId));
    }
    
    public List<BorrowingRecord> findAll() {
        return new ArrayList<>(records.values());
    }
    
    public List<BorrowingRecord> findByPatronId(String patronId) {
        return records.values().stream()
                .filter(record -> record.getPatronId().equals(patronId))
                .collect(Collectors.toList());
    }
    
    public List<BorrowingRecord> findByIsbn(String isbn) {
        return records.values().stream()
                .filter(record -> record.getIsbn().equals(isbn))
                .collect(Collectors.toList());
    }
    
    public List<BorrowingRecord> findActiveRecords() {
        return records.values().stream()
                .filter(record -> !record.isReturned())
                .collect(Collectors.toList());
    }
    
    public List<BorrowingRecord> findOverdueRecords() {
        return records.values().stream()
                .filter(BorrowingRecord::isOverdue)
                .collect(Collectors.toList());
    }
}
