package com.kmtp.reservation.endpoint;

import com.kmtp.common.filter.FunctionalApiExceptionFilter;
import com.kmtp.reservation.service.ItemHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ItemRouter {

    private ItemHandler itemHandler;
    private FunctionalApiExceptionFilter functionalApiExceptionFilter;

    @Autowired
    public ItemRouter(ItemHandler itemHandler, FunctionalApiExceptionFilter functionalApiExceptionFilter) {
        this.itemHandler = itemHandler;
        this.functionalApiExceptionFilter = functionalApiExceptionFilter;
    }

    @Bean
    public RouterFunction<ServerResponse> itemRoutes() {
        return RouterFunctions.route()
                .GET("/item", itemHandler::list)
                .GET("/item/{id}", itemHandler::get)
                .POST("/item", itemHandler::post)
                .PUT("/item/{id}", itemHandler::put)
                .DELETE("/item/{id}", itemHandler::delete)
                .filter(functionalApiExceptionFilter.exceptionHandler())
                .build();
    }
}
