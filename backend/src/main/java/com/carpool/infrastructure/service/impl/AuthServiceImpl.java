package com.carpool.infrastructure.service.impl;

import com.carpool.domain.model.auth.TokenPair;
import com.carpool.domain.model.user.User;
import com.carpool.domain.repository.UserRepositoryPort;
import com.carpool.domain.service.AuthService;
import com.carpool.infrastructure.security.JwtService;
import com.carpool.infrastructure.security.RedisTokenService;
import com.carpool.infrastructure.security.UserDetailsImpl;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RedisTokenService redisTokenService;

    public AuthServiceImpl(UserRepositoryPort userRepositoryPort,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           RedisTokenService redisTokenService) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.redisTokenService = redisTokenService;
    }

    @Override
    public TokenPair register(User user, String rawPassword) {
        if (!user.getEmail().endsWith("@company.com")) {
            throw new IllegalArgumentException("Регистрация доступна только для сотрудников компании");
        }

        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole("ROLE_USER");

        try {
            User savedUser = userRepositoryPort.save(user);
            return generateTokenPair(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
    }

    @Override
    public TokenPair login(String email, String rawPassword) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword)
        );

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return generateTokenPair(user);
    }

    @Override
    public TokenPair refreshTokens(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);

        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (!redisTokenService.isRefreshTokenValid(user.getId(), refreshToken)) {
            throw new IllegalArgumentException("Невалидный Refresh Token");
        }

        return new TokenPair(generateAccesToken(user), refreshToken);
    }

    @Override
    public void logout(Long userId, String refreshToken) {
        redisTokenService.deleteRefreshToken(userId, refreshToken);
    }

    @Override
    public void logoutAll(Long userId) {
        redisTokenService.deleteAllUserTokens(userId);
    }

    @Override
    public Long getUserIdByEmail(String email) {
        return userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"))
                .getId();
    }

    private TokenPair generateTokenPair(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        String accessToken = jwtService.generateAccessToken(userDetails, user.getId());
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        redisTokenService.saveRefreshToken(
                user.getId(),
                refreshToken,
                jwtService.getRefreshExpiration()
        );

        return new TokenPair(accessToken, refreshToken);
    }

    private String generateAccesToken(User user){
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        return jwtService.generateAccessToken(userDetails, user.getId());
    }
}