package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowingRecord;
import com.library.model.Patron;
import com.library.repository.BookRepository;
import com.library.repository.BorrowingRecordRepository;
import com.library.repository.PatronRepository;
import com.library.util.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for providing book recommendations to patrons.
 * Uses collaborative filtering and content-based filtering approaches.
 */
public class RecommendationService {
    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;
    private final BorrowingRecordRepository recordRepository;
    private final Logger logger;
    
    public RecommendationService(BookRepository bookRepository, 
                                PatronRepository patronRepository,
                                BorrowingRecordRepository recordRepository) {
        this.bookRepository = bookRepository;
        this.patronRepository = patronRepository;
        this.recordRepository = recordRepository;
        this.logger = Logger.getInstance();
    }
    
    /**
     * Get book recommendations for a patron based on their borrowing history
     */
    public List<Book> getRecommendations(String patronId, int limit) {
        Optional<Patron> patronOpt = patronRepository.findById(patronId);
        if (patronOpt.isEmpty()) {
            logger.error("Patron not found: " + patronId);
            return Collections.emptyList();
        }
        
        Patron patron = patronOpt.get();
        List<BorrowingRecord> history = patron.getBorrowingHistory();
        
        if (history.isEmpty()) {
            // If no history, return popular books
            return getPopularBooks(limit);
        }
        
        // Get authors and genres from borrowing history
        Set<String> favoriteAuthors = new HashSet<>();
        Map<String, Integer> authorFrequency = new HashMap<>();
        
        for (BorrowingRecord record : history) {
            Optional<Book> bookOpt = bookRepository.findByIsbn(record.getIsbn());
            bookOpt.ifPresent(book -> {
                String author = book.getAuthor();
                favoriteAuthors.add(author);
                authorFrequency.put(author, authorFrequency.getOrDefault(author, 0) + 1);
            });
        }
        
        // Get all books by favorite authors that patron hasn't borrowed
        Set<String> borrowedIsbns = history.stream()
            .map(BorrowingRecord::getIsbn)
            .collect(Collectors.toSet());
        
        List<Book> recommendations = bookRepository.findAll().stream()
            .filter(book -> !borrowedIsbns.contains(book.getIsbn()))
            .filter(book -> favoriteAuthors.contains(book.getAuthor()))
            .sorted((b1, b2) -> {
                int freq1 = authorFrequency.getOrDefault(b1.getAuthor(), 0);
                int freq2 = authorFrequency.getOrDefault(b2.getAuthor(), 0);
                return Integer.compare(freq2, freq1);
            })
            .limit(limit)
            .collect(Collectors.toList());
        
        logger.info("Generated " + recommendations.size() + " recommendations for patron: " + patronId);
        return recommendations;
    }
    
    /**
     * Get recommendations based on similar patrons (collaborative filtering)
     */
    public List<Book> getCollaborativeRecommendations(String patronId, int limit) {
        Optional<Patron> patronOpt = patronRepository.findById(patronId);
        if (patronOpt.isEmpty()) {
            logger.error("Patron not found: " + patronId);
            return Collections.emptyList();
        }
        
        Patron targetPatron = patronOpt.get();
        List<BorrowingRecord> targetHistory = targetPatron.getBorrowingHistory();
        
        if (targetHistory.isEmpty()) {
            return getPopularBooks(limit);
        }
        
        // Get books borrowed by target patron
        Set<String> targetBooks = targetHistory.stream()
            .map(BorrowingRecord::getIsbn)
            .collect(Collectors.toSet());
        
        // Find similar patrons based on common books
        Map<String, Integer> similarityScores = new HashMap<>();
        
        for (Patron otherPatron : patronRepository.findAll()) {
            if (otherPatron.getPatronId().equals(patronId)) {
                continue;
            }
            
            Set<String> otherBooks = otherPatron.getBorrowingHistory().stream()
                .map(BorrowingRecord::getIsbn)
                .collect(Collectors.toSet());
            
            // Calculate similarity (number of common books)
            Set<String> commonBooks = new HashSet<>(targetBooks);
            commonBooks.retainAll(otherBooks);
            
            if (!commonBooks.isEmpty()) {
                similarityScores.put(otherPatron.getPatronId(), commonBooks.size());
            }
        }
        
        // Get books borrowed by similar patrons that target hasn't borrowed
        Map<String, Integer> bookScores = new HashMap<>();
        
        for (Map.Entry<String, Integer> entry : similarityScores.entrySet()) {
            String similarPatronId = entry.getKey();
            int similarity = entry.getValue();
            
            Optional<Patron> similarPatronOpt = patronRepository.findById(similarPatronId);
            if (similarPatronOpt.isEmpty()) {
                continue;
            }
            
            Patron similarPatron = similarPatronOpt.get();
            for (BorrowingRecord record : similarPatron.getBorrowingHistory()) {
                String isbn = record.getIsbn();
                if (!targetBooks.contains(isbn)) {
                    bookScores.put(isbn, bookScores.getOrDefault(isbn, 0) + similarity);
                }
            }
        }
        
        // Sort books by score and return top recommendations
        List<Book> recommendations = bookScores.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> bookRepository.findByIsbn(entry.getKey()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        
        logger.info("Generated " + recommendations.size() + 
                   " collaborative recommendations for patron: " + patronId);
        return recommendations;
    }
    
    /**
     * Get popular books based on borrowing frequency
     */
    public List<Book> getPopularBooks(int limit) {
        Map<String, Integer> borrowFrequency = new HashMap<>();
        
        for (BorrowingRecord record : recordRepository.findAll()) {
            String isbn = record.getIsbn();
            borrowFrequency.put(isbn, borrowFrequency.getOrDefault(isbn, 0) + 1);
        }
        
        List<Book> popularBooks = borrowFrequency.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> bookRepository.findByIsbn(entry.getKey()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        
        logger.info("Retrieved " + popularBooks.size() + " popular books");
        return popularBooks;
    }
    
    /**
     * Get recommendations by author
     */
    public List<Book> getRecommendationsByAuthor(String patronId, String author, int limit) {
        Optional<Patron> patronOpt = patronRepository.findById(patronId);
        if (patronOpt.isEmpty()) {
            logger.error("Patron not found: " + patronId);
            return Collections.emptyList();
        }
        
        Patron patron = patronOpt.get();
        Set<String> borrowedIsbns = patron.getBorrowingHistory().stream()
            .map(BorrowingRecord::getIsbn)
            .collect(Collectors.toSet());
        
        List<Book> recommendations = bookRepository.findAll().stream()
            .filter(book -> book.getAuthor().equalsIgnoreCase(author))
            .filter(book -> !borrowedIsbns.contains(book.getIsbn()))
            .limit(limit)
            .collect(Collectors.toList());
        
        logger.info("Generated " + recommendations.size() + 
                   " recommendations by author '" + author + "' for patron: " + patronId);
        return recommendations;
    }
}
