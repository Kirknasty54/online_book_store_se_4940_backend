package org.example.book_store_backend.service;

import org.example.book_store_backend.model.Order;

import java.util.List;

public interface OrderServiceI {
    void addOrder(Order order);
   Order findOrderById(Long orderId) throws Exception;
   List<Order> findAllOrders();
}
