package org.example.book_store_backend.controller;

import org.example.book_store_backend.dto.PaymentConfirmationDTO;
import org.example.book_store_backend.dto.PaymentIntentRequestDTO;
import org.example.book_store_backend.dto.PaymentIntentResponseDTO;
import org.example.book_store_backend.service.StripePaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    private final StripePaymentService stripePaymentService;

    public PaymentController(StripePaymentService stripePaymentService) {
        this.stripePaymentService = stripePaymentService;
    }

    /**
     * Create a payment intent for an order
     * POST /api/payments/create-intent
     * Body: { "userId": 1, "bookIsbnIds": ["123", "456"], "shippingType": "STANDARD" }
     */
    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentIntentRequestDTO request) {
        try {
            PaymentIntentResponseDTO response = stripePaymentService.createPaymentIntent(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Confirm payment status for an order
     * GET /api/payments/confirm/{orderId}
     */
    @GetMapping("/confirm/{orderId}")
    public ResponseEntity<?> confirmPayment(@PathVariable String orderId) {
        try {
            PaymentConfirmationDTO response = stripePaymentService.confirmPayment(orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get payment status for an order
     * GET /api/payments/status/{orderId}
     */
    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String orderId) {
        try {
            Map<String, Object> response = stripePaymentService.getPaymentStatus(orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Create checkout session for Stripe Embedded Checkout
     * POST /api/payments/create-checkout-session
     * Body: { "cart": [...], "amount": 5000 }
     */
    @PostMapping("/create-checkout-session")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> createCheckoutSession(@RequestBody Map<String, Object> request) {
        try{
            String clientSecret = stripePaymentService.createCheckoutSession(request);
            return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get session details including payment information
     * GET /api/payments/session/{sessionId}
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getSessionDetails(@PathVariable String sessionId) {
        try {
            Map<String, Object> sessionDetails = stripePaymentService.getSessionDetails(sessionId);
            return ResponseEntity.ok(sessionDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
