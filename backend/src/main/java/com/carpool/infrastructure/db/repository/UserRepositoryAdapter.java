package com.carpool.infrastructure.db.repository;

import com.carpool.domain.model.user.User;
import com.carpool.domain.repository.UserRepositoryPort;
import com.carpool.infrastructure.db.entity.UserEntity;
import com.carpool.infrastructure.db.common.Mapper;
import com.carpool.infrastructure.db.repository.jpa.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;
    private final Mapper<UserEntity, User> userMapper;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository, Mapper<UserEntity, User> userMapper) {
        this.jpaUserRepository = jpaUserRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id).map(userMapper::toDomain);
    }
}