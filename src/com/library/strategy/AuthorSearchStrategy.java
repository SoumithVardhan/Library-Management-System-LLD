package com.library.strategy;

import com.library.model.Book;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete strategy for searching books by author.
 */
public class AuthorSearchStrategy implements SearchStrategy {
    @Override
    public List<Book> search(List<Book> books, String query) {
        return books.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}
