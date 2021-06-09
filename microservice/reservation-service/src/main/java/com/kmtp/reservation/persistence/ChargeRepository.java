package com.kmtp.reservation.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChargeRepository extends ReactiveCrudRepository<ChargeEntity, Long> {

    /**
     * 금액 정보를 조회합니다.
     * @param masterId 마스터 ID
     * @return {@link Flux}<{@link ChargeEntity}></{@link>
     */
    Flux<ChargeEntity> findByMasterId(Long masterId);

    /**
     * 금액 정보를 조회합니다.
     * @param itemId 아이템 ID
     * @return {@link Mono}<{@link ChargeEntity}></{@link>
     */
    Mono<ChargeEntity> findByItemId(Long itemId);

    /**
     * 금액 정보를 삭제합니다.
     * @param itemId 아이템 ID
     * @return {@link Mono}<{@link Void}></{@link>
     */
    Mono<Void> deleteByItemId(Long itemId);
}
