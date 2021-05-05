package com.kmtp.master.service;

import com.kmtp.master.persistence.MasterEntity;
import com.kmtp.master.persistence.MasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
public class MasterHandler {

    private MasterRepository masterRepository;

    @Autowired
    public MasterHandler(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }

    public Mono<ServerResponse> getMaster( ServerRequest request ) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(masterRepository.findById(id)
                        .map(MasterMapper.INSTANCE::entityToApi), Master.class);
    }

    public Mono<ServerResponse> postMaster( ServerRequest request ) {

        return request.bodyToMono(Master.class)
                .log()
                .map(MasterMapper.INSTANCE::apiToEntity)
                .flatMap(masterRepository::save)
                .flatMap(result -> ServerResponse
                        .created(URI.create(request.path()))
                        .build());
    }

    public Mono<ServerResponse> putMaster( ServerRequest request ) {

        return request.bodyToMono(Master.class)
                .log()
                .flatMap(masterRepository::updateMaster);
    }

    public Mono<ServerResponse> deleteMaster(ServerRequest request) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(masterRepository.deleteById(id), Master.class);
    }
}
