package com.kmtp.reservation.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface ReservationRepository extends ReactiveCrudRepository<ReservationEntity, Long> {

    Mono<ReservationEntity> findByMasterIdAndScheduleIdAndItemIdAndStartDateBeforeAndEndDateAfter(
            Long masterId
            , Long scheduleId
            , Long itemId
            , LocalDate endDate
            , LocalDate startDate);

    Flux<ReservationEntity> findByMasterIdAndItemIdAndStartDateBeforeAndEndDateAfter(
            Long masterId
            , Long itemId
            , LocalDate endDate
            , LocalDate startDate);

    Flux<ReservationEntity> findByMasterIdAndItemIdAndStartDate(
            Long masterId
            , Long itemId
            , LocalDate startDate);
}
