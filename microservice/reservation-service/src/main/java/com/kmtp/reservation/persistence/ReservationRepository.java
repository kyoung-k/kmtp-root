package com.kmtp.reservation.persistence;

import com.kmtp.common.api.MasterType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface ReservationRepository extends ReactiveCrudRepository<ReservationEntity, Long> {

    /**
     * 예약 정보를 조회합니다.
     *
     * @param masterId 마스터 ID
     * @param scheduleId 스케쥴 ID
     * @param itemId 아이템 ID
     * @param endDate 예약 종료일
     * @param startDate 예약 시작일
     * @return {@link Mono}<{@link ReservationEntity}></{@link>
     */
    Mono<ReservationEntity> findByMasterIdAndScheduleIdAndItemIdAndStartDateBeforeAndEndDateAfter(
            Long masterId
            , Long scheduleId
            , Long itemId
            , LocalDate endDate
            , LocalDate startDate);

    /**
     * 예약 정보를 조회합니다. {@link MasterType#CONSECUTIVE} 타입일때 예약 가능 여부 확인을 위해 사용합니다.
     *
     * @param masterId 마스터 ID
     * @param itemId 아이템 ID
     * @param endDate 예약 종료일
     * @param startDate 예약 시작일
     * @return
     */
    Flux<ReservationEntity> findByMasterIdAndItemIdAndStartDateBeforeAndEndDateAfter(
            Long masterId
            , Long itemId
            , LocalDate endDate
            , LocalDate startDate);

    /**
     * 예약 정보를 조회합니다. {@link MasterType#NONCONSECUTIVE} 타입일때 예약 가능 여부 확인을 위해 사용합니다.
     *
     * @param masterId 마스터 ID
     * @param itemId 아이템 ID
     * @param startDate 예약 시작일
     * @return
     */
    Flux<ReservationEntity> findByMasterIdAndItemIdAndStartDate(
            Long masterId
            , Long itemId
            , LocalDate startDate);
}
