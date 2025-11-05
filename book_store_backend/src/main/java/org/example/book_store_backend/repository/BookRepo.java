package org.example.book_store_backend.repository;

import org.example.book_store_backend.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {
    boolean existsByTitle(String title);
    boolean existsById(Long id);
}
