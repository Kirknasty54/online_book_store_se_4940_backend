package org.example.book_store_backend.service;

import org.example.book_store_backend.model.User;
import org.example.book_store_backend.repository.UserRepo;
import org.example.book_store_backend.repository.RoleRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements IUserService{
    //adding service annotation, registers service into the inversion of control container
    //adding @repository that register the repo into the container, so it can be injected into the service
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;

    public UserDetailsServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
    }

    //determine who the user is, and what their auth is, built into spring security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var possible_user = userRepo.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return possible_user;
    }

    //dont care if user we implemented, just a user details
    @Override
    public User createUser(String username, String password, String role) {
        if(userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists"); //return object responsile, object that wraps around, or controller advise, or just ask dashuan
        }
        var hashedPassword = passwordEncoder.encode(password);
        var foundRole = roleRepo.findByName(role).orElseThrow(()-> new IllegalArgumentException("Role not found"));
        User newUser = new User(username, hashedPassword, foundRole);
        return userRepo.save(newUser);
    }

    @Override
    public void deleteUser(String username) {

    }
}
