package com.library.factory;

import com.library.model.Patron;
import com.library.model.PatronType;
import com.library.util.IdGenerator;

/**
 * Factory class for creating Patron objects (Factory Pattern).
 */
public class PatronFactory {
    
    public static Patron createPatron(String name, String email, String phone, PatronType patronType) {
        String patronId = IdGenerator.generatePatronId();
        return new Patron(patronId, name, email, phone, patronType);
    }
    
    public static Patron createStudent(String name, String email, String phone) {
        return createPatron(name, email, phone, PatronType.STUDENT);
    }
    
    public static Patron createFaculty(String name, String email, String phone) {
        return createPatron(name, email, phone, PatronType.FACULTY);
    }
    
    public static Patron createGeneralMember(String name, String email, String phone) {
        return createPatron(name, email, phone, PatronType.GENERAL);
    }
}
