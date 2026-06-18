package com.carpool.controller.auth;

import com.carpool.controller.dto.auth.AuthResponse;
import com.carpool.controller.dto.auth.LoginRequest;
import com.carpool.controller.dto.auth.RegisterRequest;
import com.carpool.domain.model.User;
import com.carpool.domain.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User user = authWebMapper.toDomain(request);
        String token = authService.register(user, request.getPassword());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return ResponseEntity.ok(response);
    }
}
