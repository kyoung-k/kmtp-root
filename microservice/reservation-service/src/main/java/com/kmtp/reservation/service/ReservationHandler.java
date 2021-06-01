package com.kmtp.reservation.service;

import com.kmtp.common.api.Reservation;
import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.reservation.persistence.ReservationEntity;
import com.kmtp.reservation.persistence.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Component
public class ReservationHandler {

    private ReservationRepository reservationRepository;
    private GenericValidator genericValidator;

    @Autowired
    public ReservationHandler(ReservationRepository reservationRepository, GenericValidator genericValidator) {
        this.reservationRepository = reservationRepository;
        this.genericValidator = genericValidator;
    }

    public Mono<ServerResponse> consecutiveCheck(ServerRequest request) {

        final Long masterId = request.queryParam("masterId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param master-id is required."));

        final Long itemId = request.queryParam("itemId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param item-id is required."));

        final LocalDate startDate = request.queryParam("startDate")
                .map(date -> LocalDate.parse(date, DateTimeFormatter.ISO_DATE))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param start-date is required."));

        final LocalDate endDate = request.queryParam("endDate")
                .map(date -> LocalDate.parse(date, DateTimeFormatter.ISO_DATE))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param end-date is required."));

        Mono<List<Reservation>> checkListMono = reservationRepository.findByMasterIdAndItemIdAndStartDateBeforeAndEndDateAfter(
                masterId, itemId, endDate, startDate)
                .collectList()
                .map(ReservationMapper.INSTANCE::entityListToApiList);

        return ResponseHandler.ok(checkListMono);
    }

    public Mono<ServerResponse> post(ServerRequest request) {

        Mono<ReservationEntity> reservationMono = request.bodyToMono(Reservation.class)
                .doOnNext(reservation -> genericValidator.validate(reservation, Reservation.class))
                .flatMap(reservation -> reservationRepository.findByMasterIdAndScheduleIdAndItemIdAndStartDateBeforeAndEndDateAfter(
                        reservation.getMasterId()
                        , reservation.getScheduleId()
                        , reservation.getItemId()
                        , reservation.getEndDate()
                        , reservation.getStartDate())
                        .doOnNext(reservationEntity -> Optional.of(reservationEntity)
                                .ifPresent(optional -> GenericError.error(HttpStatus.BAD_REQUEST, "Reservations cannot be made on that date.")))
                        .switchIfEmpty(Mono.defer(() -> Mono.just(ReservationMapper.INSTANCE.apiToEntity(reservation)))))
                .flatMap(reservationRepository::save);

        return ResponseHandler.created(reservationMono, URI.create(request.path()));
    }
}
