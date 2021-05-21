package com.kmtp.reservation.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemGoodsRepository extends ReactiveCrudRepository<ItemGoodsEntity, Long> {

    Mono<Void> deleteByGoodsId(Long id);
}
