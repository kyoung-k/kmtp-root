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

    /**
     * 해당 기간(startDate ~ endDate) master-id, item-id에 대한 예약을 조회합니다.<br>
     * 해당 기간(startDate ~ endDate) 의 예약을 확인하는 용도로 사용합니다.
     * <p></p>
     * (1) master-id를 query parameter 에서 조회하고 {@link Long} 타입으로 변환합니다.<br>
     * master-id를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (2) item-id를 query parameter 에서 조회하고 {@link Long} 타입으로 변환합니다.<br>
     * item-id를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (3) start-date를 query parameter 에서 조회하고 {@link LocalDate} 타입으로 변환합니다.<br>
     * start-date를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (4) end-date를 query parameter 에서 조회하고 {@link LocalDate} 타입으로 변환합니다.<br>
     * end-date를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (5) 예약정보를 DB에서 조회하고 응답 형태로 변환합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link List}<{@link Reservation}>></{@link>
     */
    public Mono<ServerResponse> periodCheck(ServerRequest request) {

        // (1)
        final Long masterId = request.queryParam("masterId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param master-id is required."));

        // (2)
        final Long itemId = request.queryParam("itemId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param item-id is required."));

        // (3)
        final LocalDate startDate = request.queryParam("startDate")
                .map(date -> LocalDate.parse(date, DateTimeFormatter.ISO_DATE))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param start-date is required."));

        // (4)
        final LocalDate endDate = request.queryParam("endDate")
                .map(date -> LocalDate.parse(date, DateTimeFormatter.ISO_DATE))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param end-date is required."));

        // (5)
        final Mono<List<Reservation>> checkListMono = reservationRepository.findByMasterIdAndItemIdAndStartDateBeforeAndEndDateAfter(
                masterId, itemId, endDate, startDate)
                .collectList()
                .map(ReservationMapper.INSTANCE::entityListToApiList);

        return ResponseHandler.ok(checkListMono);
    }

    /**
     * master-id, item-id, start-date에 대한 예약을 조회합니다.<br>
     * 해당일(start-date) 의 예약을 확인하는 용도로 사용합니다.
     * <p></p>
     * (1) master-id를 query parameter 에서 조회하고 {@link Long} 타입으로 변환합니다.<br>
     * master-id를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (2) item-id를 query parameter 에서 조회하고 {@link Long} 타입으로 변환합니다.<br>
     * item-id를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (3) start-date를 query parameter 에서 조회하고 {@link LocalDate} 타입으로 변환합니다.<br>
     * start-date를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (4) 예약정보를 DB에서 조회하고 응답 형태로 변환합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link List}<{@link Reservation}>></{@link>
     */
    public Mono<ServerResponse> dateCheck(ServerRequest request) {

        // (1)
        final Long masterId = request.queryParam("masterId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param master-id is required."));

        // (2)
        final Long itemId = request.queryParam("itemId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param item-id is required."));

        // (3)
        final LocalDate startDate = request.queryParam("startDate")
                .map(date -> LocalDate.parse(date, DateTimeFormatter.ISO_DATE))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param start-date is required."));

        // (4)
        final Mono<List<Reservation>> checkListMono = reservationRepository.findByMasterIdAndItemIdAndStartDate(
                masterId, itemId, startDate)
                .collectList()
                .map(ReservationMapper.INSTANCE::entityListToApiList);

        return ResponseHandler.ok(checkListMono);
    }

    /**
     * 예약을 등록 합니다.
     * <p></p>
     * (1) request body 정보를 변환한뒤 유효성 체크를 진행합니다.<br>
     * 이미 등록된 예약이 있는지 확인합니다.<br>
     * 이미 등록된 예약이 있으면 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * 이미 등록된 예약이 없으면 Entity 형태로 변환한뒤 예약을 등록합니다.
     * <p></p>
     * @param request
     * @return
     */
    public Mono<ServerResponse> post(ServerRequest request) {

        // (1)
        final Mono<ReservationEntity> reservationMono = request.bodyToMono(Reservation.class)
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
