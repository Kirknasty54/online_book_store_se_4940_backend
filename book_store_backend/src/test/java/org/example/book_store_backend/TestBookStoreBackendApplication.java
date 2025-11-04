package org.example.book_store_backend;

import org.springframework.boot.SpringApplication;

public class TestBookStoreBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(BookStoreBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
