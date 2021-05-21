package com.kmtp.reservation.persistence;

import com.kmtp.reservation.endpoint.Discount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DiscountRepository extends ReactiveCrudRepository<DiscountEntity, Long> {

    Flux<DiscountEntity> findByMasterId(Long masterId);

    Mono<DiscountEntity> findByGoodsId(Long id);

    Mono<Void> deleteByGoodsId(Long id);
}
