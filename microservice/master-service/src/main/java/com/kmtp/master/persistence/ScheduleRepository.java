package com.kmtp.master.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ScheduleRepository extends ReactiveCrudRepository<ScheduleEntity, Long> {

    Flux<ScheduleEntity> findByMasterId(Long masterId);

    Mono<Long> countByMasterId(Long masterId);

    Mono<ScheduleEntity> deleteByMasterId(Long masterId);
}
