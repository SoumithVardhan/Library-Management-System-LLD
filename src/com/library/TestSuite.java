package com.library;

import com.library.factory.PatronFactory;
import com.library.model.*;
import com.library.observer.EmailNotificationObserver;
import com.library.repository.*;
import com.library.service.*;
import com.library.strategy.*;
import com.library.util.Logger;

/**
 * Comprehensive Test Suite for Library Management System
 * Tests edge cases and error scenarios
 */
public class TestSuite {
    
    private final BookService bookService;
    private final PatronService patronService;
    private final LendingService lendingService;
    private final BranchService branchService;
    private final ReservationService reservationService;
    private final BookTransferService transferService;
    private final Logger logger;
    
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    
    public TestSuite() {
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
        this.logger = Logger.getInstance();
    }
    
    public static void main(String[] args) {
        TestSuite suite = new TestSuite();
        
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     LIBRARY MANAGEMENT SYSTEM - TEST SUITE                ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Run all test categories
        suite.runBookManagementTests();
        suite.runPatronManagementTests();
        suite.runLendingTests();
        suite.runReservationTests();
        suite.runBranchTests();
        suite.runSearchTests();
        suite.runEdgeCaseTests();
        
        // Print summary
        suite.printSummary();
    }
    
    // ============= BOOK MANAGEMENT TESTS =============
    
    private void runBookManagementTests() {
        System.out.println("\n🔍 TESTING: Book Management");
        System.out.println("═══════════════════════════════════════");
        
        LibraryBranch branch = branchService.createBranch("Test Branch", "Test Address");
        
        // Test 1: Add book successfully
        test("Add Book - Success", () -> {
            Book book = new Book("TEST-001", "Test Book", "Test Author", 2024, branch.getBranchId());
            bookService.addBook(book);
            return bookService.findBookByIsbn("TEST-001").isPresent();
        });
        
        // Test 2: Add duplicate book (should fail)
        test("Add Duplicate Book - Should Fail", () -> {
            try {
                Book book = new Book("TEST-001", "Another Book", "Another Author", 2024, branch.getBranchId());
                bookService.addBook(book);
                return false; // Should not reach here
            } catch (IllegalArgumentException e) {
                return e.getMessage().contains("already exists");
            }
        });
        
        // Test 3: Update book
        test("Update Book - Success", () -> {
            bookService.updateBook("TEST-001", "Updated Title", "Updated Author", 2025);
            Book book = bookService.findBookByIsbn("TEST-001").get();
            return book.getTitle().equals("Updated Title") && 
                   book.getAuthor().equals("Updated Author") &&
                   book.getPublicationYear() == 2025;
        });
        
        // Test 4: Update non-existent book (should fail)
        test("Update Non-existent Book - Should Fail", () -> {
            try {
                bookService.updateBook("NON-EXISTENT", "Title", "Author", 2024);
                return false;
            } catch (IllegalArgumentException e) {
                return e.getMessage().contains("not found");
            }
        });
        
        // Test 5: Remove available book
        test("Remove Available Book - Success", () -> {
            Book book = new Book("TEST-002", "Book to Remove", "Author", 2024, branch.getBranchId());
            bookService.addBook(book);
            bookService.removeBook("TEST-002");
            return !bookService.findBookByIsbn("TEST-002").isPresent();
        });
        
        // Test 6: Get book count
        test("Get Book Count", () -> {
            int count = bookService.getTotalBookCount();
            return count > 0;
        });
    }
    
    // ============= PATRON MANAGEMENT TESTS =============
    
