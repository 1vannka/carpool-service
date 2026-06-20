package com.carpool.infrastructure.scheduler;

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

    public TripStatusScheduler(JpaTripRepository jpaTripRepository, JpaTripPassengerRepository jpaTripPassengerRepository) {
        this.jpaTripRepository = jpaTripRepository;
        this.jpaTripPassengerRepository = jpaTripPassengerRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateStatusesJob() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime yesterday = now.minusHours(24);

        int startedTrips = jpaTripRepository.updateStartedTrips(now);
        int completedTrips = jpaTripRepository.updateCompletedTrips(yesterday);
        int expiredRequests = jpaTripPassengerRepository.updateExpiredPassengerRequests(now);

        if (startedTrips > 0 || completedTrips > 0 || expiredRequests > 0) {
            log.info("Очистка БД: начато поездок - {}, завершено - {}, отклонено заявок - {}",
                    startedTrips, completedTrips, expiredRequests);
        }
    }
}