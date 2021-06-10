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

import java.util.List;

/**
 * 유효성 검사 Response처리를 위한 사용자 정의 Exception 입니다.
 * @author KYoung
 */
public class ValidationException extends RuntimeException {

    final private List<ValidationError> errorsList;

    public ValidationException(List<ValidationError> errorsList, String message) {
        super(message);
        this.errorsList = errorsList;
    }

    public List<ValidationError> getErrorsList() {
        return errorsList;
    }
}
