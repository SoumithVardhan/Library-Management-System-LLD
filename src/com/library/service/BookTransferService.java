package com.library.service;

import com.library.model.Book;
import com.library.model.BookStatus;
import com.library.model.LibraryBranch;
import com.library.repository.BookRepository;
import com.library.repository.BranchRepository;
import com.library.util.Logger;

import java.util.Optional;

/**
 * Service class for transferring books between library branches.
 */
public class BookTransferService {
    private final BookRepository bookRepository;
    private final BranchRepository branchRepository;
    private final Logger logger;
    
    public BookTransferService(BookRepository bookRepository, BranchRepository branchRepository) {
        this.bookRepository = bookRepository;
        this.branchRepository = branchRepository;
        this.logger = Logger.getInstance();
    }
    
    /**
     * Transfer a book from one branch to another
     */
    public void transferBook(String isbn, String fromBranchId, String toBranchId) {
        // Validate source branch
        Optional<LibraryBranch> fromBranchOpt = branchRepository.findById(fromBranchId);
        if (fromBranchOpt.isEmpty()) {
            logger.error("Source branch not found: " + fromBranchId);
            throw new IllegalArgumentException("Source branch not found");
        }
        
        LibraryBranch fromBranch = fromBranchOpt.get();
        
        // Validate destination branch
        Optional<LibraryBranch> toBranchOpt = branchRepository.findById(toBranchId);
        if (toBranchOpt.isEmpty()) {
            logger.error("Destination branch not found: " + toBranchId);
            throw new IllegalArgumentException("Destination branch not found");
        }
        
        LibraryBranch toBranch = toBranchOpt.get();
        
        // Validate book
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        if (bookOpt.isEmpty()) {
            logger.error("Book not found: " + isbn);
            throw new IllegalArgumentException("Book not found");
        }
        
        Book book = bookOpt.get();
        
        // Check if book is at source branch
        if (!book.getCurrentBranchId().equals(fromBranchId)) {
            logger.error("Book is not at source branch. Current branch: " + book.getCurrentBranchId());
            throw new IllegalStateException("Book is not at the source branch");
        }
        
        // Check if book is available for transfer
        if (book.getStatus() != BookStatus.AVAILABLE) {
            logger.warn("Book is not available for transfer: " + isbn + " (Status: " + book.getStatus() + ")");
            throw new IllegalStateException("Book must be available (not checked out or reserved) for transfer");
        }
        
        // Perform transfer
        fromBranch.removeBookFromInventory(isbn);
        toBranch.addBookToInventory(isbn);
        book.setCurrentBranchId(toBranchId);
        
        // Save changes
        branchRepository.save(fromBranch);
        branchRepository.save(toBranch);
        bookRepository.save(book);
        
        logger.info("Book transferred: " + book.getTitle() + 
                   " from " + fromBranch.getBranchName() + 
                   " to " + toBranch.getBranchName());
    }
    
    /**
     * Check if a book can be transferred
     */
    public boolean canTransferBook(String isbn, String fromBranchId) {
        Optional<Book> bookOpt = bookRepository.findByIsbn(isbn);
        if (bookOpt.isEmpty()) {
            return false;
        }
        
        Book book = bookOpt.get();
        return book.getCurrentBranchId().equals(fromBranchId) && 
               book.getStatus() == BookStatus.AVAILABLE;
    }
}
