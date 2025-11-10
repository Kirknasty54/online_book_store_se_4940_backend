package org.example.book_store_backend.controller;

import org.example.book_store_backend.dto.RequestLoginDTO;
import org.example.book_store_backend.dto.UserDTO;
import org.example.book_store_backend.service.IUserService;
import org.example.book_store_backend.service.JwtService;
import org.example.book_store_backend.service.UserDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/users/")
public class AuthController {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final IUserService userDetailsService;

    public AuthController(JwtService jwtService, AuthenticationManager authenticationManager, IUserService userDetailsService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/auth")
    @CrossOrigin
    public ResponseEntity<?> authenticateUser(@RequestBody RequestLoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            var user = userDetailsService.loadUserByUsername(loginDTO.getUsername());
            var token = jwtService.generateToken(user);
            return ResponseEntity.ok(token);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RequestLoginDTO loginDTO){
        try{
            var user = userDetailsService.createUser(loginDTO.getUsername(), loginDTO.getPassword(), "USER");
            return ResponseEntity.ok(new UserDTO(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
        try{
            userDetailsService.deleteUser(username);
            return ResponseEntity.ok("User deleted");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

