package org.example.book_store_backend.service;

import org.example.book_store_backend.model.Order;
import org.example.book_store_backend.repository.OrderRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService implements OrderServiceI{
    private final OrderRepo orderRepo;
    public OrderService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    public void addOrder(Order order) {
        orderRepo.save(order);
    }

    @Override
    public Order findOrderById(Long orderId) throws Exception {
        var possibe_order = orderRepo.findById(orderId).orElseThrow(() -> new Exception("Book not found"));
        return possibe_order;
    }

    @Override
    public List<Order> findAllOrders() {
        return orderRepo.findAll();
    }
}
