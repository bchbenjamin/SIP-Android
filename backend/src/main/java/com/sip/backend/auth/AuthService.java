package com.sip.backend.auth;

import com.sip.backend.dto.*;
import com.sip.backend.entity.User;
import com.sip.backend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authManager, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username, request.password));
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        String accessToken = jwtService.generateAccessToken(user);
        var refreshToken = refreshTokenService.createForUser(user);

        return new LoginResponse(
                accessToken,
                refreshToken.id,
                jwtService.getAccessTokenExpirySeconds(),
                user.id,
                user.username,
                user.role.name()
        );
    }

    public LoginResponse refresh(RefreshRequest request) {
        var consumed = refreshTokenService.validateAndConsume(request.refreshToken);
        if (consumed == null) {
            throw new BadCredentialsException("Refresh token is invalid or expired");
        }

        User user = consumed.user;
        String accessToken = jwtService.generateAccessToken(user);
        var newRefreshToken = refreshTokenService.createForUser(user);

        return new LoginResponse(
                accessToken,
                newRefreshToken.id,
                jwtService.getAccessTokenExpirySeconds(),
                user.id,
                user.username,
                user.role.name()
        );
    }

    @Transactional
    public void logout(String userId) {
        refreshTokenService.revokeAllForUser(userId);
    }
}