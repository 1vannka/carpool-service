package com.carpool.infrastructure.security;

import com.carpool.domain.model.User;
import com.carpool.domain.repository.UserRepositoryPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort userRepositoryPort;

    public CustomUserDetailsService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepositoryPort.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с email " + username + " не найден"));

        return new UserDetailsImpl(user);
    }
}