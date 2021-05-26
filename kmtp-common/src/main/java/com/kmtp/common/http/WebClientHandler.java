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

import com.kmtp.common.api.ApiInfo;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Function;

public class WebClientHandler {

    private WebClient.ResponseSpec webClient;

    private WebClientHandler(Builder builder) {

        this.webClient = MethodType.valueOf(builder.apiInfo.getHttpMethod().name()).function.apply(builder);
    }

    private enum MethodType {
        GET(HttpMethod.GET, builder -> getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder.port(builder.apiInfo.getPort())
                        .path(builder.apiInfo.getPath())
                        .queryParams(builder.queryParam)
                        .build(builder.uriVariables))
                .retrieve()
        ),
        ;
//        POST(HttpMethod.POST),
//        PUT(HttpMethod.PUT),
//        DELETE(HttpMethod.DELETE);

        MethodType(HttpMethod httpMethod, Function<Builder, WebClient.ResponseSpec> function) {
            this.httpMethod = httpMethod;
            this.function = function;
        }

        private final HttpMethod httpMethod;
        private final Function<Builder, WebClient.ResponseSpec> function;
        private final static WebClient getWebClient() {
            return WebClient.builder().baseUrl("http://localhost").build();
        }
    }

    public static class Builder {

        private final ApiInfo apiInfo;
        private Object[] uriVariables;
        private MultiValueMap<String, String> queryParam;

        public Builder(ApiInfo apiInfo) {
            this.apiInfo = apiInfo;
        }

        public Builder uriVariables(Object... uriVariables) {
            this.uriVariables = uriVariables;
            return this;
        }

        public Builder queryParam(MultiValueMap<String, String> multiValueMap) {
            this.queryParam = queryParam;
            return this;
        }

        public WebClientHandler build() {
            return new WebClientHandler(this);
        }
    }
}
