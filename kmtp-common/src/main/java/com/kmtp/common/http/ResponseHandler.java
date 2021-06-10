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

import com.kmtp.common.http.error.ResponseErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Webflux Functional Endpoint Response helper class
 * @author KYoung
 */
public class ResponseHandler {

    private ResponseHandler() {
    }

    /**
     * 객체 인스턴스를 {@link List}로 캐스팅 합니다.
     * @param data {@link Object}
     * @param <T> POJO
     * @return {@link List}
     */
    private static <T> List<T> setData(T data) {

        if (data instanceof List) {
            return (List<T>) data;
        } else {
            return List.of(data);
        }
    }

    /**
     * {@link HttpStatus#OK} 상태로 응답 합니다.
     * @param mono {@link Mono}
     * @param <T> POJO
     * @return {@link Mono}<{@link ServerResponse}></{@link>
     */
    public static <T> Mono<ServerResponse> ok(Mono<? extends T> mono) {

        return mono.map(t -> Mono.just(HttpInfo.<T>builder()
                        .timestamp(ZonedDateTime.now())
                        .message("success")
                        .data(setData(t))
                        .build()))
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response, HttpInfo.class))
                .onErrorResume(ResponseErrorHandler::build);
    }

    /**
     * {@link HttpStatus#CREATED} 상태로 응답 합니다.
     * @param mono {@link Mono}
     * @param <T> POJO
     * @return {@link Mono}<{@link ServerResponse}></{@link>
     */
    public static <T> Mono<ServerResponse> created(Mono<? extends T> mono, URI uri) {

        return mono.flatMap(t -> ServerResponse.created(uri).build())
                .onErrorResume(ResponseErrorHandler::build);
    }

    /**
     * {@link HttpStatus#NO_CONTENT} 상태로 응답 합니다.
     * @param mono {@link Mono}
     * @param <T> POJO
     * @return {@link Mono}<{@link ServerResponse}></{@link>
     */
    public static <T> Mono<ServerResponse> noContent(Mono<? extends T> mono) {

        return mono.flatMap(t -> ServerResponse.noContent().build())
                .onErrorResume(ResponseErrorHandler::build);
    }
}
