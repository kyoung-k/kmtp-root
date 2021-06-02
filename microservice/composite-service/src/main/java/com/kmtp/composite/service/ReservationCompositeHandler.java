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
import com.kmtp.common.generic.GenericError;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.common.http.WebClientHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReservationCompositeHandler {

    @Autowired
    public ReservationCompositeHandler() {
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

    public Mono<ServerResponse> calendarList(ServerRequest request) {

        final YearMonth yearMonth = request.queryParam("yearMonth")
                .map(YearMonth::parse)
                .orElseGet(YearMonth::now);

        return ResponseHandler.ok(Mono.empty());
    }

    public Mono<ServerResponse> detail(ServerRequest request) {

        final Long masterId = request.queryParam("masterId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param master-id is required."));

        final Long itemId = request.queryParam("itemId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param item-id is required."));

        final Long goodsId = request.queryParam("goodsId")
                .map(Long::parseLong)
                .orElse(0l);

        final String startDate = request.queryParam("startDate")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param start-date is required."));

        final String endDate = request.queryParam("endDate")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param end-date is required."));

        final Mono<Master> masterMono = WebClientHandler.build(ApiInfo.MASTER_GET)
                .uriVariables(masterId)
                .mono(Master.class)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master."));

        final Mono<Item> itemMono = WebClientHandler.build(ApiInfo.ITEM_GET)
                .uriVariables(itemId)
                .mono(Item.class)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item."));

        final Mono<Goods> goodsMono = WebClientHandler.build(ApiInfo.GOODS_GET)
                .uriVariables(goodsId)
                .mono(Goods.class);

        final Mono<ReservationDetail.Response> responseMono = Mono.zip(masterMono, goodsMono, itemMono)
                .flatMap(tuple3 -> {

                    final ReservationDetail.Response response = ReservationDetail.Response
                            .setDetail(tuple3.getT1(), tuple3.getT2(), tuple3.getT3());

                    final ReservationDetail.ScheduleCheck scheduleCheckRequest =
                            ReservationDetail.ScheduleCheck.builder()
                                    .masterId(masterId)
                                    .itemId(itemId)
                                    .startDate(startDate)
                                    .endDate(endDate)
                                    .build();

                    final Mono<List<ReservationDetail.ScheduleCheck>> checkListMono =
                            tuple3.getT1().getMasterType().getCheckFunction().apply(scheduleCheckRequest);

                    return Mono.zip(Mono.just(response), checkListMono);
                })
                .flatMap(tuple2 -> {

                    final List<Schedule> scheduleList = tuple2.getT1().getSchedule().stream()
                            .filter(schedule -> tuple2.getT2().stream().allMatch(check -> schedule.getId() != check.getScheduleId()))
                            .collect(Collectors.toList());

                    tuple2.getT1().setSchedule(Optional.of(scheduleList).orElse(Collections.emptyList()));

                    return Mono.just(tuple2.getT1());
                });

        return ResponseHandler.ok(responseMono);
    }
}
