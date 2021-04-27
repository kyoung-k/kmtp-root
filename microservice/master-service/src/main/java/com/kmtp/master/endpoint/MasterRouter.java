package com.kmtp.master.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class MasterRouter {

    private MasterHandler masterHandler;

    @Autowired
    public MasterRouter(MasterHandler masterHandler) {
        this.masterHandler = masterHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> masterRoutes() {
        return RouterFunctions.route()
                .GET("/master.ky", masterHandler::endpointTest)
                .build();
    }
}
