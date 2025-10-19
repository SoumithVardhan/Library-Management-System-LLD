package com.library;

import com.library.factory.PatronFactory;
import com.library.model.*;
import com.library.observer.EmailNotificationObserver;
import com.library.observer.SMSNotificationObserver;
import com.library.repository.*;
import com.library.service.*;
import com.library.strategy.*;
import com.library.util.Logger;

import java.util.List;

/**
 * Main application class demonstrating the Library Management System.
 * Demonstrates all core functionalities and design patterns.
 */
public class LibraryManagementSystem {
    
    private final BookService bookService;
    private final PatronService patronService;
    private final LendingService lendingService;
    private final BranchService branchService;
    private final ReservationService reservationService;
    private final BookTransferService transferService;
    private final RecommendationService recommendationService;
    private final Logger logger;
    
    public LibraryManagementSystem() {
        // Initialize repositories
        BookRepository bookRepository = new BookRepository();
        PatronRepository patronRepository = new PatronRepository();
        BorrowingRecordRepository recordRepository = new BorrowingRecordRepository();
        BranchRepository branchRepository = new BranchRepository();
        
        // Initialize services
        this.bookService = new BookService(bookRepository);
        this.patronService = new PatronService(patronRepository);
        this.lendingService = new LendingService(bookRepository, patronRepository, recordRepository);
        this.branchService = new BranchService(branchRepository);
        this.reservationService = new ReservationService(bookRepository, patronRepository);
        this.transferService = new BookTransferService(bookRepository, branchRepository);
        this.recommendationService = new RecommendationService(bookRepository, patronRepository, recordRepository);
        this.logger = Logger.getInstance();
    }
    
    public BookService getBookService() {
        return bookService;
    }
    
    public PatronService getPatronService() {
        return patronService;
    }
    
    public LendingService getLendingService() {
        return lendingService;
    }
    
    public BranchService getBranchService() {
        return branchService;
    }
    
    public ReservationService getReservationService() {
        return reservationService;
    }
    
    public BookTransferService getTransferService() {
        return transferService;
    }
    
    public RecommendationService getRecommendationService() {
        return recommendationService;
    }
    
