package com.carpool.infrastructure.db.repository.jpa;

import com.carpool.infrastructure.db.entity.OfficeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeJpaRepository extends JpaRepository<OfficeEntity, Long> {
}