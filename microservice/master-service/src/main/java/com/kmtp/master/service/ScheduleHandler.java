package com.kmtp.master.service;

import com.kmtp.common.api.Master;
import com.kmtp.common.api.Schedule;
import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.RequestHandler;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.master.persistence.MasterRepository;
import com.kmtp.master.persistence.ScheduleEntity;
import com.kmtp.master.persistence.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class ScheduleHandler {

    final private ScheduleRepository scheduleRepository;
    final private MasterRepository masterRepository;
    final private GenericValidator genericValidator;

    @Autowired
    public ScheduleHandler(ScheduleRepository scheduleRepository
            , MasterRepository masterRepository
            , GenericValidator genericValidator) {

        this.scheduleRepository = scheduleRepository;
        this.masterRepository = masterRepository;
        this.genericValidator = genericValidator;
    }

    /**
     * 스케쥴 목록을 조회합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) 스케쥴 정보를 조회하고 Entity 객체를 응답 객체로 변환합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link List}<{@link Schedule}>></{@link></{@link>
     */
    public Mono<ServerResponse> get( ServerRequest request ) {

        // (1)
        final Long masterId = Long.parseLong( request.pathVariable("masterId") );

        // (2)
        final Mono<List<Schedule>> listMono = scheduleRepository.findByMasterId(masterId)
                .map(ScheduleMapper.INSTANCE::entityToApi)
                .collectList();

        return ResponseHandler.ok(listMono);
    }

    /**
     * 스케쥴 정보를 등록 합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회해서 {@link Mono}를 생성합니다.<br> {@link Long} 타입으로 변환하고 스케쥴 정보를 조회합니다.<br>
     * 스케쥴 정보가 없을경우 HttpStatus.NOT_FOUND 상태로 응답합니다.<br> Entity 객체를 응답 객체로 변환 합니다.<br><br>
     * (2) request body정보를 변환하고 유효성 체크를 진행합니다.<br> {@link Mono} 타입을 {@link Flux} 타입으로 변환합니다 <br>
     * {@link Tuples#of}를 이용해 마스터 정보와 스케쥴 정보를 Merge 합니다. <br>
     * 수정할 마스터 id를 설정하고 스케쥴 정보를 Delete & Insert 합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#CREATED}
     */
    public Mono<ServerResponse> post( ServerRequest request ) {

        // (1)
        final Mono<Master> masterMono = Mono.just(request.pathVariable("masterId"))
                .map(Long::parseLong)
                .flatMap(masterRepository::findById)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master."))
                .map(MasterMapper.INSTANCE::entityToApi);

        // (2)
        final Mono<List<ScheduleEntity>> listMono = RequestHandler.jsonBodyToList(request, Schedule[].class)
                .doOnNext(schedules -> genericValidator.validateList(schedules, Schedule.class))
                .flatMapMany(Flux::fromIterable)
                .flatMap(schedule -> masterMono.map(master -> Tuples.of(schedule, master)))
                .flatMap(tuple2 -> {

                    tuple2.getT1().setMasterId(tuple2.getT2().getId());

                    return scheduleRepository.deleteByMasterId(tuple2.getT2().getId())
                            .then(scheduleRepository.save(ScheduleMapper.INSTANCE.apiToEntity(tuple2.getT1())));
                }).collectList();

        return ResponseHandler.created(listMono, URI.create(request.path()));
    }

    /**
     * 스케쥴 정보를 삭제합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) 스케쥴 정보를 조회합니다.<br> 스케쥴 정보가 없을경우 HttpStatus.NOT_FOUND 상태로 응답합니다.<br>
     * 스케쥴 정보를 삭제합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#NO_CONTENT}
     */
    public Mono<ServerResponse> delete(ServerRequest request) {

        // (1)
        final Long masterId = Long.parseLong( request.pathVariable("masterId") );

        // (2)
        final Mono<Void> voidMono = scheduleRepository.findByMasterId(masterId)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found schedule."))
                .then(scheduleRepository.deleteByMasterId(masterId));

        return ResponseHandler.noContent(voidMono);
    }
}
