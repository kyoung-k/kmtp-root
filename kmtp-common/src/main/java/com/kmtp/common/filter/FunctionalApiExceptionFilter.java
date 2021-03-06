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
package com.kmtp.common.filter;

import com.kmtp.common.http.error.ResponseErrorHandler;
import com.kmtp.common.validation.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

/**
 * Spring Webflux Functional Endpoint {@link RouterFunctions} 에서 사용하는 Exception Filter 입니다.
 * @author KYoung
 */
@Component
public class FunctionalApiExceptionFilter {

    public HandlerFilterFunction<ServerResponse, ServerResponse> exceptionHandler() {
        return (request, next) -> next.handle(request)
                .onErrorResume(ResponseStatusException.class, ResponseErrorHandler::build)
                .onErrorResume(ValidationException.class, ResponseErrorHandler::build);
    }
}
