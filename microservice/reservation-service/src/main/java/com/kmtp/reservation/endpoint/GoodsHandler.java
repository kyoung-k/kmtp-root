package com.kmtp.reservation.endpoint;

import com.kmtp.reservation.persistence.GoodsEntity;
import com.kmtp.reservation.persistence.GoodsRepository;
import com.kmtp.reservation.persistence.ItemEntity;
import com.kmtp.reservation.persistence.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class GoodsHandler {

    private GoodsRepository goodsRepository;

    @Autowired
    public GoodsHandler(GoodsRepository goodsRepository) {
        this.goodsRepository = goodsRepository;
    }

    public Mono<ServerResponse> getGoods(ServerRequest request) {
        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(goodsRepository.findById(1l), GoodsEntity.class);
    }
}
