package com.kmtp.reservation.service;

import com.kmtp.reservation.persistence.ReservationEntity;
import com.kmtp.reservation.persistence.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class ReservationHandler {

    private ReservationRepository reservationRepository;

    @Autowired
    public ReservationHandler(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Mono<ServerResponse> getReservation(ServerRequest request) {
        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(reservationRepository.findById(1l), ReservationEntity.class);
    }
}
