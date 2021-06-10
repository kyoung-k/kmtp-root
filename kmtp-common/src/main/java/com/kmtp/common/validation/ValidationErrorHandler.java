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
package com.kmtp.common.validation;

import com.kmtp.common.generic.GenericValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link GenericValidator}에서 발생한 유효성 검사 에러를 처리 합니다.
 * @author KYoung
 */
public class ValidationErrorHandler {

    /**
     * {@link ValidationError}를 생성하고 {@link ValidationException} 발생 시킵니다.
     * @param errors {@link Errors}
     */
    public static void build(Errors errors) {

        List<ValidationError> errorList = errors.getFieldErrors().stream()
                .map(ValidationErrorHandler::filedErrorToError)
                .collect(Collectors.toList());

        throw new ValidationException(errorList, "Validation Error.");
    }

    /**
     * {@link ValidationError}를 생성합니다.
     * @param fieldError {@link FieldError}
     * @return {@link ValidationError}
     */
    private static ValidationError filedErrorToError(FieldError fieldError) {
        return ValidationError.builder()
                .code(fieldError.getCode())
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }
}

