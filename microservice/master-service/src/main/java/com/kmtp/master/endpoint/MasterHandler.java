package com.kmtp.master.endpoint;

import com.kmtp.master.persistence.MasterEntity;
import com.kmtp.master.persistence.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class MasterHandler {

    private MasterRepository masterRepository;

    public Mono<ServerResponse> endpointTest(ServerRequest request) {
        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(masterRepository.findById(1)), MasterEntity.class);
    }
}
