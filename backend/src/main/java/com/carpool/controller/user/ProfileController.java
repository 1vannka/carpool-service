package com.carpool.controller.user;

import com.carpool.controller.dto.user.UpdateProfileRequest;
import com.carpool.controller.dto.user.UserProfileResponse;
import com.carpool.domain.model.user.User;
import com.carpool.domain.service.UserService;
import com.carpool.infrastructure.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserProfileByEmail(userDetails.getUsername());
        return ResponseEntity.ok(userWebMapper.toDto(user));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userService.getUserProfileByEmail(userDetails.getUsername()).getId();

        User updatedUser = userService.updateSocialAliases(
                userId,
                request.telegramAlias(),
                request.vkAlias()
        );

        return ResponseEntity.ok(userWebMapper.toDto(updatedUser));
    }
}