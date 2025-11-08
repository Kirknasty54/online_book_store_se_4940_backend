package org.example.book_store_backend.controller;

import org.example.book_store_backend.model.Book;
import org.example.book_store_backend.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/api/books/")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/allbooks")
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

    @DeleteMapping("/{isbn_id}")
    public ResponseEntity<?> deleteBook(@PathVariable long isbn_id){
        bookService.deleteBook(isbn_id);
        return ResponseEntity.ok("Book deleted");
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBook(@RequestBody Book book){
        bookService.addBook(book);
        return ResponseEntity.ok("Book created");
    }
}
