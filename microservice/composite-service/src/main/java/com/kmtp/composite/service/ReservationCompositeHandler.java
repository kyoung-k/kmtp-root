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
import com.kmtp.common.api.ApiInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReservationCompositeHandler {

    @Value("${app.master-service.host}")
    private String host;

    @Autowired
    public ReservationCompositeHandler() {
    }

    /**
     * 상품을 포함한 아이템 목록을 조회 합니다.
     * <p></p>
     * (1) master-id를 {@link MultiValueMap}으로 query parameter를 설정합니다.<br>
     * (2) {@link ApiInfo#ITEM_LIST} API를 요청하고 응답값을 {@link Item} 으로 변환합니다.<br>
     * {@link Flux} 타입으로 변합니다.<br>
     * (3) {@link ApiInfo#ITEMGOODS_ITEMLIST} API를 요청하고 응답값을 {@link ItemGoods} 으로 변환합니다.<br>
     * {@link Flux} 타입으로 변환하고 {@link ApiInfo#GOODS_GET} API를 요청하고 응답값을 {@link Goods} 으로 변환합니다.<br>
     * (4) 조회한 {@link Goods} 정보를 {@link ItemList.Response}에 설정하고 반환합니다.
     *
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link List}<{@link ItemList.Response}>></{@link>
     */
    public Mono<ServerResponse> itemList(ServerRequest request) {

        // (1)
        final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>(){{
            put("masterId", Collections.singletonList(request.pathVariable("masterId")));
        }};

        // (2)
        final Flux<ItemList.Response> responseFlux = WebClientHandler.build(ApiInfo.ITEM_LIST)
                .queryParam(paramMap)
                .monoList(Item.class)
                .flatMapMany(Flux::fromIterable)
                .flatMap(item -> {

                    // (3)
                    final Mono<List<Goods>> goodsMonoList = WebClientHandler.build(ApiInfo.ITEMGOODS_ITEMLIST)
                            .uriVariables(item.getId())
                            .monoList(ItemGoods.class)
                            .flatMapMany(Flux::fromIterable)
                            .flatMap(itemGoods -> WebClientHandler.build(ApiInfo.GOODS_GET)
                                    .uriVariables(itemGoods.getGoodsId())
                                    .mono(Goods.class))
                            .collectList();

                    // (4)
                    return goodsMonoList.map(goods -> {

                        ItemList.Response response = new ItemList.Response();
                        response.setItem(item);
                        response.setGoodsList(goods);

                        return response;
                    });
                });

        return ResponseHandler.ok(responseFlux.collectList());
    }

    /**
     * 아이템을 포함한 상품 목록을 조회 합니다.
     * <p></p>
     * (1) master-id를 {@link MultiValueMap}으로 query parameter를 설정합니다.<br>
     * (2) {@link ApiInfo#GOODS_LIST} API를 요청하고 응답값을 {@link Goods} 으로 변환합니다.<br>
     * {@link Flux} 타입으로 변합니다.<br>
     * (3) {@link ApiInfo#ITEMGOODS_GOODSLIST} API를 요청하고 응답값을 {@link ItemGoods} 으로 변환합니다.<br>
     * {@link Flux} 타입으로 변환하고 {@link ApiInfo#ITEM_GET} API를 요청하고 응답값을 {@link Item} 으로 변환합니다.<br>
     * (4) 조회한 {@link Item} 정보를 {@link GoodsList.Response}에 설정하고 반환합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link List}<{@link ItemList.Response}>></{@link>
     */
    public Mono<ServerResponse> goodsList(ServerRequest request) {

        // (1)
        final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>(){{
            put("masterId", Collections.singletonList(request.pathVariable("masterId")));
        }};

        // (2)
        final Flux<GoodsList.Response> responseFlux = WebClientHandler.build(ApiInfo.GOODS_LIST)
                .queryParam(paramMap)
                .monoList(Goods.class)
                .flatMapMany(Flux::fromIterable)
                .flatMap(goods -> {

                    // (3)
                    final Mono<List<Item>> itemListMono = WebClientHandler.build(ApiInfo.ITEMGOODS_GOODSLIST)
                            .uriVariables(goods.getId())
                            .monoList(ItemGoods.class)
                            .flatMapMany(Flux::fromIterable)
                            .flatMap(itemGoods -> WebClientHandler.build(ApiInfo.ITEM_GET)
                                    .uriVariables(itemGoods.getItemId())
                                    .mono(Item.class))
                            .collectList();

                    // (4)
                    return itemListMono.map(items -> {

                        GoodsList.Response response = new GoodsList.Response();
                        response.setGoods(goods);
                        response.setItemList(items);

                        return response;
                    });
                });

        return ResponseHandler.ok(responseFlux.collectList());
    }

    /**
     * YYYY-MM 값을 받아 해당 월의 달력으로 예약 가능한 아이템 목록을 조회합니다.
     * <p></p>
     * (1) master-id값을 path variable에서 조회합니다.<br>
     * (2) year-month값을 query parameter 에서 조회하고 {@link Long} 타입으로 변환합니다.<br>
     * year-month를 조회하지 못할 경우 현재 년,월로 설정합니다.<br>
     * (3) {@link CalendarList.CalendarBuilder}로 달력 목록을 생성합니다.<br>
     * (4) master-id를 {@link MultiValueMap}으로 query parameter를 설정합니다.<br>
     * (5) {@link ApiInfo#ITEM_LIST} API를 요청하고 응답값을 {@link Item} 으로 변환합니다.<br>
     * {@link Flux} 타입으로 변합니다. 예약확인 결과값으로 필터링 하고 {@link Item#getId()}값으로 오름차순 정렬 합니다.
     * (6) master-id, item-id, start-date를 {@link MultiValueMap}으로 query parameter를 설정합니다.<br>
     * (7) {@link ApiInfo#RESERVATION_DATE_CHECK} Api를 요청하고 응답값을
     * {@link ReservationDetail.ScheduleCheck} 으로 변환하고 {@link List#size()}값으로 변환합니다.<br>
     * (8) {@link CalendarList.Response}로 변환하고 {@link CalendarList.Response#getDate()} 값으로 오름차순 정렬 합니다.
     * <p></p>
     * @param request
     * @return {@link Mono}<{@link List}
     */
    public Mono<ServerResponse> calendarList(ServerRequest request) {

        // (1)
        final String masterId = request.pathVariable("masterId");

        // (2)
        final YearMonth yearMonth = request.queryParam("yearMonth")
                .map(YearMonth::parse)
                .orElseGet(YearMonth::now);

        // (3)
        final Mono<List<CalendarList.Response>> responseMono = CalendarList.CalendarBuilder.of(yearMonth)
                .flatMap(calendar -> {

                    // (4)
                    final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>() {{
                        put("masterId", Collections.singletonList(masterId));
                    }};

                    // (5)
                    final Mono<List<Item>> itemMonoList = WebClientHandler.build(ApiInfo.ITEM_LIST)
                            .queryParam(paramMap)
                            .monoList(Item.class)
                            .flatMapMany(Flux::fromIterable)
                            .flatMap(item -> {

                                // (6)
                                final MultiValueMap<String, String> dateCheckParamMap = new LinkedMultiValueMap<>() {{
                                    put("masterId", Collections.singletonList(masterId));
                                    put("itemId", Collections.singletonList(String.valueOf(item.getId())));
                                    put("startDate", Collections.singletonList(calendar.getDate().format(DateTimeFormatter.ISO_DATE)));
                                }};

                                // (7)
                                final Mono<Integer> countMono = WebClientHandler.build(ApiInfo.RESERVATION_DATE_CHECK)
                                        .queryParam(dateCheckParamMap)
                                        .monoList(ReservationDetail.ScheduleCheck.class)
                                        .map(List::size);

                                return Mono.zip(Mono.just(item), countMono);
                            })
                            .filter(tuple2 -> tuple2.getT2() <= 0)
                            .flatMap(tuple2 -> Mono.just(tuple2.getT1()))
                            .collectSortedList(Comparator.comparingLong(Item::getId));

                    return Mono.zip(Mono.just(calendar.getDate()), itemMonoList);
                })

                // (8)
                .flatMap(tuple2 -> Mono.just(CalendarList.Response
                        .builder()
                        .date(tuple2.getT1())
                        .itemList(tuple2.getT2())
                        .build()))
                .collectSortedList(Comparator.comparing(CalendarList.Response::getDate));

        return ResponseHandler.ok(responseMono);
    }

    /**
     * 예약하기 상세 화면을 조회합니다.
     * <p></p>
     * (1) master-id를 query parameter 에서 조회하고 {@link Long} 타입으로 변환합니다.<br>
     * master-id를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (2) item-id를 query parameter 에서 조회하고 {@link Long} 타입으로 변환합니다.<br>
     * master-id를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (3) goods-id를 query parameter 에서 조회하고 {@link Long} 타입으로 변환합니다.<br>
     * master-id를 조회하지 못할 경우 0l값으로 초기화 합니다.<br>
     * (4) start-date를 query parameter 에서 조회하고<br>
     * start-date를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (5) end-date를 query parameter 에서 조회하고<br>
     * end-date를 조회하지 못할 경우 {@link HttpStatus#BAD_REQUEST} 상태로 응답합니다.<br>
     * (6) {@link ApiInfo#MASTER_GET} API를 요청하고 {@link Master}로 변환합니다.<br>
     * 마스터 정보를 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (7) {@link ApiInfo#ITEM_GET} API를 요청하고 {@link Item}로 변환합니다.<br>
     * 아이템 정보를 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (8) {@link ApiInfo#GOODS_GET} API를 요청하고 {@link Goods}로 변환합니다.<br>
     * 상품 정보를 조회하지 못할 경우 빈 상품 정보를 생성합니다.<br>
     * (9) 마스터, 아이템, 상품를 {@link Mono#zip} 해서 merge합니다.<br>
     * (10) {@link ReservationDetail.Response}에 마스터, 아이템, 상품 정보를 설정합니다.<br>
     * (11) 예약 확인을 위해 {@link ReservationDetail.ScheduleCheck}를 설정합니다.<br>
     * (12) {@link MasterType}에 정의된 타입별 예약확인 {@link FunctionalInterface}를 실행해서 예약정보를 조회합니다.<br>
     * (13) (10)에서 설정된 {@link Schedule}정보를 예약확인에서 조회된 {@link Schedule} 정보로 필터링 합니다.<br>
     * (14) 필터링된 예약 가능한 {@link Schedule}정보를 다시 설정합니다.
     * <p></p>
     * TODO : (3)에서 설정된 0l값으로 조회하면 {@link HttpStatus#NOT_FOUND} 상태로 오류처리가 됨 {@link WebClientHandler}로 API를 요청할때
     * {@link Predicate}를 사용해서 요청하는 방식으로 수정하거나 다른 함수형 프로그래밍 방법을 고민하자.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link ReservationDetail.Response}></{@link>
     */
    public Mono<ServerResponse> detail(ServerRequest request) {

        // (1)
        final Long masterId = request.queryParam("masterId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param master-id is required."));

        // (2)
        final Long itemId = request.queryParam("itemId")
                .map(Long::parseLong)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param item-id is required."));

        // (3)
        final Long goodsId = request.queryParam("goodsId")
                .map(Long::parseLong)
                .orElse(0l);

        // (4)
        final String startDate = request.queryParam("startDate")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param start-date is required."));

        // (5)
        final String endDate = request.queryParam("endDate")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "query param end-date is required."));

        // (6)
        final Mono<Master> masterMono = WebClientHandler.build(ApiInfo.MASTER_GET)
                .uriVariables(masterId)
                .mono(Master.class)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found master."));

        // (7)
        final Mono<Item> itemMono = WebClientHandler.build(ApiInfo.ITEM_GET)
                .uriVariables(itemId)
                .mono(Item.class)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item."));

        // (8)
        final Mono<Goods> goodsMono = WebClientHandler.build(ApiInfo.GOODS_GET)
                .uriVariables(goodsId)
                .mono(Goods.class)
                .onErrorResume(throwable -> Mono.just(new Goods()));

        // (9)
        final Mono<ReservationDetail.Response> responseMono = Mono.zip(masterMono, goodsMono, itemMono)
                .flatMap(tuple3 -> {

                    // (10)
                    final ReservationDetail.Response response = ReservationDetail.Response
                            .setDetail(tuple3.getT1(), tuple3.getT2(), tuple3.getT3());

                    // (11)
                    final ReservationDetail.ScheduleCheck scheduleCheckRequest =
                            ReservationDetail.ScheduleCheck.builder()
                                    .masterId(masterId)
                                    .itemId(itemId)
                                    .startDate(startDate)
                                    .endDate(endDate)
                                    .build();

                    // (12)
                    final Mono<List<ReservationDetail.ScheduleCheck>> checkListMono =
                            tuple3.getT1().getMasterType().getCheckFunction().apply(scheduleCheckRequest);

                    return Mono.zip(Mono.just(response), checkListMono);
                })
                .flatMap(tuple2 -> {

                    // (13)
                    final List<Schedule> scheduleList = tuple2.getT1().getSchedule().stream()
                            .filter(schedule -> tuple2.getT2().stream().allMatch(check -> schedule.getId() != check.getScheduleId()))
                            .collect(Collectors.toList());

                    // (14)
                    tuple2.getT1().setSchedule(Optional.of(scheduleList).orElse(Collections.emptyList()));

                    return Mono.just(tuple2.getT1());
                });

        return ResponseHandler.ok(responseMono);
    }
}
