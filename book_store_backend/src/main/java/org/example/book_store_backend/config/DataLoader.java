package org.example.book_store_backend.config;

import org.example.book_store_backend.model.Role;
import org.example.book_store_backend.repository.RoleRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final RoleRepo roleRepo;

    public DataLoader(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if(roleRepo.count()==0){
            roleRepo.save(new Role(1, "USER"));
            roleRepo.save(new Role(2, "USER"));
        }
    }
}
