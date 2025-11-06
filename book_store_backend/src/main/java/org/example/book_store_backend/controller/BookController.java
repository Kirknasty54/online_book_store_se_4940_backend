package org.example.book_store_backend.controller;

import org.example.book_store_backend.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books/")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("allbooks")
    public ResponseEntity<?> getAllBooks(){
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/book")
    public ResponseEntity<?> getBook(){
        return ResponseEntity.ok("Book details");
    }

    @GetMapping("/{isbn_id}")
    public ResponseEntity<?> getBookById(@PathVariable long isbn_id) throws Exception {
        //gets book details by isbn id
        var book = bookService.getBookById(isbn_id);
        return ResponseEntity.ok(book);
    }
}
