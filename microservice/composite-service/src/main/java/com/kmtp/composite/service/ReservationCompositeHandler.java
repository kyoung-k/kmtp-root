/*
 * Copyright (c) 2021-Present KYoung.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kmtp.composite.service;

import com.kmtp.common.api.*;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.common.http.WebClientHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ReservationCompositeHandler {

    @Autowired
    public ReservationCompositeHandler() {
    }

    public Mono<ServerResponse> goodsList(ServerRequest request) {

        final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>(){{
            put("masterId", Collections.singletonList(request.pathVariable("masterId")));
        }};

        final Flux<GoodsList.Response> responseFlux = WebClientHandler.build(ApiInfo.GOODS_LIST)
                .queryParam(paramMap)
                .monoList(Goods.class)
                .flatMapMany(Flux::fromIterable)
                .flatMap(goods -> {

                    final Mono<List<Item>> itemListMono = WebClientHandler.build(ApiInfo.ITEMGOODS_GOODSLIST)
                            .uriVariables(goods.getId())
                            .monoList(ItemGoods.class)
                            .flatMapMany(Flux::fromIterable)
                            .flatMap(itemGoods -> WebClientHandler.build(ApiInfo.ITEM_GET)
                                    .uriVariables(itemGoods.getItemId())
                                    .mono(Item.class))
                            .collectList();

                    return itemListMono.map(items -> {

                        GoodsList.Response response = new GoodsList.Response();
                        response.setGoods(goods);
                        response.setItemList(items);

                        return response;
                    });
                });

        return ResponseHandler.ok(responseFlux.collectList());
    }

    public Mono<ServerResponse> itemList(ServerRequest request) {

        final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>(){{
            put("masterId", Collections.singletonList(request.pathVariable("masterId")));
        }};

        Flux<ItemList.Response> responseFlux = WebClientHandler.build(ApiInfo.ITEM_LIST)
                .queryParam(paramMap)
                .monoList(Item.class)
                .flatMapMany(Flux::fromIterable)
                .flatMap(item -> {

                    final Mono<List<Goods>> goodsMonoList = WebClientHandler.build(ApiInfo.ITEMGOODS_ITEMLIST)
                            .uriVariables(item.getId())
                            .monoList(ItemGoods.class)
                            .flatMapMany(Flux::fromIterable)
                            .flatMap(itemGoods -> WebClientHandler.build(ApiInfo.GOODS_GET)
                                    .uriVariables(itemGoods.getGoodsId())
                                    .mono(Goods.class))
                            .collectList();

                    return goodsMonoList.map(goods -> {

                        ItemList.Response response = new ItemList.Response();
                        response.setItem(item);
                        response.setGoodsList(goods);

                        return response;
                    });
                });

        return ResponseHandler.ok(responseFlux.collectList());
    }

    public Mono<ServerResponse> detail(ServerRequest request) {

        final Mono<List<Schedule>> scheduleListMono = request.queryParam("masterId")
                .map(Mono::just)
                .orElseGet(Mono::empty)
                .flatMap(masterId -> WebClientHandler.build(ApiInfo.SCHEDULE_GET)
                        .uriVariables(masterId)
                        .monoList(Schedule.class));

        final Mono<Goods> goodsMono = request.queryParam("goodsId")
                .map(Mono::just)
                .orElseGet(Mono::empty)
                .flatMap(goodsId -> WebClientHandler.build(ApiInfo.GOODS_GET)
                        .uriVariables(goodsId)
                        .mono(Goods.class));

        final Mono<Item> itemMono = request.queryParam("itemId")
                .map(Mono::just)
                .orElseGet(Mono::empty)
                .flatMap(itemId -> WebClientHandler.build(ApiInfo.ITEM_GET)
                        .uriVariables(itemId)
                        .mono(Item.class));

        final Mono<ReservationDetail.Response> responseMono = Mono.zip(scheduleListMono, goodsMono, itemMono)
                .flatMap(tuple3 -> {

                    ReservationDetail.Response response = new ReservationDetail.Response();

                    response.setMasterId(tuple3.getT2().getMasterId());
                    response.setGoodsId(tuple3.getT2().getId());
                    response.setItemId(tuple3.getT3().getId());

                    return Mono.just(response);
                });

        return ResponseHandler.noContent(Mono.empty());
    }

    public Mono<ServerResponse> post(ServerRequest request) {
        return null;
    }
}
