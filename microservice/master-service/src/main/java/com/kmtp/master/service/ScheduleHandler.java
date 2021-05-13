package com.kmtp.master.service;

import com.google.gson.Gson;
import com.kmtp.common.http.ResponseErrorHandler;
import com.kmtp.master.endpoint.Schedule;
import com.kmtp.master.persistence.ScheduleEntity;
import com.kmtp.master.persistence.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
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

        return ScheduleMapper.INSTANCE
                .entityFluxToApiFlux( scheduleRepository.findByMasterId(masterId) )
                .log()
                .collectList()
                .flatMap(schedules -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(schedules, Schedule.class))
                .onErrorResume(ResponseErrorHandler::build);
    }

    public Mono<ServerResponse> post( ServerRequest request ) {

        return request.bodyToMono(String.class).log()
                .flatMapMany(jsonString -> {

                    final Gson gson = new Gson();
                    final List<ScheduleEntity> scheduleEntityList = Arrays.asList( gson.fromJson(jsonString, ScheduleEntity[].class) );

                    return scheduleRepository.saveAll(scheduleEntityList);
                })
                .then(ServerResponse
                        .created(URI.create(request.path()))
                        .build());
    }

    public Mono<ServerResponse> put( ServerRequest request ) {

        final Long masterId = Long.parseLong( request.pathVariable("masterId") );

        return request.bodyToMono(String.class).log()
                .flatMap(jsonString -> scheduleRepository.countByMasterId(masterId)
                    .flatMap(count -> {

                        if (count <= 0) {
                            return ServerResponse.notFound().build();
                        }

                        scheduleRepository.deleteByMasterId(masterId)
                                .subscribe();

                        final Gson gson = new Gson();
                        final List<ScheduleEntity> scheduleEntityList = Arrays.asList( gson.fromJson(jsonString, ScheduleEntity[].class) );

                        return scheduleRepository.saveAll(scheduleEntityList)
                                .then(ServerResponse.noContent().build());
                    }));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long masterId = Long.parseLong( request.pathVariable("masterId") );

        return scheduleRepository.countByMasterId(masterId)
                .flatMap(count -> {

                    if (count <= 0) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "not fount masterId."));
                    }

                    return scheduleRepository.deleteByMasterId(masterId)
                            .then(ServerResponse.noContent().build());
                });
    }
}
