package com.kmtp.reservation.service;

import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.RequestHandler;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.common.api.ItemGoods;
import com.kmtp.reservation.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Mono<ServerResponse> itemList(ServerRequest request) {

        final Long itemId = Long.parseLong(request.pathVariable("itemId"));

        Mono<List<ItemGoods>> listMono = itemGoodsRepository.findByItemId(itemId)
                .map(ItemGoodsMapper.INSTANCE::entityToApi)
                .collectList();

        return ResponseHandler.ok(listMono);
    }

    public Mono<ServerResponse> goodsList(ServerRequest request) {

        final Long goodsId = Long.parseLong(request.pathVariable("goodsId"));

        Mono<List<ItemGoods>> listMono = itemGoodsRepository.findByGoodsId(goodsId)
                .map(ItemGoodsMapper.INSTANCE::entityToApi)
                .collectList();

        return ResponseHandler.ok(listMono);
    }

    public Mono<ServerResponse> post(ServerRequest request) {

        final Long goodsId = Long.parseLong(request.pathVariable("goodsId"));

        Mono<List<ItemGoodsEntity>> listMono = itemGoodsRepository.deleteByGoodsId(goodsId)
                .flatMap(delete -> RequestHandler.jsonBodyToList(request, ItemGoods[].class)
                        .doOnNext(itemGoods -> genericValidator.validateList(itemGoods, ItemGoods.class))
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(itemGoods -> {

                            final Mono<ItemEntity> itemEntityMono = itemRepository.findById(itemGoods.getItemId())
                                    .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item."));

                            final Mono<GoodsEntity> goodsEntityMono = goodsRepository.findById(itemGoods.getGoodsId())
                                    .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods."));

                            return Mono.zip(itemEntityMono, goodsEntityMono);
                        })
                        .flatMap(tuple2 -> {

                            final ItemGoodsEntity itemGoodsEntity = ItemGoodsEntity.builder()
                                    .itemId(tuple2.getT1().getId())
                                    .goodsId(tuple2.getT2().getId())
                                    .build();

                            return itemGoodsRepository.save(itemGoodsEntity);
                        }).collectList());

        return ResponseHandler.created(listMono, URI.create(request.path()));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long goodsId = Long.parseLong(request.pathVariable("goodsId"));

        Mono<Void> voidMono = itemGoodsRepository.findByGoodsId(goodsId)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found itemgoods."))
                .then(itemGoodsRepository.deleteByGoodsId(goodsId));

        return ResponseHandler.noContent(voidMono);
    }
}
