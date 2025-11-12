package org.example.book_store_backend.dto;

public class PaymentIntentResponseDTO {
    private String clientSecret;
    private String orderId;
    private double amount;

    public PaymentIntentResponseDTO() {}

    public PaymentIntentResponseDTO(String clientSecret, String orderId, double amount) {
        this.clientSecret = clientSecret;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
