package org.example.book_store_backend.controller;

import org.example.book_store_backend.model.Order;
import org.example.book_store_backend.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping("/orders")
    public List<Order> getOrders(){
        return orderService.findAllOrders();
    }
}
