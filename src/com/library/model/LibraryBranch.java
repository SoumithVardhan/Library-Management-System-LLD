package com.library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Library Branch in a multi-branch system.
 */
public class LibraryBranch {
    private final String branchId;
    private String branchName;
    private String address;
    private List<String> inventory;
    
    public LibraryBranch(String branchId, String branchName, String address) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.address = address;
        this.inventory = new ArrayList<>();
    }
    
    public String getBranchId() {
        return branchId;
    }
    
    public String getBranchName() {
        return branchName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public List<String> getInventory() {
        return new ArrayList<>(inventory);
    }
    
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public void addBookToInventory(String isbn) {
        if (!inventory.contains(isbn)) {
            inventory.add(isbn);
        }
    }
    
    public void removeBookFromInventory(String isbn) {
        inventory.remove(isbn);
    }
    
    public boolean hasBook(String isbn) {
        return inventory.contains(isbn);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryBranch that = (LibraryBranch) o;
        return Objects.equals(branchId, that.branchId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(branchId);
    }
    
    @Override
    public String toString() {
        return "LibraryBranch{" +
                "branchId='" + branchId + '\'' +
                ", branchName='" + branchName + '\'' +
                ", address='" + address + '\'' +
                ", inventory size=" + inventory.size() +
                '}';
    }
}
