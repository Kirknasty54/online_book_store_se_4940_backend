package org.example.book_store_backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity @Table(name="users")
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;


    @ManyToOne(fetch = FetchType.EAGER) //eager gets data every time it updates, lazy gets data at the last moment before its needed
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name="account_not_expired")
    private boolean accountNonExpired = true;

    @Column(name="account_non_locked")
    private boolean accountNonLocked = true;

    @Column(name="credentials_non_expired")
    private boolean credentialsNonExpired = true;

    @Column(name="enabled")
    private boolean enabled = true;

    public User(String username, String password, Role role){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(){}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+role.getName())); //collection gives out granted authority, simplegrantedauthority is a class that has a string inside it, which is the role name. this is my role
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
