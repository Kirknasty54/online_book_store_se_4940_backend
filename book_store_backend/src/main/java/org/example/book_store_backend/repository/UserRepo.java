package org.example.book_store_backend.repository;

import org.example.book_store_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<org.example.book_store_backend.model.User, Long> {
    Optional<User>findByUsername(String username);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
}
