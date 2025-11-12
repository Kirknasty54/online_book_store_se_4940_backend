package org.example.book_store_backend.model;

import java.util.List;

public class BookCollection {
    private final List<Book> books;
    public BookCollection(List<Book> books) {
        this.books = books;
    }

    public Book[] getBooks() {
        return books.toArray(new Book[0]);
    }
}
