package com.carpool.infrastructure.service;

import com.carpool.domain.model.User;
import com.carpool.domain.repository.UserRepositoryPort;
import com.carpool.domain.service.AuthService;
import com.carpool.infrastructure.security.JwtService;
import com.carpool.infrastructure.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepositoryPort userRepositoryPort,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public String register(User user, String rawPassword) {
        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole("ROLE_USER");

        User savedUser = userRepositoryPort.save(user);

        UserDetailsImpl userDetails = new UserDetailsImpl(savedUser);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", savedUser.getRole());
        return jwtService.generateToken(extraClaims, userDetails);
    }

    @Override
    public String login(String email, String rawPassword) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword)
        );

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole());
        return jwtService.generateToken(extraClaims, userDetails);
    }
}