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

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kmtp.common.adaptor.ZonedDateTimeTypeAdaptor;
import com.kmtp.common.api.ApiInfo;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WebClientHandler {

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

    public <T> Mono<T> mono(Class<T> clazz) {
        return this.exchange(clazz)
                .map(httpInfo -> httpInfo.getData()
                        .parallelStream()
                        .map(clazz::cast)
                        .collect(Collectors.toList()).get(0));
    }

    public <T> Mono<List<T>> monoList(Class<T> clazz) {
        return this.exchange(clazz)
                .map(httpInfo -> httpInfo.getData()
                        .parallelStream()
                        .map(clazz::cast)
                        .collect(Collectors.toList()));
    }

    public Mono<String> jsonString() {
        return MethodType.valueOf(this.apiInfo.getHttpMethod().name())
                .function.apply(this)
                .bodyToMono(String.class);
    }

    private <T> Mono<HttpInfo<T>> exchange(Class<T> clazz) {
        return MethodType.valueOf(this.apiInfo.getHttpMethod().name())
                .function.apply(this)
                .bodyToMono(String.class)
                .map(jsonBody -> new GsonBuilder()
                        .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdaptor())
                        .create()
                        .fromJson(jsonBody, TypeToken.getParameterized(HttpInfo.class, clazz).getType()));
    }

    private enum MethodType {
        GET(webClientHandler -> getWebClient()
                .method(webClientHandler.apiInfo.getHttpMethod())
                .uri(uriBuilder -> createUri(uriBuilder, webClientHandler))
                .retrieve()
        ),
        ;
//        POST(HttpMethod.POST),
//        PUT(HttpMethod.PUT),
//        DELETE(HttpMethod.DELETE);

        MethodType(Function<WebClientHandler, WebClient.ResponseSpec> function) {
            this.function = function;
        }

        private final Function<WebClientHandler, WebClient.ResponseSpec> function;

        private static WebClient getWebClient() {
            return WebClient.builder().baseUrl("http://localhost").build();
        }

        private static URI createUri(UriBuilder uriBuilder, WebClientHandler webClientHandler) {

            uriBuilder.port(webClientHandler.apiInfo.getPort())
                    .path(webClientHandler.apiInfo.getPath());

            if (webClientHandler.queryParam != null) {
                uriBuilder.queryParams(webClientHandler.queryParam);
            }

            if (webClientHandler.uriVariables != null) {
                return uriBuilder.build(webClientHandler.uriVariables);
            } else {
                return uriBuilder.build();
            }
        }
    }
}
