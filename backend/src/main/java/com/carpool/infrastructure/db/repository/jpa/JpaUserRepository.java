package com.carpool.infrastructure.db.repository.jpa;

import com.carpool.infrastructure.db.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE UserEntity u SET u.telegramAlias = :tg, u.vkAlias = :vk WHERE u.id = :id")
    void updateSocialAliases(@Param("id") Long id, @Param("tg") String tg, @Param("vk") String vk);
}
