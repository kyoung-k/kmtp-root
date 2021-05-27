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
package com.kmtp.common.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kmtp.common.adaptor.ZonedDateTimeTypeAdaptor;
import com.kmtp.common.api.ApiInfo;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class WebClientHandler<T> {

    private WebClient.ResponseSpec webClient;

    private final ApiInfo apiInfo;
    private Object[] uriVariables;
    private MultiValueMap<String, String> queryParam;

    private WebClientHandler(ApiInfo apiInfo) {

        this.apiInfo = apiInfo;
    }

    public static WebClientHandler build(ApiInfo apiInfo) {
        return new WebClientHandler(apiInfo);
    }

    public WebClientHandler uriVariables(Object... uriVariables) {
        this.uriVariables = uriVariables;
        return this;
    }

    public WebClientHandler queryParam(MultiValueMap<String, String> multiValueMap) {
        this.queryParam = multiValueMap;
        return this;
    }

    public Mono<List<T>> exchange() {
        return MethodType.valueOf(this.apiInfo.getHttpMethod().name()).function.apply(this)
                .bodyToMono(String.class)
                .flatMap(jsonBody -> {
                    final HttpInfo<T> httpInfo = new GsonBuilder()
                            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdaptor())
                            .create()
                            .fromJson(jsonBody, HttpInfo.class);

                    return Mono.just(httpInfo.getData());
                });
    }

    private enum MethodType {
        GET(HttpMethod.GET, builder -> getWebClient()
                .get()
                .uri(uriBuilder -> createUri(uriBuilder, builder))
                .retrieve()
        ),
        ;
//        POST(HttpMethod.POST),
//        PUT(HttpMethod.PUT),
//        DELETE(HttpMethod.DELETE);

        MethodType(HttpMethod httpMethod, Function<WebClientHandler, WebClient.ResponseSpec> function) {
            this.httpMethod = httpMethod;
            this.function = function;
        }

        private final HttpMethod httpMethod;

        private final Function<WebClientHandler, WebClient.ResponseSpec> function;

        private final static WebClient getWebClient() {
            return WebClient.builder().baseUrl("http://localhost").build();
        }

        private final static URI createUri(UriBuilder uriBuilder, WebClientHandler webClientHandler) {

            uriBuilder.port(webClientHandler.apiInfo.getPort())
                    .path(webClientHandler.apiInfo.getPath());

            if (webClientHandler.queryParam != null) {
                uriBuilder.queryParams(webClientHandler.queryParam);
            }

            if (webClientHandler.uriVariables != null) {
                return uriBuilder.build(uriBuilder);
            } else {
                return uriBuilder.build();
            }
        }
    }
}
