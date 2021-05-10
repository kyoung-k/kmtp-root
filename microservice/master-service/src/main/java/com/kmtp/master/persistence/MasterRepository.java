package com.kmtp.master.persistence;

import com.kmtp.master.endpoint.Master;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MasterRepository extends ReactiveCrudRepository<MasterEntity, Long> {

    default Mono<MasterEntity> updateMaster(Master master) {

        return this.findById(master.getId())
                .flatMap(entity -> entity.change(me -> {
                    me.setName(master.getName());
                    me.setInformation(master.getInformation());
                }).persistenceMono(() -> this.save(entity)));
    }
}
