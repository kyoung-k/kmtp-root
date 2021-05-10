/*
 * Copyright (c) 2021-Present KYoung.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kmtp.master.service;

import com.kmtp.master.endpoint.Master;
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

    final private MasterRepository masterRepository;

    @Autowired
    public MasterHandler(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }

    public Mono<ServerResponse> get( ServerRequest request ) {

        final Long id = Long.parseLong( request.pathVariable("id") );
        Mono<Master> master = masterRepository.findById(id)
                .map(MasterMapper.INSTANCE::entityToApi);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(master, Master.class);
    }

    public Mono<ServerResponse> post( ServerRequest request ) {

        return request.bodyToMono(Master.class)
            .map(MasterMapper.INSTANCE::apiToEntity)
            .flatMap(masterRepository::save)
            .flatMap(result -> ServerResponse
                .created(URI.create(request.path()))
                .build());
    }

    public Mono<ServerResponse> put( ServerRequest request ) {

        return request.bodyToMono(Master.class)
                .flatMap(masterRepository::updateMaster)
                .flatMap(result -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        return ServerResponse.noContent()
                .build(masterRepository.deleteById(id));
    }
}
