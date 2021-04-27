package com.kmtp.master.endpoint;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class MasterHandler {

    public Mono<ServerResponse> endpointTest(ServerRequest request) {
        return ok().contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("Functional Endpoint Test!!"), String.class);
    }
}
