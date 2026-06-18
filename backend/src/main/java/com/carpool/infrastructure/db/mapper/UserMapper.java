package com.carpool.infrastructure.db.mapper;

import com.carpool.domain.model.User;
import com.carpool.infrastructure.db.common.Mapper;
import com.carpool.infrastructure.db.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<UserEntity, User> {
    @Override
    public User toDomain(UserEntity entity) {
        if (entity == null){
            return null;
        }

        User user = new User();
        user.setEmail(entity.getEmail());
        user.setPasswordHash(entity.getPasswordHash());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setTelegramAlias(entity.getTelegramAlias());
        user.setVkAlias(entity.getVkAlias());
        user.setRole(entity.getRole());
        return user;
    }

    @Override
    public UserEntity toEntity(User domain) {
        if (domain == null){
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setTelegramAlias(domain.getTelegramAlias());
        entity.setVkAlias(domain.getVkAlias());
        entity.setRole(domain.getRole());
        return entity;
    }
}
