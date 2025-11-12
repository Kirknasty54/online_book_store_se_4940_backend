package org.example.book_store_backend.controller;

import org.example.book_store_backend.model.Book;
import org.example.book_store_backend.model.Order;
import org.example.book_store_backend.model.User;
import org.example.book_store_backend.repository.BookRepo;
import org.example.book_store_backend.repository.UserRepo;
import org.example.book_store_backend.service.OrderService;
import org.example.book_store_backend.service.StripePaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    private final OrderService orderService;
    private final BookRepo bookRepo;
    private final UserRepo userRepo;
    private final StripePaymentService stripePaymentService;

    public OrderController(OrderService orderService, BookRepo bookRepo, UserRepo userRepo,
                          StripePaymentService stripePaymentService) {
        this.orderService = orderService;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
        this.stripePaymentService = stripePaymentService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getOrders(){
        return ResponseEntity.ok(orderService.findAllOrders());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        try {
            String sessionId = (String) request.get("sessionId");
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");

            // Get authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new Exception("User not found"));

            // Get session details from Stripe
            Map<String, Object> sessionDetails = stripePaymentService.getSessionDetails(sessionId);

            // Create order
            Order order = new Order();
            order.setUser(user);
            order.setPaymentIntentId(sessionId);
            order.setPaymentStatus("COMPLETED");
            order.setShipping_type("STANDARD");

            // Process books from items
            List<Book> books = new ArrayList<>();
            double totalPrice = 0.0;

            for (Map<String, Object> item : items) {
                Object isbnObj = item.get("isbn");
                long isbn = (isbnObj instanceof Integer) ?
                    ((Integer) isbnObj).longValue() :
                    Long.parseLong(isbnObj.toString());

                Book book = bookRepo.findById(isbn)
                        .orElseThrow(() -> new Exception("Book not found: " + isbn));

                int quantity = ((Number) item.get("quantity")).intValue();
                for (int i = 0; i < quantity; i++) {
                    books.add(book);
                }
                totalPrice += book.getPrice() * quantity;
            }

            order.setBooks(books);
            order.setTotal_price(totalPrice);

            orderService.addOrder(order);

            // Prepare response with order details and payment info
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getOrder_id());
            response.put("sessionId", sessionId);
            response.put("items", items);
            response.put("totalAmount", totalPrice);
            response.put("orderDate", java.time.LocalDateTime.now().toString());
            response.put("status", "completed");
            response.put("paymentDetails", sessionDetails);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        try {
            Order order = orderService.findOrderById(Long.parseLong(orderId));

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getOrder_id());
            response.put("totalAmount", order.getTotal_price());
            response.put("paymentStatus", order.getPaymentStatus());
            response.put("shippingType", order.getShipping_type());

            List<Map<String, Object>> bookDetails = new ArrayList<>();
            for (Book book : order.getBooks().getBooks()) {
                Map<String, Object> bookInfo = new HashMap<>();
                bookInfo.put("isbn", book.getIsbn_id());
                bookInfo.put("title", book.getTitle());
                bookInfo.put("price", book.getPrice());
                bookDetails.add(bookInfo);
            }
            response.put("books", bookDetails);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
