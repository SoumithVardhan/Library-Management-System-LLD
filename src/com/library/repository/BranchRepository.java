package com.library.repository;

import com.library.model.LibraryBranch;
import java.util.*;

/**
 * Repository for managing LibraryBranch data.
 */
public class BranchRepository {
    private final Map<String, LibraryBranch> branches;
    
    public BranchRepository() {
        this.branches = new HashMap<>();
    }
    
    public void save(LibraryBranch branch) {
        branches.put(branch.getBranchId(), branch);
    }
    
    public Optional<LibraryBranch> findById(String branchId) {
        return Optional.ofNullable(branches.get(branchId));
    }
    
    public List<LibraryBranch> findAll() {
        return new ArrayList<>(branches.values());
    }
    
    public boolean delete(String branchId) {
        return branches.remove(branchId) != null;
    }
    
    public boolean exists(String branchId) {
        return branches.containsKey(branchId);
    }
}
