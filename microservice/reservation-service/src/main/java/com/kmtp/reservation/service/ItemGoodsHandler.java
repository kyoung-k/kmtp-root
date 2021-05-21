package com.kmtp.reservation.service;

import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.RequestHandler;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.reservation.endpoint.Item;
import com.kmtp.reservation.endpoint.ItemGoods;
import com.kmtp.reservation.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.Not;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Component
public class ItemGoodsHandler {

    private ItemRepository itemRepository;
    private GoodsRepository goodsRepository;
    private ItemGoodsRepository itemGoodsRepository;
    private GenericValidator genericValidator;

    @Autowired
    public ItemGoodsHandler(ItemRepository itemRepository
            , GoodsRepository goodsRepository
            , ItemGoodsRepository itemGoodsRepository
            , GenericValidator genericValidator) {

        this.itemRepository = itemRepository;
        this.goodsRepository = goodsRepository;
        this.itemGoodsRepository = itemGoodsRepository;
        this.genericValidator = genericValidator;
    }

    public Mono<ServerResponse> list(ServerRequest request) {

        return null;
    }

    public Mono<ServerResponse> post(ServerRequest request) {

        Mono<List<ItemGoodsEntity>> listMono = RequestHandler.jsonBodyToList(request, ItemGoods[].class)
                .doOnNext(itemGoods -> genericValidator.validateList(itemGoods, ItemGoods.class))
                .flatMapMany(Flux::fromIterable)
                .flatMap(itemGoods -> {

                    final Mono<ItemEntity> itemEntityMono = itemRepository.findById(itemGoods.getItemId())
                            .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id."));

                    final Mono<GoodsEntity> goodsEntityMono = goodsRepository.findById(itemGoods.getGoodsId())
                            .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods-id."));

                    return Mono.zip(itemEntityMono, goodsEntityMono);
                })
                .flatMap(tuple2 -> {

                    final ItemGoodsEntity itemGoodsEntity = ItemGoodsEntity.builder()
                            .itemId(tuple2.getT1().getId())
                            .goodsId(tuple2.getT2().getId())
                            .build();

                    return itemGoodsRepository.deleteByGoodsId(tuple2.getT2().getId())
                            .then(itemGoodsRepository.save(itemGoodsEntity));
                }).collectList();

        return ResponseHandler.created(listMono, URI.create(request.path()));
    }

    public Mono<ServerResponse> put(ServerRequest request) {

        return null;
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        return null;
    }
}
