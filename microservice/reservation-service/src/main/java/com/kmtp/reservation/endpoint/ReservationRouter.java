package com.kmtp.reservation.endpoint;

import com.kmtp.reservation.service.ReservationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReservationRouter {

    private ReservationHandler reservationHandler;

    @Autowired
    public ReservationRouter(ReservationHandler reservationHandler) {
        this.reservationHandler = reservationHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> reservationRoutes() {
        return RouterFunctions.route()
                .GET("/reservation/{id}", reservationHandler::getReservation)
                .build();
    }
}
