package com.kmtp.reservation.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemRepository extends ReactiveCrudRepository<ItemEntity, Long> {

    /**
     * 아이템 정보를 조회합니다.
     * @param masterId 마스터 ID
     * @return {@link Flux}<{@link ItemEntity}></{@link>
     */
    Flux<ItemEntity> findByMasterId(Long masterId);
}
