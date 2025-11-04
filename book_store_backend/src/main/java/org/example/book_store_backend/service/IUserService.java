package org.example.book_store_backend.service;

import org.example.book_store_backend.model.Role;
import org.example.book_store_backend.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

//couple user details service with out IUserService, rather of
public interface IUserService extends UserDetailsService {
    User createUser(String username, String password, String role);
    void deleteUser(String username);
}
