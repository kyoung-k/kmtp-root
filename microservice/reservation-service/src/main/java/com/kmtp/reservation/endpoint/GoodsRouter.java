package com.kmtp.reservation.endpoint;

import com.kmtp.common.filter.FunctionalApiExceptionFilter;
import com.kmtp.reservation.service.GoodsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GoodsRouter {

    private GoodsHandler goodsHandler;
    private FunctionalApiExceptionFilter functionalApiExceptionFilter;

    @Autowired
    public GoodsRouter(GoodsHandler goodsHandler, FunctionalApiExceptionFilter functionalApiExceptionFilter) {
        this.goodsHandler = goodsHandler;
        this.functionalApiExceptionFilter = functionalApiExceptionFilter;
    }

    @Bean
    public RouterFunction<ServerResponse> goodsRoutes() {
        return RouterFunctions.route()
                .GET("/goods", goodsHandler::list)
                .GET("/goods/{id}", goodsHandler::get)
                .POST("/goods", goodsHandler::post)
                .PUT("/goods/{id}", goodsHandler::put)
                .DELETE("/goods/{id}", goodsHandler::delete)
                .filter(functionalApiExceptionFilter.exceptionHandler())
                .build();
    }
}
