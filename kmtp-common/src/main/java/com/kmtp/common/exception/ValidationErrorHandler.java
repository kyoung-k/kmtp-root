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
package com.kmtp.common.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationErrorHandler {

    @Data
    @Builder
    static class Error {

        private String code;
        private String field;
        private String message;
    }

    public static void handle(Errors errors) {

        List<Error> errorList = errors.getFieldErrors().stream()
                .map(ValidationErrorHandler::filedErrorToError)
                .collect(Collectors.toList());

        throw new ValidationException(errorList, "Kmtp Validation Error.");
    }

    private static Error filedErrorToError(FieldError fieldError) {
        return Error.builder()
                .code(fieldError.getCode())
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }
}

