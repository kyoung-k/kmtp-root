package com.kmtp.master.service;

import com.kmtp.common.http.RequestHandler;
import com.kmtp.common.http.ResponseErrorHandler;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.master.endpoint.Schedule;
import com.kmtp.master.persistence.ScheduleEntity;
import com.kmtp.master.persistence.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class ScheduleHandler {

    final private ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleHandler(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public Mono<ServerResponse> findById( ServerRequest request ) {

        final Long masterId = Long.parseLong( request.pathVariable("masterId") );

        Mono<List<Schedule>> monoList = ScheduleMapper.INSTANCE
                .entityFluxToApiFlux( scheduleRepository.findByMasterId(masterId) )
                .collectList();

        return ResponseHandler.ok(monoList);
    }

    public Mono<ServerResponse> post( ServerRequest request ) {

        return RequestHandler.jsonBodyToList(request, Schedule[].class)
                .map(ScheduleMapper.INSTANCE::apiListToEntityList)
                .doOnNext(scheduleRepository::saveAll)
                .then(ServerResponse
                        .created(URI.create(request.path()))
                        .build())
                .onErrorResume(ResponseErrorHandler::build);
    }

    public Mono<ServerResponse> put( ServerRequest request ) {

        final Long masterId = Long.parseLong( request.pathVariable("masterId") );
        final Mono<Long> count = scheduleRepository.countByMasterId( masterId );
        final Mono<List<Schedule>> scheduleList = RequestHandler.jsonBodyToList(request, Schedule[].class);

        return Mono.zip(count, scheduleList)
                .flatMap(tuple2 -> {

                    if (tuple2.getT1() <= 0)
                        return ResponseErrorHandler.notFound("master-id");

                    scheduleRepository.deleteByMasterId(masterId)
                            .subscribe();

                    List<ScheduleEntity> scheduleEntityList =
                            ScheduleMapper.INSTANCE.apiListToEntityList(tuple2.getT2());

                    return scheduleRepository.saveAll(scheduleEntityList)
                            .then(ServerResponse.noContent().build());
                })
                .onErrorResume(ResponseErrorHandler::build);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long masterId = Long.parseLong( request.pathVariable("masterId") );

        return scheduleRepository.countByMasterId(masterId)
                .flatMap(count -> {

                    if (count <= 0) {
                        return ResponseErrorHandler.notFound("master-id");
                    }

                    return scheduleRepository.deleteByMasterId(masterId)
                            .then(ServerResponse.noContent().build());
                });
    }
}
