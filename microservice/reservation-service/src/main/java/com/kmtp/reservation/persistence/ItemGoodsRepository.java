package com.kmtp.reservation.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemGoodsRepository extends ReactiveCrudRepository<ItemGoodsEntity, Long> {
}
