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

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;

public class ResponseHandler {

    private ResponseHandler() {
    }

    private static <T> List<?> setData(T data) {

        if (data instanceof List) {
            return (List<?>) data;
        } else {
            return List.of(data);
        }
    }

    public static <T> Mono<ServerResponse> ok(Mono<T> mono) {

        return mono.map(t -> HttpInfo.builder()
                        .timestamp(ZonedDateTime.now())
                        .message("success")
                        .data(setData(t))
                        .build())
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response, HttpInfo.class))
                .onErrorResume(ResponseErrorHandler::build);
    }
}
