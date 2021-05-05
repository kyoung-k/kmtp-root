package com.kmtp.master.persistence;

import com.kmtp.master.service.Master;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Repository
public interface MasterRepository extends ReactiveCrudRepository<MasterEntity, Long> {

    default Mono<ServerResponse> updateMaster(Master master) {

        return this.findById(master.getId())
                .flatMap(entity -> {

                    entity.setName(master.getName());
                    entity.setInformation(master.getInformation());

                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.save(entity), MasterEntity.class);
                });
    }
}
