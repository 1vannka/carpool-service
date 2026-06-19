package com.carpool.controller.user;

import com.carpool.controller.common.ResponseMapper;
import com.carpool.controller.dto.user.UserProfileResponse;
import com.carpool.domain.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserWebMapper implements ResponseMapper<User, UserProfileResponse> {

    @Override
    public UserProfileResponse toDto(User domain) {
        if (domain == null) return null;

        return new UserProfileResponse(
                domain.getId(),
                domain.getEmail(),
                domain.getFirstName(),
                domain.getLastName(),
                domain.getTelegramAlias(),
                domain.getVkAlias(),
                domain.getRole()
        );
    }
}