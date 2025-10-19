package com.library.util;

import java.util.UUID;

/**
 * Utility class for generating unique IDs.
 */
public class IdGenerator {
    
    public static String generateBookId() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public static String generatePatronId() {
        return "PT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public static String generateBranchId() {
        return "BR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public static String generateRecordId() {
        return "RC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    public static String generateReservationId() {
        return "RS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
