package com.carpool.infrastructure.config;

import com.carpool.domain.repository.*;
import com.carpool.domain.service.*;
import com.carpool.domain.service.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public TripService tripService(TripRepositoryPort tripRepositoryPort, RideRequestRepositoryPort rideRequestRepositoryPort) {
        return new TripServiceImpl(tripRepositoryPort, rideRequestRepositoryPort);
    }

    @Bean
    public OfficeService officeService(OfficeRepositoryPort officeRepositoryPort) {
        return new OfficeServiceImpl(officeRepositoryPort);
    }

    @Bean
    public UserService userService(UserRepositoryPort userRepositoryPort) {
        return new UserServiceImpl(userRepositoryPort);
    }

    @Bean
    public RideRequestService rideRequestService(RideRequestRepositoryPort rideRequestRepositoryPort, TripRepositoryPort tripRepositoryPort) {
        return new RideRequestServiceImpl(rideRequestRepositoryPort, tripRepositoryPort);
    }

    @Bean
    public TripPassengerService tripPassengerService(
            TripPassengerRepositoryPort tripPassengerRepositoryPort,
            TripRepositoryPort tripRepositoryPort,
            RideRequestRepositoryPort rideRequestRepositoryPort) {
        return new TripPassengerServiceImpl(tripPassengerRepositoryPort, tripRepositoryPort, rideRequestRepositoryPort);
    }
}