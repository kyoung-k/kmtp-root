package com.kmtp.master.persistence;

import com.kmtp.master.service.Master;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Repository
public interface MasterRepository extends ReactiveCrudRepository<MasterEntity, Long> {

    default Mono<?> updateMaster(Master master) {

        return this.findById(master.getId())
                .flatMap(entity -> entity.change(me -> {

                        me.setName(master.getName());
                        me.setInformation(master.getInformation());
                    })
                    .persistenceMono(() -> this.save(entity))
                );
    }
}
