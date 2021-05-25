package com.kmtp.reservation.endpoint;

import com.kmtp.common.filter.FunctionalApiExceptionFilter;
import com.kmtp.reservation.service.GoodsHandler;
import com.kmtp.reservation.service.ItemGoodsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ItemGoodsRouter {

    private ItemGoodsHandler itemGoodsHandler;
    private FunctionalApiExceptionFilter functionalApiExceptionFilter;

    @Autowired
    public ItemGoodsRouter(ItemGoodsHandler itemGoodsHandler, FunctionalApiExceptionFilter functionalApiExceptionFilter) {
        this.itemGoodsHandler = itemGoodsHandler;
        this.functionalApiExceptionFilter = functionalApiExceptionFilter;
    }

    @Bean
    public RouterFunction<ServerResponse> itemGoodsRoutes() {
        return RouterFunctions.route()
                .GET("/itemGoods/item/{itemId}", itemGoodsHandler::itemList)
                .GET("/itemGoods/goods/{goodsId}", itemGoodsHandler::goodsList)
                .POST("/itemGoods/{goodsId}", itemGoodsHandler::post)
                .DELETE("/itemGoods/{goodsId}", itemGoodsHandler::delete)
                .filter(functionalApiExceptionFilter.exceptionHandler())
                .build();
    }
}
