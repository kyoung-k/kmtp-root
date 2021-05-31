package com.kmtp.reservation.service;

import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.common.api.Reservation;
import com.kmtp.reservation.persistence.ReservationEntity;
import com.kmtp.reservation.persistence.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
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

    public Mono<ServerResponse> checkList(ServerRequest request) {

        Mono<Long> masterIdMono = request.queryParam("masterId")
                .map(Long::parseLong)
                .map(Mono::just)
                .orElseGet(Mono::empty);

        Mono<Long> itemIdMono = request.queryParam("itemId")
                .map(Long::parseLong)
                .map(Mono::just)
                .orElseGet(Mono::empty);

        Mono<LocalDate> startDateMono = request.queryParam("startDate")
                .map(startDate -> LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE))
                .map(Mono::just)
                .orElseGet(Mono::empty);

        Mono<LocalDate> endDateMono = request.queryParam("endDate")
                .map(endDate -> LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE))
                .map(Mono::just)
                .orElseGet(Mono::empty);

        Mono<List<Reservation>> reservationMono = Mono.zip(masterIdMono, itemIdMono, startDateMono, endDateMono)
                .flatMapMany(tuple4 -> reservationRepository.findByMasterIdAndItemIdAndStartDateBeforeAndEndDateAfter(
                        tuple4.getT1()
                        , tuple4.getT2()
                        , tuple4.getT4()
                        , tuple4.getT3()))
                .collectList()
                .map(ReservationMapper.INSTANCE::entityListToApiList);

        return ResponseHandler.ok(reservationMono);
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
