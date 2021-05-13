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

import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.ResponseErrorHandler;
import com.kmtp.master.endpoint.Master;
import com.kmtp.master.persistence.MasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    final private GenericValidator genericValidator;

    @Autowired
    public MasterHandler(MasterRepository masterRepository, GenericValidator genericValidator) {
        this.masterRepository = masterRepository;
        this.genericValidator = genericValidator;
    }

    public Mono<ServerResponse> get( ServerRequest request ) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        return masterRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master-id."))
                .map(MasterMapper.INSTANCE::entityToApi)
                .flatMap(master -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(master, Master.class))
                .onErrorResume(ResponseErrorHandler::build);
    }

    public Mono<ServerResponse> post( ServerRequest request ) {

        return request.bodyToMono(Master.class)
                .doOnNext(master -> genericValidator.validate(master, Master.class))
                .map(MasterMapper.INSTANCE::apiToEntity)
                .flatMap(masterRepository::save)
                .flatMap(result -> ServerResponse
                        .created(URI.create(request.path()))
                        .build())
                .onErrorResume(ResponseErrorHandler::build);
    }

    public Mono<ServerResponse> put( ServerRequest request ) {

        return request.bodyToMono(Master.class)
                .doOnNext(master -> genericValidator.validate(master, Master.class))
                .flatMap(masterRepository::updateMaster)
                .flatMap(result -> ServerResponse.noContent().build())
                .onErrorResume(ResponseErrorHandler::build);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        return masterRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master-id."))
                .flatMap(masterEntity -> ServerResponse.noContent()
                        .build(masterRepository.deleteById(id)))
                .onErrorResume(ResponseErrorHandler::build);
    }
}
