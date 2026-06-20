package com.carpool.controller.auth;

import com.carpool.controller.dto.auth.AuthResponse;
import com.carpool.controller.dto.auth.LoginRequest;
import com.carpool.controller.dto.auth.RegisterRequest;
import com.carpool.controller.dto.auth.RefreshRequest;
import com.carpool.domain.model.auth.TokenPair;
import com.carpool.domain.model.user.User;
import com.carpool.domain.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthWebMapper authWebMapper;

    public AuthController(AuthService authService, AuthWebMapper authWebMapper) {
        this.authService = authService;
        this.authWebMapper = authWebMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authWebMapper.toDomain(request);
        TokenPair tokenPair = authService.register(user, request.password());

        return ResponseEntity.ok(new AuthResponse(tokenPair.accessToken(), tokenPair.refreshToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenPair tokenPair = authService.login(request.email(), request.password());

        return ResponseEntity.ok(new AuthResponse(tokenPair.accessToken(), tokenPair.refreshToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenPair tokenPair = authService.refreshTokens(request.refreshToken());

        return ResponseEntity.ok(new AuthResponse(tokenPair.accessToken(), tokenPair.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RefreshRequest request) {

        Long userId = authService.getUserIdByEmail(userDetails.getUsername());
        authService.logout(userId, request.refreshToken());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal UserDetails userDetails) {

        Long userId = authService.getUserIdByEmail(userDetails.getUsername());
        authService.logoutAll(userId);

        return ResponseEntity.noContent().build();
    }
}