package com.carpool.infrastructure.config;

import com.carpool.domain.repository.OfficeRepositoryPort;
import com.carpool.domain.repository.TripRepositoryPort;
import com.carpool.domain.repository.UserRepositoryPort;
import com.carpool.domain.service.OfficeService;
import com.carpool.domain.service.TripService;
import com.carpool.domain.service.UserService;
import com.carpool.domain.service.impl.OfficeServiceImpl;
import com.carpool.domain.service.impl.TripServiceImpl;
import com.carpool.domain.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public TripService tripService(TripRepositoryPort tripRepositoryPort) {
        return new TripServiceImpl(tripRepositoryPort);
    }

    @Bean
    public OfficeService officeService(OfficeRepositoryPort officeRepositoryPort) {
        return new OfficeServiceImpl(officeRepositoryPort);
    }

    @Bean
    public UserService userService(UserRepositoryPort userRepositoryPort) {
        return new UserServiceImpl(userRepositoryPort);
    }
}