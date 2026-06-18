package com.carpool.controller.auth;

import com.carpool.controller.common.RequestMapper;
import com.carpool.controller.dto.auth.RegisterRequest;
import com.carpool.domain.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class AuthWebMapper implements RequestMapper<RegisterRequest, User> {

    @Override
    public User toDomain(RegisterRequest request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return user;
    }
}