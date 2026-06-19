package com.carpool.infrastructure.db.repository.jpa;

import com.carpool.infrastructure.db.entity.OfficeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaOfficeRepository extends JpaRepository<OfficeEntity, Long> {
    List<OfficeEntity> findByCity(String city);
}