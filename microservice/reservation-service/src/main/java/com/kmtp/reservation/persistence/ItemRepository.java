package com.kmtp.reservation.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemRepository extends ReactiveCrudRepository<ItemEntity, Long> {

    Flux<ItemEntity> findByMasterId(Long masterId);
}
