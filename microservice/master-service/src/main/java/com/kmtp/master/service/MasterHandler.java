package com.kmtp.master.service;

import com.kmtp.master.persistence.MasterEntity;
import com.kmtp.master.persistence.MasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class MasterHandler {

    private MasterRepository masterRepository;

    @Autowired
    public MasterHandler(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }

    public Mono<ServerResponse> getMaster( ServerRequest request ) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(masterRepository.findById(1l), MasterEntity.class);
    }

    public Mono<ServerResponse> postMaster( ServerRequest request ) {

        return request.bodyToMono(Master.class)
                .map(api -> MasterMapper.INSTANCE.apiToEntity(api))
                .flatMap(masterRepository::save)
                .flatMap(result -> ServerResponse
                        .created(URI.create(request.path()))
                        .build());
//        return ServerResponse.created(URI.create(request.path())).build();
    }
}
