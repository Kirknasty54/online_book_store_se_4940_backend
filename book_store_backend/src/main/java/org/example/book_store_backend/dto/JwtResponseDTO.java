package org.example.book_store_backend.dto;

public class JwtResponseDTO {
    private String token;
    public JwtResponseDTO() {

    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
