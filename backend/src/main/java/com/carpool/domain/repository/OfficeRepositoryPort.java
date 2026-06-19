package com.carpool.domain.repository;

import com.carpool.domain.model.office.Office;
import java.util.List;
import java.util.Optional;

public interface OfficeRepositoryPort {
    Office save(Office office);
    List<Office> findAll();
    Optional<Office> findById(Long id);
    void deleteById(Long id);
}