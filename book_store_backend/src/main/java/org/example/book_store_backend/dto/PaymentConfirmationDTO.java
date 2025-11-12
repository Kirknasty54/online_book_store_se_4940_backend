package org.example.book_store_backend.dto;

public class PaymentConfirmationDTO {
    private String orderId;
    private String paymentIntentId;
    private String status;

    public PaymentConfirmationDTO() {}

    public PaymentConfirmationDTO(String orderId, String paymentIntentId, String status) {
        this.orderId = orderId;
        this.paymentIntentId = paymentIntentId;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