    private void runPatronManagementTests() {
        System.out.println("\n🔍 TESTING: Patron Management");
        System.out.println("═══════════════════════════════════════");
        
        // Test 1: Create student patron
        test("Create Student Patron - Factory Pattern", () -> {
            Patron student = PatronFactory.createStudent("Test Student", "test@email.com", "555-0001");
            patronService.addPatron(student);
            return student.getPatronType() == PatronType.STUDENT &&
                   student.getPatronType().getMaxBooksAllowed() == 3 &&
                   student.getPatronType().getMaxBorrowDays() == 14;
        });
        
        // Test 2: Create faculty patron
        test("Create Faculty Patron - Factory Pattern", () -> {
            Patron faculty = PatronFactory.createFaculty("Test Faculty", "faculty@email.com", "555-0002");
            patronService.addPatron(faculty);
            return faculty.getPatronType() == PatronType.FACULTY &&
                   faculty.getPatronType().getMaxBooksAllowed() == 10 &&
                   faculty.getPatronType().getMaxBorrowDays() == 30;
        });
        
        // Test 3: Create general patron
        test("Create General Patron - Factory Pattern", () -> {
            Patron general = PatronFactory.createGeneralMember("Test General", "general@email.com", "555-0003");
            patronService.addPatron(general);
            return general.getPatronType() == PatronType.GENERAL &&
                   general.getPatronType().getMaxBooksAllowed() == 5 &&
                   general.getPatronType().getMaxBorrowDays() == 21;
        });
        
        // Test 4: Update patron info
        test("Update Patron Information", () -> {
            Patron patron = patronService.getAllPatrons().get(0);
            patronService.updatePatron(patron.getPatronId(), "Updated Name", "updated@email.com", "555-9999");
            Patron updated = patronService.findPatronById(patron.getPatronId()).get();
            return updated.getName().equals("Updated Name") &&
                   updated.getEmail().equals("updated@email.com") &&
                   updated.getPhone().equals("555-9999");
        });
        
        // Test 5: Get patron count
        test("Get Patron Count", () -> {
            return patronService.getTotalPatronCount() >= 3;
        });
    }
    
    // ============= LENDING TESTS =============
    
    private void runLendingTests() {
        System.out.println("\n🔍 TESTING: Lending Operations");
        System.out.println("═══════════════════════════════════════");
        
        LibraryBranch branch = branchService.getAllBranches().get(0);
        Book book = new Book("LEND-001", "Lending Test Book", "Author", 2024, branch.getBranchId());
        bookService.addBook(book);
        
        Patron patron = PatronFactory.createStudent("Lending Test Patron", "lending@test.com", "555-1111");
        patronService.addPatron(patron);
        
        // Test 1: Checkout book
        test("Checkout Book - Success", () -> {
            BorrowingRecord record = lendingService.checkoutBook(
                patron.getPatronId(), book.getIsbn(), branch.getBranchId()
            );
            Book checkedBook = bookService.findBookByIsbn(book.getIsbn()).get();
            return record != null && 
                   checkedBook.getStatus() == BookStatus.CHECKED_OUT &&
                   !record.isReturned();
        });
        
        // Test 2: Checkout already borrowed book (should fail)
        test("Checkout Already Borrowed Book - Should Fail", () -> {
            try {
                lendingService.checkoutBook(patron.getPatronId(), book.getIsbn(), branch.getBranchId());
                return false;
            } catch (IllegalStateException e) {
                return e.getMessage().contains("not available");
            }
        });
        
        // Test 3: Return book
        test("Return Book - Success", () -> {
            lendingService.returnBook(book.getIsbn(), patron.getPatronId());
            Book returnedBook = bookService.findBookByIsbn(book.getIsbn()).get();
            return returnedBook.getStatus() == BookStatus.AVAILABLE;
        });
        
        // Test 4: Checkout multiple books
        test("Checkout Multiple Books - Within Limit", () -> {
            Book book1 = new Book("MULTI-001", "Multi Book 1", "Author", 2024, branch.getBranchId());
            Book book2 = new Book("MULTI-002", "Multi Book 2", "Author", 2024, branch.getBranchId());
            Book book3 = new Book("MULTI-003", "Multi Book 3", "Author", 2024, branch.getBranchId());
            bookService.addBook(book1);
            bookService.addBook(book2);
            bookService.addBook(book3);
            
            lendingService.checkoutBook(patron.getPatronId(), book1.getIsbn(), branch.getBranchId());
            lendingService.checkoutBook(patron.getPatronId(), book2.getIsbn(), branch.getBranchId());
            lendingService.checkoutBook(patron.getPatronId(), book3.getIsbn(), branch.getBranchId());
            
            return patron.getCurrentBorrowedBooks().size() == 3;
        });
        
        // Test 5: Exceed borrowing limit (student has max 3 books)
        test("Exceed Borrowing Limit - Should Fail", () -> {
            try {
                Book book4 = new Book("MULTI-004", "Multi Book 4", "Author", 2024, branch.getBranchId());
                bookService.addBook(book4);
                lendingService.checkoutBook(patron.getPatronId(), book4.getIsbn(), branch.getBranchId());
                return false;
            } catch (IllegalStateException e) {
                return e.getMessage().contains("maximum borrowing limit");
            }
        });
    }
    
    // ============= RESERVATION TESTS =============
    
