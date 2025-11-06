package org.example.book_store_backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String order_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "order_books",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "isbn_id")
    )
    private List<Book> books = new ArrayList<>();

    public Order(String order_id, User user, List<Book> books) {
        this.order_id = order_id;
        this.user = user;
        this.books = books;
    }

    public Order(){}
    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

}