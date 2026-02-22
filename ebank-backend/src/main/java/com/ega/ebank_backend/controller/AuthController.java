package com.ega.ebank_backend.controller;

import com.ega.ebank_backend.dto.AuthRequest;
import com.ega.ebank_backend.entities.AppUser;
import com.ega.ebank_backend.repositories.AppUserRepository;
import com.ega.ebank_backend.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")

public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final AppUserRepository userRepository;

    public AuthController(AuthenticationManager authManager, JwtUtils jwtUtils, AppUserRepository userRepository) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody AuthRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // récupère le rôle de l'utilisateur
        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtUtils.generateToken(user.getUsername(), user.getRole());
    }
}