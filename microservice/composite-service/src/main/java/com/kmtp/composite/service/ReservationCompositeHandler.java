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

import com.kmtp.common.api.Goods;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.HttpInfo;
import com.kmtp.common.http.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class ReservationCompositeHandler {

    final private GenericValidator genericValidator;
    final private WebClient webClient;

    @Autowired
    public ReservationCompositeHandler(GenericValidator genericValidator, WebClient.Builder webClient) {
        this.genericValidator = genericValidator;
        this.webClient = webClient.build();
    }

    public Mono<ServerResponse> goodsList(ServerRequest request) {

        final Long masterId = Long.parseLong(request.pathVariable("masterId"));

        Mono<List<Goods>> listMono = webClient.get()
                .uri("http://localhost:8882/goods?masterId={masterId}", masterId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<HttpInfo<Goods>>(){})
                .map(goodsHttpInfo -> goodsHttpInfo.getData());

        return ResponseHandler.ok(Mono.empty());
    }

    public Mono<ServerResponse> itemList(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> detail(ServerRequest request) {
        return null;
    }

    public Mono<ServerResponse> post(ServerRequest request) {
        return null;
    }
}
