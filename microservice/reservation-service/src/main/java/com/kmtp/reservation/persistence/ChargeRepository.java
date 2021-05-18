package com.kmtp.reservation.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChargeRepository extends ReactiveCrudRepository<ChargeEntity, Long> {

    Flux<ChargeEntity> findByMasterId(Long masterId);

    Mono<ChargeEntity> findByItemId(Long id);

    Mono<Void> deleteByItemId(Long id);
}
