package com.sip.backend.auth;

import com.sip.backend.config.JwtProperties;
import com.sip.backend.entity.RefreshToken;
import com.sip.backend.entity.User;
import com.sip.backend.repository.RefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    public RefreshToken createForUser(User user) {
        String tokenValue = UUID.randomUUID().toString();
        String tokenHash = hash(tokenValue);

        RefreshToken rt = new RefreshToken();
        rt.id = UUID.randomUUID().toString();
        rt.user = user;
        rt.tokenHash = tokenHash;
        rt.expiresAt = OffsetDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpirySeconds());

        return refreshTokenRepository.save(rt);
    }

    public RefreshToken validateAndConsume(String tokenValue) {
        String tokenHash = hash(tokenValue);
        RefreshToken rt = refreshTokenRepository.findByTokenHash(tokenHash).orElse(null);
        if (rt == null || rt.revokedAt != null || rt.isExpired()) {
            return null;
        }
        rt.revokedAt = OffsetDateTime.now();
        refreshTokenRepository.save(rt);
        return rt;
    }

    @Transactional
    public void revokeAllForUser(String userId) {
        refreshTokenRepository.revokeAllForUser(userId, OffsetDateTime.now());
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}