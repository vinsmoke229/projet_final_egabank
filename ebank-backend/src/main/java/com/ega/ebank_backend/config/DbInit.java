package com.ega.ebank_backend.config;

import com.ega.ebank_backend.entities.AppUser;
import com.ega.ebank_backend.repositories.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DbInit implements CommandLineRunner {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DbInit(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin@ega.com").isEmpty()) {
            AppUser admin = new AppUser();
            admin.setUsername("admin@ega.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println(">>> ADMIN CRÉÉ : admin@ega.com / admin123");
        }
    }
}