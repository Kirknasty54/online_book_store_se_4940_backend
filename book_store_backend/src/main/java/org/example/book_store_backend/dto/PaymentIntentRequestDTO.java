package org.example.book_store_backend.dto;

import java.util.List;

public class PaymentIntentRequestDTO {
    private Long userId;
    private List<String> bookIsbnIds;
    private String shippingType;

    public PaymentIntentRequestDTO() {}

    public PaymentIntentRequestDTO(Long userId, List<String> bookIsbnIds, String shippingType) {
        this.userId = userId;
        this.bookIsbnIds = bookIsbnIds;
        this.shippingType = shippingType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getBookIsbnIds() {
        return bookIsbnIds;
    }

    public void setBookIsbnIds(List<String> bookIsbnIds) {
        this.bookIsbnIds = bookIsbnIds;
    }

    public String getShippingType() {
        return shippingType;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }
}
