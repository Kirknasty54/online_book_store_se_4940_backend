package org.example.book_store_backend.repository;

import org.example.book_store_backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order,  Long>{
    Optional<Order> findById(Long userId);
    boolean existsById(Long userId);
}
