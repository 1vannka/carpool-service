package com.carpool.controller.user;

import com.carpool.controller.dto.user.UpdateProfileRequest;
import com.carpool.controller.dto.user.UserProfileResponse;
import com.carpool.domain.model.user.User;
import com.carpool.domain.service.UserService;
import com.carpool.infrastructure.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;
    private final UserWebMapper userWebMapper;

    public ProfileController(UserService userService, UserWebMapper userWebMapper) {
        this.userService = userService;
        this.userWebMapper = userWebMapper;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userService.getUserProfile(userDetails.getUser().getId());
        return ResponseEntity.ok(userWebMapper.toDto(user));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User updatedUser = userService.updateSocialAliases(
                userDetails.getUser().getId(),
                request.telegramAlias(),
                request.vkAlias()
        );

        return ResponseEntity.ok(userWebMapper.toDto(updatedUser));
    }
}