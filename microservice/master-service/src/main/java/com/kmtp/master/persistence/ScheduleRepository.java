package com.kmtp.master.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends ReactiveCrudRepository<ScheduleEntity, Long> {
}
