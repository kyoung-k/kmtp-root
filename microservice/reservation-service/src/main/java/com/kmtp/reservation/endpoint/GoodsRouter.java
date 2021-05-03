package com.kmtp.reservation.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GoodsRouter {

    private GoodsHandler goodsHandler;

    @Autowired
    public GoodsRouter(GoodsHandler goodsHandler) {
        this.goodsHandler = goodsHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> goodsRoutes() {
        return RouterFunctions.route()
                .GET("/goods/{id}", goodsHandler::getGoods)
                .build();
    }
}
