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

    /**
     * 마스터, 마스터의 스케쥴 목록을 조회합니다.<br>
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) 마스터 정보를 조회한뒤 응답 객체로 변환합니다.<br> 마스터 정보가 없을경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (3) 스케쥴 정보를 조회한뒤 응답 객체료 변환합니다.<br> 스케쥴 정보가 없을경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (4) 마스터, 스케쥴 정보를 {@link Mono#zip} 처리합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link Master}></{@link>
     */
    public Mono<ServerResponse> get( ServerRequest request ) {

        // (1)
        final Long id = Long.parseLong( request.pathVariable("id") );

        // (2)
        final Mono<Master> masterMono = masterRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master-id."))
                .map(MasterMapper.INSTANCE::entityToApi);

        // (3)
        final Mono<List<Schedule>> scheduleListMono = scheduleRepository.findByMasterId(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master schedule"))
                .collectList()
                .map(ScheduleMapper.INSTANCE::entityListToApiList);

        // (4)
        final Mono<Master> responseMono = Mono.zip(masterMono, scheduleListMono)
                .flatMap(tuple2 -> {
                    tuple2.getT1().setScheduleList(tuple2.getT2());
                    return Mono.just(tuple2.getT1());
                });

        return ResponseHandler.ok(responseMono);
    }

    /**
     * 마스터 정보를 등록합니다.
     * <p></p>
     * (1) request body 정보를 변환한뒤 유효성 체크를 진행합니다. Entity 객체로 변한한뒤 DB에 등록합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#CREATED}
     */
    public Mono<ServerResponse> post( ServerRequest request ) {

        // (1)
        final Mono<MasterEntity> saveMasterMono = request.bodyToMono(Master.class)
                .doOnNext(master -> genericValidator.validate(master, Master.class))
                .map(MasterMapper.INSTANCE::apiToEntity)
                .flatMap(masterRepository::save);

        return ResponseHandler.created(saveMasterMono, URI.create(request.path()));
    }

    /**
     * 마스터 정보를 수정합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) request body 정보를 변환한뒤 유효성 체크를 진행합니다.<br>
     * (3) 마스터 정보를 DB에서 조회합니다.<br> 마스터 정보가 없을경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (4) request 정보와 DB 정보를 {@link Mono#zip} 하여 수정할 정보를 설정하고 DB에 저장합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#NO_CONTENT}
     */
    public Mono<ServerResponse> put( ServerRequest request ) {

        // (1)
        final Long id = Long.parseLong(request.pathVariable("id"));

        // (2)
        final Mono<Master> masterMono = request.bodyToMono(Master.class)
                .doOnNext(master -> genericValidator.validate(master, Master.class));

        // (3)
        final Mono<MasterEntity> masterEntityMono = masterRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master"));

        // (4)
        final Mono<MasterEntity> updateMasterMono = Mono.zip(masterMono, masterEntityMono)
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

    /**
     * 마스터 정보를 삭제합니다.<br>
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) 마스터 정보를 DB에서 조회합니다.<br> 마스터 정보가 없을경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br> 조회된 마스터 정보를 삭제합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#NO_CONTENT}
     */
    public Mono<ServerResponse> delete(ServerRequest request) {

        // (1)
        final Long id = Long.parseLong( request.pathVariable("id") );

        // (2)
        final Mono<Void> deleteMasterMono = masterRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master-id."))
                .then(masterRepository.deleteById(id));

        return ResponseHandler.noContent(deleteMasterMono);
    }
}