    public static void main(String[] args) {
        LibraryManagementSystem lms = new LibraryManagementSystem();
        Logger logger = Logger.getInstance();
        
        logger.info("=== Library Management System Started ===\n");
        
        try {
            // Demo 1: Create Library Branches
            logger.info("--- Creating Library Branches ---");
            LibraryBranch mainBranch = lms.getBranchService().createBranch("Main Library", "123 Main St");
            LibraryBranch eastBranch = lms.getBranchService().createBranch("East Branch", "456 East Ave");
            System.out.println();
            
            // Demo 2: Add Books to Inventory
            logger.info("--- Adding Books to Inventory ---");
            Book book1 = new Book("978-0-13-468599-1", "Clean Code", "Robert C. Martin", 2008, mainBranch.getBranchId());
            Book book2 = new Book("978-0-201-63361-0", "Design Patterns", "Gang of Four", 1994, mainBranch.getBranchId());
            Book book3 = new Book("978-0-13-235088-4", "Clean Architecture", "Robert C. Martin", 2017, mainBranch.getBranchId());
            Book book4 = new Book("978-0-13-597783-5", "Effective Java", "Joshua Bloch", 2018, eastBranch.getBranchId());
            Book book5 = new Book("978-0-13-468599-2", "The Pragmatic Programmer", "Andrew Hunt", 1999, eastBranch.getBranchId());
            
            lms.getBookService().addBook(book1);
            lms.getBookService().addBook(book2);
            lms.getBookService().addBook(book3);
            lms.getBookService().addBook(book4);
            lms.getBookService().addBook(book5);
            
            mainBranch.addBookToInventory(book1.getIsbn());
            mainBranch.addBookToInventory(book2.getIsbn());
            mainBranch.addBookToInventory(book3.getIsbn());
            eastBranch.addBookToInventory(book4.getIsbn());
            eastBranch.addBookToInventory(book5.getIsbn());
            System.out.println();
            
            // Demo 3: Create Patrons using Factory Pattern
            logger.info("--- Creating Patrons (Factory Pattern) ---");
            Patron student1 = PatronFactory.createStudent("Alice Johnson", "alice@email.com", "555-0101");
            Patron faculty1 = PatronFactory.createFaculty("Dr. Bob Smith", "bob@email.com", "555-0102");
            Patron general1 = PatronFactory.createGeneralMember("Charlie Brown", "charlie@email.com", "555-0103");
            
            lms.getPatronService().addPatron(student1);
            lms.getPatronService().addPatron(faculty1);
            lms.getPatronService().addPatron(general1);
            System.out.println();
            
            // Demo 4: Attach Observers for Notifications (Observer Pattern)
            logger.info("--- Setting up Notifications (Observer Pattern) ---");
            lms.getLendingService().attach(new EmailNotificationObserver(student1.getEmail()));
            lms.getLendingService().attach(new SMSNotificationObserver(student1.getPhone()));
            lms.getReservationService().attach(new EmailNotificationObserver(faculty1.getEmail()));
            System.out.println();
            
            // Demo 5: Search Books using Strategy Pattern
            logger.info("--- Searching Books (Strategy Pattern) ---");
            
            // Search by title
            lms.getBookService().setSearchStrategy(new TitleSearchStrategy());
            List<Book> titleResults = lms.getBookService().searchBooks("Clean");
            System.out.println("📚 Books with 'Clean' in title: " + titleResults.size());
            titleResults.forEach(b -> System.out.println("  - " + b.getTitle()));
            
            // Search by author
            lms.getBookService().setSearchStrategy(new AuthorSearchStrategy());
            List<Book> authorResults = lms.getBookService().searchBooks("Robert");
            System.out.println("\n📚 Books by authors containing 'Robert': " + authorResults.size());
            authorResults.forEach(b -> System.out.println("  - " + b.getTitle() + " by " + b.getAuthor()));
            
            // Search by ISBN
            lms.getBookService().setSearchStrategy(new ISBNSearchStrategy());
            List<Book> isbnResults = lms.getBookService().searchBooks("978-0-13");
            System.out.println("\n📚 Books with ISBN starting '978-0-13': " + isbnResults.size());
            System.out.println();
            
            // Demo 6: Checkout Books
            logger.info("--- Book Checkout Process ---");
            BorrowingRecord record1 = lms.getLendingService().checkoutBook(
                student1.getPatronId(), book1.getIsbn(), mainBranch.getBranchId()
            );
            System.out.println("✅ Checkout successful! Due date: " + record1.getDueDate());
            System.out.println();
            
            // Demo 7: Try to checkout the same book (should fail)
            logger.info("--- Attempting to checkout already borrowed book ---");
            try {
                lms.getLendingService().checkoutBook(
                    faculty1.getPatronId(), book1.getIsbn(), mainBranch.getBranchId()
                );
            } catch (IllegalStateException e) {
                System.out.println("❌ Expected error: " + e.getMessage());
            }
            System.out.println();
            
            // Demo 8: Reserve the checked-out book
            logger.info("--- Book Reservation System ---");
            Reservation reservation = lms.getReservationService().reserveBook(
                faculty1.getPatronId(), book1.getIsbn()
            );
            System.out.println("✅ Reservation created! ID: " + reservation.getReservationId());
            int position = lms.getReservationService().getQueuePosition(reservation.getReservationId());
            System.out.println("📋 Queue position: " + position);
            System.out.println();
            
            // Demo 9: Return Book and Notify Next in Queue
            logger.info("--- Book Return and Reservation Notification ---");
            lms.getLendingService().returnBook(book1.getIsbn(), student1.getPatronId());
            lms.getReservationService().notifyNextInQueue(book1.getIsbn());
            System.out.println();
            
            // Demo 10: Book Transfer Between Branches
            logger.info("--- Transferring Books Between Branches ---");
            System.out.println("📍 Book current location: " + book2.getCurrentBranchId());
            lms.getTransferService().transferBook(book2.getIsbn(), mainBranch.getBranchId(), eastBranch.getBranchId());
            System.out.println("📍 Book new location: " + book2.getCurrentBranchId());
            System.out.println();
            
            // Demo 11: Checkout more books for recommendation demo
            logger.info("--- Building Borrowing History for Recommendations ---");
            lms.getLendingService().checkoutBook(student1.getPatronId(), book3.getIsbn(), mainBranch.getBranchId());
            lms.getLendingService().checkoutBook(general1.getPatronId(), book4.getIsbn(), eastBranch.getBranchId());
            System.out.println();
            
            // Demo 12: Get Book Recommendations
            logger.info("--- Book Recommendation System ---");
            List<Book> recommendations = lms.getRecommendationService().getRecommendations(student1.getPatronId(), 3);
            System.out.println("📖 Recommendations for " + student1.getName() + ":");
            if (recommendations.isEmpty()) {
                System.out.println("  (Building more history for better recommendations)");
            } else {
                recommendations.forEach(b -> System.out.println("  - " + b.getTitle() + " by " + b.getAuthor()));
            }
            System.out.println();
            
            // Demo 13: Get Popular Books
            logger.info("--- Popular Books ---");
            List<Book> popularBooks = lms.getRecommendationService().getPopularBooks(3);
            System.out.println("🔥 Most Popular Books:");
            popularBooks.forEach(b -> System.out.println("  - " + b.getTitle()));
            System.out.println();
            
            // Demo 14: View Active Borrowings
            logger.info("--- Active Borrowings Report ---");
            List<BorrowingRecord> activeRecords = lms.getLendingService().getActiveBorrowings();
            System.out.println("📊 Total Active Borrowings: " + activeRecords.size());
            activeRecords.forEach(r -> {
                lms.getBookService().findBookByIsbn(r.getIsbn()).ifPresent(b -> 
                    System.out.println("  - " + b.getTitle() + " (Due: " + r.getDueDate() + ")")
                );
            });
            System.out.println();
            
            // Demo 15: System Statistics
            logger.info("--- System Statistics ---");
            System.out.println("📚 Total Books: " + lms.getBookService().getTotalBookCount());
            System.out.println("👥 Total Patrons: " + lms.getPatronService().getTotalPatronCount());
            System.out.println("🏢 Total Branches: " + lms.getBranchService().getAllBranches().size());
            System.out.println("✅ Available Books: " + lms.getBookService().getAvailableBooks().size());
            System.out.println();
            
            logger.info("=== Library Management System Demo Completed Successfully ===");
            
        } catch (Exception e) {
            logger.error("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
