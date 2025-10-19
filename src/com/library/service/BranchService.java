package com.library.service;

import com.library.model.LibraryBranch;
import com.library.repository.BranchRepository;
import com.library.util.IdGenerator;
import com.library.util.Logger;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing library branches.
 * Supports multi-branch operations.
 */
public class BranchService {
    private final BranchRepository branchRepository;
    private final Logger logger;
    
    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
        this.logger = Logger.getInstance();
    }
    
    /**
     * Create a new branch
     */
    public LibraryBranch createBranch(String branchName, String address) {
        String branchId = IdGenerator.generateBranchId();
        LibraryBranch branch = new LibraryBranch(branchId, branchName, address);
        branchRepository.save(branch);
        logger.info("Branch created: " + branchName + " (ID: " + branchId + ")");
        return branch;
    }
    
    /**
     * Update branch information
     */
    public void updateBranch(String branchId, String branchName, String address) {
        Optional<LibraryBranch> branchOpt = branchRepository.findById(branchId);
        if (branchOpt.isEmpty()) {
            logger.error("Branch not found: " + branchId);
            throw new IllegalArgumentException("Branch not found");
        }
        
        LibraryBranch branch = branchOpt.get();
        branch.setBranchName(branchName);
        branch.setAddress(address);
        branchRepository.save(branch);
        logger.info("Branch updated: " + branchName + " (ID: " + branchId + ")");
    }
    
    /**
     * Get branch by ID
     */
    public Optional<LibraryBranch> getBranchById(String branchId) {
        return branchRepository.findById(branchId);
    }
    
    /**
     * Get all branches
     */
    public List<LibraryBranch> getAllBranches() {
        return branchRepository.findAll();
    }
    
    /**
     * Delete a branch
     */
    public void deleteBranch(String branchId) {
        Optional<LibraryBranch> branchOpt = branchRepository.findById(branchId);
        if (branchOpt.isEmpty()) {
            logger.error("Branch not found: " + branchId);
            throw new IllegalArgumentException("Branch not found");
        }
        
        LibraryBranch branch = branchOpt.get();
        if (!branch.getInventory().isEmpty()) {
            logger.warn("Cannot delete branch with books in inventory: " + branchId);
            throw new IllegalStateException("Cannot delete branch with books in inventory");
        }
        
        branchRepository.delete(branchId);
        logger.info("Branch deleted: " + branch.getBranchName() + " (ID: " + branchId + ")");
    }
}
