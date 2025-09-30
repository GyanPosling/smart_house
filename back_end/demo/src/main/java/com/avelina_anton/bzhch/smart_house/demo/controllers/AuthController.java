package com.avelina_anton.bzhch.smart_house.demo.controllers;

import com.avelina_anton.bzhch.smart_house.demo.models.User;
import com.avelina_anton.bzhch.smart_house.demo.repositories.UsersRepository;
import com.avelina_anton.bzhch.smart_house.demo.security.JwtUtils;
import com.avelina_anton.bzhch.smart_house.demo.services.CustomUserDetailsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          UsersRepository usersRepository,
                          CustomUserDetailsService customUserDetailsService,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.usersRepository = usersRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            if (usersRepository.existsByName(user.getName())) {
                return ResponseEntity.badRequest().body("Username already taken");
            }
            if (usersRepository.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email already in use");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = usersRepository.save(user);

            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword())
            );

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getName());
            String token = jwtUtils.generateJwtToken(userDetails.getUsername());

            User foundUser = usersRepository.findByName(user.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("jwt", token);
            response.put("userId", foundUser.getId());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }
}