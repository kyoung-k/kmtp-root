package com.kmtp.reservation.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DiscountRepository extends ReactiveCrudRepository<DiscountEntity, Long> {

    /**
     * 할인율 정보를 조회합니다.
     * @param masterId 마스터 ID
     * @return {@link Flux}<{@link DiscountEntity}></{@link>
     */
    Flux<DiscountEntity> findByMasterId(Long masterId);

    /**
     * 할인율 정보를 조회합니다.
     * @param goodsId 상품 ID
     * @return {@link Mono}<{@link DiscountEntity}></{@link>
     */
    Mono<DiscountEntity> findByGoodsId(Long goodsId);

    /**
     * 할인율 정보를 삭제합니다.
     * @param goodsId 상품 ID
     * @return {@link Mono}<{@link Void}></{@link>
     */
    Mono<Void> deleteByGoodsId(Long goodsId);
}
