package org.example.book_store_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name="books")
public class Book {
    @Id
    private long isbn_id;

    @Column
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private  String published_date;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int stock;

    @Column
    private String image_url_small;

    @Column
    private String image_url_medium;

    @Column(nullable = false)
    private String image_url_large;

    public Book(long isbn_id, String title, String author, String publisher, String published_date, String description, double price, int stock, String image_url_small, String image_url_medium, String image_url_large) {
        this.isbn_id = isbn_id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.published_date = published_date;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.image_url_small = image_url_small;
        this.image_url_medium = image_url_medium;
        this.image_url_large = image_url_large;
    }

    public Book(){}

}
