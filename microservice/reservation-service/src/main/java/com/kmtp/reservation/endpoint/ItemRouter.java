package com.kmtp.reservation.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ItemRouter {

    private ItemHandler itemHandler;

    @Autowired
    public ItemRouter(ItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> itemRoutes() {
        return RouterFunctions.route()
                .GET("/item/{id}", itemHandler::getItem)
                .build();
    }
}