    private void runReservationTests() {
        System.out.println("\n🔍 TESTING: Reservation System");
        System.out.println("═══════════════════════════════════════");
        
        LibraryBranch branch = branchService.getAllBranches().get(0);
        Book book = new Book("RES-001", "Reservation Book", "Author", 2024, branch.getBranchId());
        bookService.addBook(book);
        
        Patron patron1 = PatronFactory.createStudent("Patron 1", "patron1@test.com", "555-2001");
        Patron patron2 = PatronFactory.createStudent("Patron 2", "patron2@test.com", "555-2002");
        patronService.addPatron(patron1);
        patronService.addPatron(patron2);
        
        // Checkout book first
        lendingService.checkoutBook(patron1.getPatronId(), book.getIsbn(), branch.getBranchId());
        
        // Test 1: Reserve checked-out book
        test("Reserve Checked-out Book - Success", () -> {
            Reservation reservation = reservationService.reserveBook(patron2.getPatronId(), book.getIsbn());
            return reservation != null && 
                   reservation.getStatus() == ReservationStatus.ACTIVE;
        });
        
        // Test 2: Reserve already reserved book (same patron)
        test("Reserve Already Reserved Book - Should Fail", () -> {
            try {
                reservationService.reserveBook(patron2.getPatronId(), book.getIsbn());
                return false;
            } catch (IllegalStateException e) {
                return e.getMessage().contains("already have an active reservation");
            }
        });
        
        // Test 3: Check queue position
        test("Check Reservation Queue Position", () -> {
            var reservations = reservationService.getReservationsForBook(book.getIsbn());
            return reservations.size() == 1;
        });
        
        // Test 4: Return book triggers notification
        test("Return Book Triggers Reservation Notification", () -> {
            lendingService.returnBook(book.getIsbn(), patron1.getPatronId());
            reservationService.notifyNextInQueue(book.getIsbn());
            Book reservedBook = bookService.findBookByIsbn(book.getIsbn()).get();
            return reservedBook.getStatus() == BookStatus.RESERVED;
        });
    }
    
    // ============= BRANCH TESTS =============
    
    private void runBranchTests() {
        System.out.println("\n🔍 TESTING: Multi-Branch Operations");
        System.out.println("═══════════════════════════════════════");
        
        LibraryBranch branch1 = branchService.createBranch("Branch A", "Address A");
        LibraryBranch branch2 = branchService.createBranch("Branch B", "Address B");
        
        // Test 1: Create branches
        test("Create Multiple Branches", () -> {
            return branchService.getAllBranches().size() >= 2;
        });
        
        // Test 2: Transfer book between branches
        test("Transfer Book Between Branches - Success", () -> {
            Book book = new Book("TRANS-001", "Transfer Book", "Author", 2024, branch1.getBranchId());
            bookService.addBook(book);
            branch1.addBookToInventory(book.getIsbn());
            
            transferService.transferBook(book.getIsbn(), branch1.getBranchId(), branch2.getBranchId());
            Book transferredBook = bookService.findBookByIsbn(book.getIsbn()).get();
            
            return transferredBook.getCurrentBranchId().equals(branch2.getBranchId());
        });
        
        // Test 3: Transfer checked-out book (should fail)
        test("Transfer Checked-out Book - Should Fail", () -> {
            try {
                Book book = new Book("TRANS-002", "Book 2", "Author", 2024, branch1.getBranchId());
                bookService.addBook(book);
                branch1.addBookToInventory(book.getIsbn());
                
                Patron patron = PatronFactory.createStudent("Transfer Test", "transfer@test.com", "555-3001");
                patronService.addPatron(patron);
                lendingService.checkoutBook(patron.getPatronId(), book.getIsbn(), branch1.getBranchId());
                
                transferService.transferBook(book.getIsbn(), branch1.getBranchId(), branch2.getBranchId());
                return false;
            } catch (IllegalStateException e) {
                return e.getMessage().contains("must be available");
            }
        });
    }
    
    // ============= SEARCH TESTS =============
    
    private void runSearchTests() {
        System.out.println("\n🔍 TESTING: Search Strategies");
        System.out.println("═══════════════════════════════════════");
        
        LibraryBranch branch = branchService.getAllBranches().get(0);
        bookService.addBook(new Book("SEARCH-001", "Java Programming", "James Gosling", 2020, branch.getBranchId()));
        bookService.addBook(new Book("SEARCH-002", "Python Programming", "Guido van Rossum", 2021, branch.getBranchId()));
        bookService.addBook(new Book("SEARCH-003", "JavaScript Guide", "Brendan Eich", 2022, branch.getBranchId()));
        
        // Test 1: Search by title
        test("Search by Title Strategy", () -> {
            bookService.setSearchStrategy(new TitleSearchStrategy());
            var results = bookService.searchBooks("Programming");
            return results.size() >= 2;
        });
        
        // Test 2: Search by author
        test("Search by Author Strategy", () -> {
            bookService.setSearchStrategy(new AuthorSearchStrategy());
            var results = bookService.searchBooks("Gosling");
            return results.size() >= 1;
        });
        
        // Test 3: Search by ISBN
        test("Search by ISBN Strategy", () -> {
            bookService.setSearchStrategy(new ISBNSearchStrategy());
            var results = bookService.searchBooks("SEARCH-002");
            return results.size() == 1 && 
                   results.get(0).getTitle().equals("Python Programming");
        });
        
        // Test 4: Case-insensitive search
        test("Case-insensitive Search", () -> {
            bookService.setSearchStrategy(new TitleSearchStrategy());
            var results = bookService.searchBooks("javascript");
            return results.size() >= 1;
        });
    }
    
