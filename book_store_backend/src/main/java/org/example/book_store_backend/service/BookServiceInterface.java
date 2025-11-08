package org.example.book_store_backend.service;

import org.example.book_store_backend.model.Book;

import java.util.List;

public interface BookServiceInterface {
    Book getBookById(Long id) throws Exception;
    boolean existsByTitle(String title);
    void deleteBook(Long id);
    void addBook(Book book);

    List<Book> getAllBooks();
}
