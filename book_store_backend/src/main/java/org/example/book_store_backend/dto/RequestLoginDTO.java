package org.example.book_store_backend.dto;

public class RequestLoginDTO {
    private String username;
    private String password;
    public RequestLoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public RequestLoginDTO() {

    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


}
