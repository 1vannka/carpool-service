package com.carpool.infrastructure.config;

import com.carpool.domain.repository.TripRepositoryPort;
import com.carpool.domain.service.TripService;
import com.carpool.domain.service.impl.TripServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public TripService tripService(TripRepositoryPort tripRepositoryPort) {
        return new TripServiceImpl(tripRepositoryPort);
    }
}