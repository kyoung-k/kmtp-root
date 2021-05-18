package com.kmtp.reservation.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface GoodsRepository extends ReactiveCrudRepository<GoodsEntity, Long> {

    Flux<GoodsEntity> findByMasterId(Long masterId);
}
