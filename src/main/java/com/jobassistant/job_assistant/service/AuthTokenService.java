package com.jobassistant.job_assistant.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Short-lived in-memory bearer tokens for the local portfolio application. */
@Service
public class AuthTokenService {
    private static final long TOKEN_LIFETIME_SECONDS = 8 * 60 * 60;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public String issue(Long userId) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sessions.put(hash(token), new Session(userId,
                Instant.now().plusSeconds(TOKEN_LIFETIME_SECONDS)));
        return token;
    }

    public Long userIdFor(String token) {
        if (token == null || token.isBlank()) return null;
        String key = hash(token);
        Session session = sessions.get(key);
        if (session == null) return null;
        if (session.expiresAt().isBefore(Instant.now())) {
            sessions.remove(key);
            return null;
        }
        return session.userId();
    }

    public void revoke(String token) {
        if (token != null && !token.isBlank()) sessions.remove(hash(token));
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 is unavailable", impossible);
        }
    }

    private record Session(Long userId, Instant expiresAt) { }
}
