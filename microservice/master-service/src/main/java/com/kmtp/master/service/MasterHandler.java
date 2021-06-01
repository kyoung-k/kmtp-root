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

import com.kmtp.common.api.Master;
import com.kmtp.common.api.Schedule;
import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.master.persistence.MasterEntity;
import com.kmtp.master.persistence.MasterRepository;
import com.kmtp.master.persistence.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class MasterHandler {

    final private MasterRepository masterRepository;
    final private ScheduleRepository scheduleRepository;
    final private GenericValidator genericValidator;

    @Autowired
    public MasterHandler(MasterRepository masterRepository
            , ScheduleRepository scheduleRepository
            , GenericValidator genericValidator) {

        this.masterRepository = masterRepository;
        this.scheduleRepository = scheduleRepository;
        this.genericValidator = genericValidator;
    }

    public Mono<ServerResponse> get( ServerRequest request ) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        Mono<Master> masterMono = masterRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master-id."))
                .map(MasterMapper.INSTANCE::entityToApi);

        Mono<List<Schedule>> scheduleListMono = scheduleRepository.findByMasterId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master schedule"))
                .collectList()
                .map(ScheduleMapper.INSTANCE::entityListToApiList);

        Mono<Master> responseMono = Mono.zip(masterMono, scheduleListMono)
                .flatMap(tuple2 -> {
                    tuple2.getT1().setScheduleList(tuple2.getT2());
                    return Mono.just(tuple2.getT1());
                });

        return ResponseHandler.ok(responseMono);
    }

    public Mono<ServerResponse> post( ServerRequest request ) {

        Mono<MasterEntity> saveMasterMono = request.bodyToMono(Master.class)
                .doOnNext(master -> genericValidator.validate(master, Master.class))
                .map(MasterMapper.INSTANCE::apiToEntity)
                .flatMap(masterRepository::save);

        return ResponseHandler.created(saveMasterMono, URI.create(request.path()));
    }

    public Mono<ServerResponse> put( ServerRequest request ) {

        final Long id = Long.parseLong(request.pathVariable("id"));
        final Mono<Master> masterMono = request.bodyToMono(Master.class)
                .doOnNext(master -> genericValidator.validate(master, Master.class));

        final Mono<MasterEntity> masterEntityMono = masterRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id"));

        Mono<MasterEntity> updateMasterMono = Mono.zip(masterMono, masterEntityMono)
                .flatMap(tuple2 -> {

                    tuple2.getT2().change(masterEntity -> {
                        masterEntity.setName(tuple2.getT1().getName());
                        masterEntity.setInformation(tuple2.getT1().getInformation());
                    });

                    return Mono.just(tuple2.getT2())
                            .flatMap(masterRepository::save);
                });

        return ResponseHandler.noContent(updateMasterMono);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        final Mono<Void> deleteMasterMono = masterRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master-id."))
                .then(masterRepository.deleteById(id));

        return ResponseHandler.noContent(deleteMasterMono);
    }
}
