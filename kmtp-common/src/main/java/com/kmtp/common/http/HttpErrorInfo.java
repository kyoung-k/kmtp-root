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

import com.kmtp.common.exception.ValidationException;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class HttpErrorInfo {

    private ZonedDateTime timestamp;
    private String message;
    private List<?> errors;

    /**
     * ResponseStatusException handler
     * @param responseStatusException
     * @return
     */
    public static Mono<ServerResponse> build(ResponseStatusException responseStatusException) {

        Mono<HttpInfo> mono = Mono.just(HttpInfo.builder()
                .timestamp(ZonedDateTime.now())
                .message(responseStatusException.getReason())
                .build());

        return ServerResponse.status(responseStatusException.getStatus().value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(mono, HttpInfo.class);
    }

    /**
     * Throwable handler
     * @param throwable
     * @return
     */
    public static Mono<ServerResponse> build(Throwable throwable) {

        if (throwable instanceof ResponseStatusException) {
            return build((ResponseStatusException) throwable);
        } else if (throwable instanceof ValidationException) {
            return HttpErrorInfo.build((ValidationException) throwable);
        }

        Mono<HttpInfo> mono = Mono.just(HttpInfo.builder()
                .timestamp(ZonedDateTime.now())
                .message("Internal Server Error")
                .build());

        return ServerResponse.status(500)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mono, HttpInfo.class);
    }

    public static Mono<ServerResponse> build(ValidationException validationException) {

        Mono<HttpErrorInfo> mono = Mono.just(HttpErrorInfo.builder()
                .timestamp(ZonedDateTime.now())
                .message(validationException.getMessage())
                .errors(validationException.getErrorsList())
                .build());

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mono, HttpInfo.class);

    }
}
