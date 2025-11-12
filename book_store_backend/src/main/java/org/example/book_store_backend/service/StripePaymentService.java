package org.example.book_store_backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.example.book_store_backend.dto.PaymentConfirmationDTO;
import org.example.book_store_backend.dto.PaymentIntentRequestDTO;
import org.example.book_store_backend.dto.PaymentIntentResponseDTO;
import org.example.book_store_backend.model.Book;
import org.example.book_store_backend.model.Order;
import org.example.book_store_backend.model.User;
import org.example.book_store_backend.repository.BookRepo;
import org.example.book_store_backend.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripePaymentService {
    private final BookRepo bookRepo;
    private final UserRepo userRepo;
    private final OrderService orderService;

    public StripePaymentService(BookRepo bookRepo, UserRepo userRepo, OrderService orderService) {
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
        this.orderService = orderService;
    }

    public PaymentIntentResponseDTO createPaymentIntent(PaymentIntentRequestDTO request) throws Exception {
        // Fetch user
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        // Fetch books and calculate total
        List<Book> books = new ArrayList<>();
        double totalPrice = 0.0;

        for (String isbnId : request.getBookIsbnIds()) {
            long isbn = Long.parseLong(isbnId);
            Book book = bookRepo.findById(isbn)
                    .orElseThrow(() -> new Exception("Book not found: " + isbnId));
            books.add(book);
            totalPrice += book.getPrice();
        }

        // Create order in database
        Order order = new Order();
        order.setUser(user);
        order.setBooks(books);
        order.setTotal_price(totalPrice);
        order.setShipping_type(request.getShippingType());
        order.setPaymentStatus("PENDING");

        orderService.addOrder(order);

        // Create Stripe PaymentIntent
        // Amount is in cents, so multiply by 100
        long amountInCents = (long) (totalPrice * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("orderId", order.getOrder_id())
                .putMetadata("userId", user.getId().toString())
                .build();

        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Update order with payment intent ID
            order.setPaymentIntentId(paymentIntent.getId());
            orderService.addOrder(order);

            return new PaymentIntentResponseDTO(
                    paymentIntent.getClientSecret(),
                    order.getOrder_id(),
                    totalPrice
            );
        } catch (StripeException e) {
            throw new Exception("Failed to create payment intent: " + e.getMessage());
        }
    }

    public PaymentConfirmationDTO confirmPayment(String orderId) throws Exception {
        Order order = orderService.findOrderById(Long.parseLong(orderId));

        if (order.getPaymentIntentId() == null) {
            throw new Exception("No payment intent associated with this order");
        }

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(order.getPaymentIntentId());

            String status;
            if ("succeeded".equals(paymentIntent.getStatus())) {
                order.setPaymentStatus("COMPLETED");
                status = "COMPLETED";
            } else if ("requires_payment_method".equals(paymentIntent.getStatus()) ||
                       "requires_confirmation".equals(paymentIntent.getStatus()) ||
                       "requires_action".equals(paymentIntent.getStatus())) {
                order.setPaymentStatus("PENDING");
                status = "PENDING";
            } else {
                order.setPaymentStatus("FAILED");
                status = "FAILED";
            }

            orderService.addOrder(order);

            return new PaymentConfirmationDTO(
                    order.getOrder_id(),
                    paymentIntent.getId(),
                    status
            );
        } catch (StripeException e) {
            throw new Exception("Failed to retrieve payment intent: " + e.getMessage());
        }
    }

    public Map<String, Object> getPaymentStatus(String orderId) throws Exception {
        Order order = orderService.findOrderById(Long.parseLong(orderId));

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.getOrder_id());
        response.put("paymentStatus", order.getPaymentStatus());
        response.put("totalPrice", order.getTotal_price());

        if (order.getPaymentIntentId() != null) {
            response.put("paymentIntentId", order.getPaymentIntentId());
        }

        return response;
    }

    public String createCheckoutSession(Map<String, Object> request) throws StripeException{
        List<Map<String, Object>> cart = (List<Map<String, Object>>) request.get("cart");

        //build line items from cart
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for(Map<String, Object> item : cart){
            lineItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("usd")
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName((String) item.get("title"))
                                                            .build()
                                            )
                                            .setUnitAmount(((Number) item.get("price")).longValue()*100)
                                            .build()
                            )
                            .setQuantity(((Number) item.get("quantity")).longValue())
                            .build()
            );
        }
        SessionCreateParams params = SessionCreateParams.builder()
                .setUiMode(SessionCreateParams.UiMode.EMBEDDED)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addAllLineItem(lineItems)
                .setReturnUrl("http://localhost:5173/return?session_id={CHECKOUT_SESSION_ID}")
                .build();

        Session session = Session.create(params);
        return session.getClientSecret();
    }

    public Map<String, Object> getSessionDetails(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);

        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("sessionId", session.getId());
        sessionDetails.put("paymentStatus", session.getPaymentStatus());
        sessionDetails.put("status", session.getStatus());
        sessionDetails.put("amountTotal", session.getAmountTotal());
        sessionDetails.put("currency", session.getCurrency());
        sessionDetails.put("customerEmail", session.getCustomerDetails() != null ?
                session.getCustomerDetails().getEmail() : null);

        // Get payment method details from the session
        if (session.getPaymentIntent() != null) {
            try {
                PaymentIntent paymentIntent = PaymentIntent.retrieve(session.getPaymentIntent());

                // Get payment method details
                if (paymentIntent.getPaymentMethod() != null) {
                    com.stripe.model.PaymentMethod paymentMethod =
                        com.stripe.model.PaymentMethod.retrieve(paymentIntent.getPaymentMethod());

                    if (paymentMethod.getCard() != null) {
                        sessionDetails.put("cardLast4", paymentMethod.getCard().getLast4());
                        sessionDetails.put("cardBrand", paymentMethod.getCard().getBrand());
                    }
                }
            } catch (Exception e) {
                // If we can't get payment method details, continue without them
                System.err.println("Could not retrieve payment method details: " + e.getMessage());
            }
        }

        return sessionDetails;
    }
}
