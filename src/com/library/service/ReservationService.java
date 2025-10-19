package com.library.service;

import com.library.model.*;
import com.library.observer.Observer;
import com.library.observer.Subject;
import com.library.repository.BookRepository;
import com.library.repository.PatronRepository;
import com.library.util.IdGenerator;
import com.library.util.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing book reservations.
 * Implements Observer pattern for notifications.
 */
public class ReservationService implements Subject {
    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;
    private final Map<String, Queue<Reservation>> reservationQueues; // ISBN -> Queue of reservations
    private final Map<String, Reservation> reservationById; // ReservationId -> Reservation
    private final Logger logger;
    private final List<Observer> observers;
    
    public ReservationService(BookRepository bookRepository, PatronRepository patronRepository) {
        this.bookRepository = bookRepository;
        this.patronRepository = patronRepository;
        this.reservationQueues = new HashMap<>();
        this.reservationById = new HashMap<>();
        this.logger = Logger.getInstance();
        this.observers = new ArrayList<>();
    }
    
    /**
     * Reserve a book that is currently checked out
     */
    public Reservation reserveBook(String patronId, String isbn) {
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
        
        // Check if book is already available
        if (book.getStatus() == BookStatus.AVAILABLE) {
            logger.warn("Book is available, no need to reserve: " + isbn);
            throw new IllegalStateException("Book is available for checkout, reservation not needed");
        }
        
        // Check if patron already has a reservation for this book
        Queue<Reservation> queue = reservationQueues.getOrDefault(isbn, new LinkedList<>());
        boolean alreadyReserved = queue.stream()
            .anyMatch(r -> r.getPatronId().equals(patronId) && r.getStatus() == ReservationStatus.ACTIVE);
        
        if (alreadyReserved) {
            logger.warn("Patron already has an active reservation for this book");
            throw new IllegalStateException("You already have an active reservation for this book");
        }
        
        // Create reservation
        String reservationId = IdGenerator.generateReservationId();
        Reservation reservation = new Reservation(reservationId, patronId, isbn);
        
        // Add to queue
        queue.add(reservation);
        reservationQueues.put(isbn, queue);
        reservationById.put(reservationId, reservation);
        
        // Add to patron's reserved books
        patron.addReservedBook(isbn);
        patronRepository.save(patron);
        
        logger.info("Book reserved: " + book.getTitle() + " for " + patron.getName() + 
                   " (Position in queue: " + queue.size() + ")");
        
        return reservation;
    }
    
    /**
     * Cancel a reservation
     */
    public void cancelReservation(String reservationId) {
        Reservation reservation = reservationById.get(reservationId);
        if (reservation == null) {
            logger.error("Reservation not found: " + reservationId);
            throw new IllegalArgumentException("Reservation not found");
        }
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        
        // Remove from queue
        Queue<Reservation> queue = reservationQueues.get(reservation.getIsbn());
        if (queue != null) {
            queue.remove(reservation);
        }
        
        // Remove from patron's reserved books
        Optional<Patron> patronOpt = patronRepository.findById(reservation.getPatronId());
        patronOpt.ifPresent(patron -> {
            patron.removeReservedBook(reservation.getIsbn());
            patronRepository.save(patron);
        });
        
        logger.info("Reservation cancelled: " + reservationId);
    }
    
    /**
     * Notify next patron in queue when book becomes available
     */
    public void notifyNextInQueue(String isbn) {
        Queue<Reservation> queue = reservationQueues.get(isbn);
        if (queue == null || queue.isEmpty()) {
            return;
        }
        
        Reservation nextReservation = queue.peek();
        if (nextReservation != null && nextReservation.getStatus() == ReservationStatus.ACTIVE) {
            // Update reservation status
            nextReservation.setStatus(ReservationStatus.NOTIFIED);
            nextReservation.setNotificationSentDate(LocalDateTime.now());
            
            // Get patron and book details
            Optional<Patron> patronOpt = patronRepository.findById(nextReservation.getPatronId());
            Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
            
            if (patronOpt.isPresent() && bookOpt.isPresent()) {
                Patron patron = patronOpt.get();
                Book book = bookOpt.get();
                
                // Update book status to reserved
                book.setStatus(BookStatus.RESERVED);
                bookRepository.save(book);
                
                String message = "Good news! The book '" + book.getTitle() + 
                               "' you reserved is now available. Please collect it within 2 days.";
                
                logger.info("Notification sent to " + patron.getName() + " for book: " + book.getTitle());
                notifyObservers(message);
            }
        }
    }
    
    /**
     * Fulfill a reservation (when patron checks out the reserved book)
     */
    public void fulfillReservation(String isbn, String patronId) {
        Queue<Reservation> queue = reservationQueues.get(isbn);
        if (queue == null || queue.isEmpty()) {
            return;
        }
        
        Reservation reservation = queue.poll();
        if (reservation != null && reservation.getPatronId().equals(patronId)) {
            reservation.setStatus(ReservationStatus.FULFILLED);
            
            // Remove from patron's reserved books
            Optional<Patron> patronOpt = patronRepository.findById(patronId);
            patronOpt.ifPresent(patron -> {
                patron.removeReservedBook(isbn);
                patronRepository.save(patron);
            });
            
            logger.info("Reservation fulfilled for patron: " + patronId + ", book: " + isbn);
        }
    }
    
    /**
     * Get all reservations for a book
     */
    public List<Reservation> getReservationsForBook(String isbn) {
        Queue<Reservation> queue = reservationQueues.get(isbn);
        if (queue == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(queue);
    }
    
    /**
     * Get all reservations for a patron
     */
    public List<Reservation> getReservationsForPatron(String patronId) {
        return reservationById.values().stream()
            .filter(r -> r.getPatronId().equals(patronId))
            .filter(r -> r.getStatus() == ReservationStatus.ACTIVE || 
                        r.getStatus() == ReservationStatus.NOTIFIED)
            .collect(Collectors.toList());
    }
    
    /**
     * Get position in queue for a reservation
     */
    public int getQueuePosition(String reservationId) {
        Reservation reservation = reservationById.get(reservationId);
        if (reservation == null) {
            return -1;
        }
        
        Queue<Reservation> queue = reservationQueues.get(reservation.getIsbn());
        if (queue == null) {
            return -1;
        }
        
        List<Reservation> queueList = new ArrayList<>(queue);
        return queueList.indexOf(reservation) + 1;
    }
    
    // Observer pattern implementation
    @Override
    public void attach(Observer observer) {
        observers.add(observer);
        logger.info("Observer attached to ReservationService");
    }
    
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
        logger.info("Observer detached from ReservationService");
    }
    
    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
