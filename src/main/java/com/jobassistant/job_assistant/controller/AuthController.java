package com.jobassistant.job_assistant.controller;

import com.jobassistant.job_assistant.entity.User;
import com.jobassistant.job_assistant.repository.UserRepository;
import com.jobassistant.job_assistant.service.EmailService;
import com.jobassistant.job_assistant.service.EmailVerification;
import com.jobassistant.job_assistant.service.AuthTokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final EmailVerification emailVerification;
    private final AuthTokenService tokenService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthController(
            UserRepository userRepository,
            EmailService emailService,
            EmailVerification emailVerification,
            AuthTokenService tokenService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.emailVerification = emailVerification;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {

        if (user.getUsername() == null || user.getUsername().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Username, email, and password are required");
        }

        String username = user.getUsername().trim();
        String email = user.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest()
                    .body("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false);

        // Send the verification email before saving the account.
        String code = emailVerification.generateCode(email);

        try {
            emailService.sendVerificationCode(email, code);
        } catch (Exception e) {
            System.err.println("Failed to send verification email:");
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not send verification email. Check the backend terminal for details.");
        }

        userRepository.save(user);

        return ResponseEntity.ok(
                "Verification code sent to your email. Verify your email before logging in."
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String code = request.get("code");

        if (email == null || code == null
                || email.isBlank() || code.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Email and verification code are required");
        }

        email = email.trim().toLowerCase();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        if (user.isEmailVerified()) {
            return ResponseEntity.ok("Email is already verified");
        }

        if (!emailVerification.verifyCode(email, code.trim())) {
            return ResponseEntity.badRequest()
                    .body("Invalid or expired verification code");
        }

        user.setEmailVerified(true);
        userRepository.save(user);

        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        if (user.getUsername() == null || user.getPassword() == null
                || user.getUsername().isBlank()
                || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Username and password are required");
        }

        User existingUser = userRepository
                .findByUsername(user.getUsername().trim())
                .orElse(null);

        if (existingUser == null
                || !passwordEncoder.matches(
                        user.getPassword(),
                        existingUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        if (!existingUser.isEmailVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Please verify your email before logging in");
        }

        String token = tokenService.issue(existingUser.getId());
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "token", token,
                "userId", existingUser.getId(),
                "username", existingUser.getUsername()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader(value = "Authorization", required = false)
            String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            tokenService.revoke(authorization.substring(7).trim());
        }
        return ResponseEntity.ok("Logged out");
    }
}
