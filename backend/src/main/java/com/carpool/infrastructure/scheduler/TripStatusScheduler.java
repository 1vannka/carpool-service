package com.carpool.infrastructure.scheduler;

import com.carpool.infrastructure.db.repository.jpa.JpaRideRequestRepository;
import com.carpool.infrastructure.db.repository.jpa.JpaTripPassengerRepository;
import com.carpool.infrastructure.db.repository.jpa.JpaTripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class TripStatusScheduler {

    private static final Logger log = LoggerFactory.getLogger(TripStatusScheduler.class);

    private final JpaTripRepository jpaTripRepository;
    private final JpaTripPassengerRepository jpaTripPassengerRepository;
    private final JpaRideRequestRepository jpaRideRequestRepository;

    public TripStatusScheduler(JpaTripRepository jpaTripRepository,
                               JpaTripPassengerRepository jpaTripPassengerRepository,
                               JpaRideRequestRepository jpaRideRequestRepository) {
        this.jpaTripRepository = jpaTripRepository;
        this.jpaTripPassengerRepository = jpaTripPassengerRepository;
        this.jpaRideRequestRepository = jpaRideRequestRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateStatusesJob() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        int startedTrips = jpaTripRepository.updateStartedTrips(now);
        int completedTrips = jpaTripRepository.updateCompletedTrips(now);
        int rejectedPassengers = jpaTripPassengerRepository.updateExpiredPassengerRequests(now);
        int expiredRequests = jpaRideRequestRepository.updateExpiredRequests(now);

        if (startedTrips > 0 || completedTrips > 0 || rejectedPassengers > 0 || expiredRequests > 0) {
            log.info("Очистка БД: начато поездок - {}, завершено - {}, отклонено пассажиров - {}, протухло заявок - {}",
                    startedTrips, completedTrips, rejectedPassengers, expiredRequests);
        }
    }
}