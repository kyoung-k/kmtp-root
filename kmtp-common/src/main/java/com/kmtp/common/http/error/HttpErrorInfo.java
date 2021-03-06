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
package com.kmtp.common.http.error;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * HTTP Error Response POJO
 * <p>
 *     기본 에러 응답 템플릿 입니다.
 * </p>
 * @author KYoung
 */
@Data
@Builder
public class HttpErrorInfo {

    private ZonedDateTime timestamp;
    private String message;
    private List<?> error;
}