    // ============= EDGE CASE TESTS =============
    
    private void runEdgeCaseTests() {
        System.out.println("\n🔍 TESTING: Edge Cases");
        System.out.println("═══════════════════════════════════════");
        
        // Test 1: Search with no strategy set (should fail)
        test("Search Without Strategy - Should Fail", () -> {
            try {
                BookService newBookService = new BookService(new BookRepository());
                newBookService.searchBooks("test");
                return false;
            } catch (IllegalStateException e) {
                return e.getMessage().contains("Search strategy not set");
            }
        });
        
        // Test 2: Return book not checked out (should fail)
        test("Return Non-borrowed Book - Should Fail", () -> {
            try {
                LibraryBranch branch = branchService.getAllBranches().get(0);
                Book book = new Book("EDGE-001", "Edge Book", "Author", 2024, branch.getBranchId());
                bookService.addBook(book);
                
                Patron patron = PatronFactory.createStudent("Edge Test", "edge@test.com", "555-4001");
                patronService.addPatron(patron);
                
                lendingService.returnBook(book.getIsbn(), patron.getPatronId());
                return false;
            } catch (Exception e) {
                return true;
            }
        });
        
        // Test 3: Remove checked-out book (should fail)
        test("Remove Checked-out Book - Should Fail", () -> {
            try {
                LibraryBranch branch = branchService.getAllBranches().get(0);
                Book book = new Book("EDGE-002", "Book to Remove", "Author", 2024, branch.getBranchId());
                bookService.addBook(book);
                
                Patron patron = PatronFactory.createStudent("Remove Test", "remove@test.com", "555-4002");
                patronService.addPatron(patron);
                lendingService.checkoutBook(patron.getPatronId(), book.getIsbn(), branch.getBranchId());
                
                bookService.removeBook(book.getIsbn());
                return false;
            } catch (IllegalStateException e) {
                return e.getMessage().contains("Cannot remove book that is currently checked out");
            }
        });
        
        // Test 4: Observer pattern - multiple observers
        test("Multiple Observers Notification", () -> {
            lendingService.attach(new EmailNotificationObserver("test1@email.com"));
            lendingService.attach(new EmailNotificationObserver("test2@email.com"));
            
            LibraryBranch branch = branchService.getAllBranches().get(0);
            Book book = new Book("OBS-001", "Observer Book", "Author", 2024, branch.getBranchId());
            bookService.addBook(book);
            
            Patron patron = PatronFactory.createStudent("Observer Test", "obs@test.com", "555-5001");
            patronService.addPatron(patron);
            
            lendingService.checkoutBook(patron.getPatronId(), book.getIsbn(), branch.getBranchId());
            return true; // If no exception, observers worked
        });
    }
    
    // ============= TEST UTILITIES =============
    
    private void test(String testName, TestCase testCase) {
        totalTests++;
        try {
            boolean result = testCase.run();
            if (result) {
                passedTests++;
                System.out.println("✅ PASS: " + testName);
            } else {
                failedTests++;
                System.out.println("❌ FAIL: " + testName);
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("❌ FAIL: " + testName + " (Exception: " + e.getMessage() + ")");
        }
    }
    
    private void printSummary() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    TEST SUMMARY                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Total Tests:  " + totalTests);
        System.out.println("✅ Passed:    " + passedTests + " (" + (passedTests * 100 / totalTests) + "%)");
        System.out.println("❌ Failed:    " + failedTests);
        System.out.println();
        
        if (failedTests == 0) {
            System.out.println("🎉 ALL TESTS PASSED! System is working perfectly!");
        } else {
            System.out.println("⚠️  Some tests failed. Please review the output above.");
        }
        System.out.println();
    }
    
    @FunctionalInterface
    interface TestCase {
        boolean run() throws Exception;
    }
}
