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

import com.kmtp.common.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Collections;

public class ResponseErrorHandler {

    /**
     * ResponseStatusException build
     * @param responseStatusException 응답 에러
     * @return ServerResponse
     */
    public static Mono<ServerResponse> build(ResponseStatusException responseStatusException) {

        Mono<HttpErrorInfo> mono = Mono.just(HttpErrorInfo.builder()
                .timestamp(ZonedDateTime.now())
                .message(responseStatusException.getReason())
                .error(Collections.emptyList())
                .build());

        return ServerResponse.status(responseStatusException.getStatus().value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(mono, HttpInfo.class);
    }

    /**
     * ValidationException build
     * @param validationException 유효성 체크 에러
     * @return ServerResponse
     */
    public static Mono<ServerResponse> build(ValidationException validationException) {

        Mono<HttpErrorInfo> mono = Mono.just(HttpErrorInfo.builder()
                .timestamp(ZonedDateTime.now())
                .message(validationException.getMessage())
                .error(validationException.getErrorsList())
                .build());

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mono, HttpErrorInfo.class);

    }

    /**
     * Throwable build
     * @param throwable Throwable
     * @return ServerResponse
     */
    public static Mono<ServerResponse> build(Throwable throwable) {

        if (throwable instanceof ResponseStatusException) {
            return build((ResponseStatusException) throwable);
        } else if (throwable instanceof ValidationException) {
            return build((ValidationException) throwable);
        }

        throwable.printStackTrace();

        Mono<HttpErrorInfo> mono = Mono.just(HttpErrorInfo.builder()
                .timestamp(ZonedDateTime.now())
                .message("Internal Server Error")
                .error(Collections.emptyList())
                .build());

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mono, HttpErrorInfo.class);
    }
}
