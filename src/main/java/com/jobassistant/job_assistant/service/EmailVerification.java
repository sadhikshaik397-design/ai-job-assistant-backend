package com.jobassistant.job_assistant.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class EmailVerification {

    private static final int CODE_EXPIRY_MINUTES = 10;

    private final ConcurrentMap<String, VerificationData> verificationCodes =
            new ConcurrentHashMap<>();

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateCode(String email) {
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));

        verificationCodes.put(
                email.toLowerCase(),
                new VerificationData(code, LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES))
        );

        return code;
    }

    public boolean verifyCode(String email, String code) {
        String key = email.toLowerCase();
        VerificationData data = verificationCodes.get(key);

        if (data == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(data.expiresAt)) {
            verificationCodes.remove(key);
            return false;
        }

        if (!data.code.equals(code)) {
            return false;
        }

        verificationCodes.remove(key);
        return true;
    }

    private static class VerificationData {
        private final String code;
        private final LocalDateTime expiresAt;

        private VerificationData(String code, LocalDateTime expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }
}