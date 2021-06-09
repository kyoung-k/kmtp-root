package com.kmtp.reservation.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemGoodsRepository extends ReactiveCrudRepository<ItemGoodsEntity, Long> {

    /**
     * 상품 기준으로 아이템-상품 관계를 조회합니다.
     * @param goodsId 상품 ID
     * @return {@link Flux}<{@link ItemGoodsEntity}></{@link>
     */
    Flux<ItemGoodsEntity> findByGoodsId(Long goodsId);

    /**
     * 아이템 기준으로 아이템-상품 관계를 조회합니다.
     * @param itemId 아이템 ID
     * @return {@link Flux}<{@link ItemGoodsEntity}></{@link>
     */
    Flux<ItemGoodsEntity> findByItemId(Long itemId);

    /**
     * 상품 기준으로 아이템-상품 관계를 삭제합니다.
     * @param goodsId 상품 ID
     * @return {@link Mono}<{@link Void}></{@link>
     */
    Mono<Void> deleteByGoodsId(Long goodsId);
}
