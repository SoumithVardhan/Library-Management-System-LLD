package com.library.strategy;

import com.library.model.Book;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete strategy for searching books by ISBN.
 */
public class ISBNSearchStrategy implements SearchStrategy {
    @Override
    public List<Book> search(List<Book> books, String query) {
        return books.stream()
                .filter(book -> book.getIsbn().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
