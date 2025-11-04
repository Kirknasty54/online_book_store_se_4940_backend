package org.example.book_store_backend.dto;

import org.example.book_store_backend.model.User;
import org.springframework.security.core.userdetails.UserDetails;

//use dto to hide password, from something like f12 tools browser or just making request
public class UserDTO {
    private String username;
    private Long id;

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.id = user.getId();
    }
}
