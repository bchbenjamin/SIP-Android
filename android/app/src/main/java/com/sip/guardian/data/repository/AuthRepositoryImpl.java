package com.sip.guardian.data.repository;

import com.sip.guardian.data.local.SecureTokenStore;
import com.sip.guardian.data.remote.api.AuthApiService;
import com.sip.guardian.data.remote.dto.LoginRequest;
import com.sip.guardian.data.remote.dto.LoginResponse;
import com.sip.guardian.domain.model.User;
import com.sip.guardian.domain.repository.AuthRepository;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepositoryImpl implements AuthRepository {

    private final AuthApiService authApi;
    private final SecureTokenStore tokenStore;

    @Inject
    public AuthRepositoryImpl(AuthApiService authApi, SecureTokenStore tokenStore) {
        this.authApi = authApi;
        this.tokenStore = tokenStore;
    }

    @Override
    public User login(String username, String password) {
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
