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
package com.kmtp.common.generic;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Error Handling helper class 입니다.
 * @author KYoung
 */
public class GenericError {

    public static void error(HttpStatus httpStatus, String message) {
        throw new ResponseStatusException(httpStatus, message);
    }

    public static <T> Mono<? extends T> of(HttpStatus httpStatus, String message) {
        return Mono.error(new ResponseStatusException(httpStatus, message));
    }
}
