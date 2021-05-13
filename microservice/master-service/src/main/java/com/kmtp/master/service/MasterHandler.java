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

import com.kmtp.common.exception.ValidationErrorHandler;
import com.kmtp.common.generic.GenericValidate;
import com.kmtp.common.http.HttpErrorInfo;
import com.kmtp.master.endpoint.Master;
import com.kmtp.master.persistence.MasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class MasterHandler implements GenericValidate<Master> {

    final private Validator validator;
    final private MasterRepository masterRepository;

    @Autowired
    public MasterHandler(Validator validator, MasterRepository masterRepository) {
        this.validator = validator;
        this.masterRepository = masterRepository;
    }

    public Mono<ServerResponse> get( ServerRequest request ) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        return masterRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NO_CONTENT, "not found master-id.")))
                .map(MasterMapper.INSTANCE::entityToApi)
                .flatMap(master -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(master, Master.class))
                .onErrorResume(HttpErrorInfo::build);
    }

    public Mono<ServerResponse> post( ServerRequest request ) {

        return request.bodyToMono(Master.class)
                .doOnNext(this::validate)
                .map(MasterMapper.INSTANCE::apiToEntity)
                .flatMap(masterRepository::save)
                .flatMap(result -> ServerResponse
                        .created(URI.create(request.path()))
                        .build())
                .onErrorResume(HttpErrorInfo::build);
    }

    public Mono<ServerResponse> put( ServerRequest request ) {

        return request.bodyToMono(Master.class)
                .doOnNext(this::validate)
                .flatMap(masterRepository::updateMaster)
                .flatMap(result -> ServerResponse.noContent().build())
                .onErrorResume(HttpErrorInfo::build);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        return masterRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "not found master-id.")))
                .flatMap(masterEntity -> ServerResponse.noContent()
                        .build(masterRepository.deleteById(id)))
                .onErrorResume(HttpErrorInfo::build);
    }

    @Override
    public void validate(Master api) {

        Errors errors = new BeanPropertyBindingResult(api, Master.class.getName());
        validator.validate(api, errors);
        if (errors.hasErrors()) {
            ValidationErrorHandler.handle(errors);
        }
    }
}
