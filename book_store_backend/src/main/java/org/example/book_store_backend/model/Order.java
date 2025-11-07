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

    @Column(nullable = false, name="shipping_type")
    private String shipping_type;

    @Column(nullable=false)
    private double total_price;

    public Order(String order_id, User user, List<Book> books, double total_price) {
        this.order_id = order_id;
        this.user = user;
        this.books = books;
        this.total_price = total_price;
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

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }
}