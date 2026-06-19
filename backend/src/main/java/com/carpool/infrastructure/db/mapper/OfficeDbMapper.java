package com.carpool.infrastructure.db.mapper;

import com.carpool.domain.model.office.Office;
import com.carpool.infrastructure.db.common.Mapper;
import com.carpool.infrastructure.db.entity.OfficeEntity;
import org.springframework.stereotype.Component;

@Component
public class OfficeDbMapper implements Mapper<OfficeEntity, Office> {

    @Override
    public Office toDomain(OfficeEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Office(
                entity.getId(),
                entity.getName(),
                entity.getCity(),
                entity.getAddress(),
                entity.getLocation()
        );
    }

    @Override
    public OfficeEntity toEntity(Office domain) {
        if (domain == null) {
            return null;
        }
        OfficeEntity entity = new OfficeEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCity(domain.getCity());
        entity.setAddress(domain.getAddress());
        entity.setLocation(domain.getLocation());
        return entity;
    }
}