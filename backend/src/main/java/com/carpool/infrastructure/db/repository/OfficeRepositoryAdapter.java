package com.carpool.infrastructure.db.repository;

import com.carpool.domain.model.office.Office;
import com.carpool.domain.repository.OfficeRepositoryPort;
import com.carpool.infrastructure.db.mapper.OfficeDbMapper;
import com.carpool.infrastructure.db.repository.jpa.JpaOfficeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OfficeRepositoryAdapter implements OfficeRepositoryPort {

    private final JpaOfficeRepository jpaOfficeRepository;
    private final OfficeDbMapper officeDbMapper;

    public OfficeRepositoryAdapter(JpaOfficeRepository jpaOfficeRepository, OfficeDbMapper officeDbMapper) {
        this.jpaOfficeRepository = jpaOfficeRepository;
        this.officeDbMapper = officeDbMapper;
    }

    @Override
    public Office save(Office office) {
        var entity = officeDbMapper.toEntity(office);
        var savedEntity = jpaOfficeRepository.save(entity);
        return officeDbMapper.toDomain(savedEntity);
    }

    @Override
    public List<Office> findAll() {
        return jpaOfficeRepository.findAll().stream()
                .map(officeDbMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Office> findById(Long id) {
        return jpaOfficeRepository.findById(id)
                .map(officeDbMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaOfficeRepository.deleteById(id);
    }
}