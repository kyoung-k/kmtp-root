package com.kmtp.master.service;

import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.RequestHandler;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.common.api.Master;
import com.kmtp.common.api.Schedule;
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

    public Mono<ServerResponse> findById( ServerRequest request ) {

        final Long masterId = Long.parseLong( request.pathVariable("masterId") );

        Mono<List<Schedule>> listMono = scheduleRepository.findByMasterId(masterId)
                .map(ScheduleMapper.INSTANCE::entityToApi)
                .collectList();

        return ResponseHandler.ok(listMono);
    }

    public Mono<ServerResponse> post( ServerRequest request ) {

        final Mono<Master> masterMono = Mono.just(request.pathVariable("masterId"))
                .map(Long::parseLong)
                .flatMap(masterRepository::findById)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master."))
                .map(MasterMapper.INSTANCE::entityToApi);

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

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long masterId = Long.parseLong( request.pathVariable("masterId") );

        final Mono<Void> voidMono = scheduleRepository.findByMasterId(masterId)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found schedule."))
                .then(scheduleRepository.deleteByMasterId(masterId));

        return ResponseHandler.noContent(voidMono);
    }
}
