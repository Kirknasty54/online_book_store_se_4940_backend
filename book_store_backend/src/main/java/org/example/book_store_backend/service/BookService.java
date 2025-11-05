package org.example.book_store_backend.service;

import org.example.book_store_backend.model.Book;
import org.example.book_store_backend.repository.BookRepo;
import org.springframework.stereotype.Service;

@Service
public class BookService implements BookServiceInterface{
    private final BookRepo bookRepo;

    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Override
    public Book getBookById(Long id) throws Exception{
        var possible_book = bookRepo.findById(id).orElseThrow(() -> new Exception("Book not found"));
        return possible_book;
    }

    @Override
    public boolean existsByTitle(String title) {
        return (bookRepo.existsByTitle(title));
    }

    @Override
    public void deleteBook(Long id){
        if (bookRepo.existsById(id)) {
            bookRepo.deleteById(id);
        }else{
            throw new RuntimeException("Book not found");
        }
    }
}
