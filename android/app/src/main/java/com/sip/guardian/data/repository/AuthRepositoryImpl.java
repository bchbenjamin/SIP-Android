package com.sip.guardian.data.repository;

import com.sip.guardian.BuildConfig;
import com.sip.guardian.data.local.SecureTokenStore;
import com.sip.guardian.data.remote.api.AuthApiService;
import com.sip.guardian.data.remote.dto.LoginRequest;
import com.sip.guardian.data.remote.dto.LoginResponse;
import com.sip.guardian.domain.model.User;
import com.sip.guardian.domain.repository.AuthRepository;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepositoryImpl implements AuthRepository {

    // Mock credentials active in DEBUG builds only (see README).
    private static final Map<String, String> MOCK_USERS = Map.of(
            "admin",    "admin123",
            "operator", "op1234"
    );

    private final AuthApiService authApi;
    private final SecureTokenStore tokenStore;

    @Inject
    public AuthRepositoryImpl(AuthApiService authApi, SecureTokenStore tokenStore) {
        this.authApi = authApi;
        this.tokenStore = tokenStore;
    }

    @Override
    public User login(String username, String password) {
        // Debug mock: bypass network when running a DEBUG build.
        if (BuildConfig.DEBUG && MOCK_USERS.containsKey(username)) {
            String stored = MOCK_USERS.get(username);
            if (!stored.equals(password)) {
                throw new SecurityException("Invalid credentials");
            }
            return mockLogin(username);
        }

        try {
            var response = authApi.login(new LoginRequest(username, password)).execute();
            if (!response.isSuccessful() || response.body() == null) {
                throw new SecurityException("Invalid credentials (HTTP " + response.code() + ")");
            }
            persist(response.body());
            return currentUser();
        } catch (IOException e) {
            throw new IllegalStateException("Login failed: network error", e);
        }
    }

    /** Sets up the token store with a deterministic mock session. */
    private User mockLogin(String username) {
        boolean isAdmin = "admin".equals(username);
        String userId  = isAdmin ? "mock-admin-id"  : "mock-operator-id";
        String role    = isAdmin ? "ADMIN"           : "OPERATOR";
        // Tokens valid for 24 hours from now.
        long expiresIn = 24 * 3600;
        tokenStore.save("mock-access-token", "mock-refresh-token", expiresIn);
        tokenStore.saveUser(userId, username, role);
        return new User(userId, username,
                "ADMIN".equals(role) ? User.Role.ADMIN : User.Role.OPERATOR);
    }

    @Override
    public User refreshSession() {
        String refresh = tokenStore.getRefreshToken();
        if (refresh == null) throw new SecurityException("No refresh token");
        try {
            var response = authApi.refresh(
                    java.util.Map.of("refreshToken", refresh)).execute();
            if (!response.isSuccessful() || response.body() == null) {
                tokenStore.clear();
                throw new SecurityException("Session expired");
            }
            persist(response.body());
            return currentUser();
        } catch (IOException e) {
            throw new IllegalStateException("Refresh failed: network error", e);
        }
    }

    @Override
    public void logout() {
        tokenStore.clear();
    }

    @Override
    public User currentUser() {
        String id = tokenStore.getUserId();
        if (id == null) return null;
        String role = tokenStore.getRole();
        return new User(id, tokenStore.getUsername(),
                "ADMIN".equals(role) ? User.Role.ADMIN : User.Role.OPERATOR);
    }

    private void persist(LoginResponse tokens) {
        tokenStore.save(tokens.token, tokens.refreshToken, tokens.expiresIn);
        tokenStore.saveUser(tokens.userId, tokens.username, tokens.role);
    }
}
