package com.kmtp.master.persistence;

import com.kmtp.master.endpoint.Master;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MasterRepository extends ReactiveCrudRepository<MasterEntity, Long> {

}
