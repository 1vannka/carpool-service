package com.carpool.infrastructure.db.common;

public interface Mapper<E, D> {
    D toDomain(E entity);
    E toEntity(D domain);
}
