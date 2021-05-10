package com.kmtp.master.endpoint;

import com.kmtp.master.service.ScheduleHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ScheduleRouter {

    final private ScheduleHandler scheduleHandler;

    @Autowired
    public ScheduleRouter(ScheduleHandler scheduleHandler) {
        this.scheduleHandler = scheduleHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> scheduleRoutes() {
        return RouterFunctions.route()
                .GET("/schedule/{masterId}", scheduleHandler::findById)
                .POST("/schedule", scheduleHandler::post)
                .PUT("/schedule/{masterId}", scheduleHandler::put)
                .DELETE("/schedule/{masterId}", scheduleHandler::delete)
                .build();
    }
}
