package com.library.strategy;

import com.library.model.Book;
import java.util.List;

/**
 * Strategy interface for book search (Strategy Pattern).
 */
public interface SearchStrategy {
    List<Book> search(List<Book> books, String query);
}
