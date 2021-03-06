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
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Webflux Request helper class
 * @author KYoung
 */
public class RequestHandler {

    /**
     *
     * @param request {@link ServerRequest}
     * @param elementClass {@link Class}*
     * @param <T> POJO
     * @return {@link Mono}
     */
    public static <T> Mono<List<T>> jsonBodyToList(ServerRequest request, Class<T[]> elementClass) {

        return request.bodyToMono(String.class)
                .flatMap(jsonBody -> {
                    final Gson gson = new Gson();
                    final List<T> list = Arrays.asList( gson.fromJson(jsonBody, elementClass) );
                    return Mono.just(list);
                });
    }
}
