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
package com.kmtp.common.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 아이템 POJO class 입니다.
 * @author KYoung
 */
@Data
@Builder
@AllArgsConstructor
public class Item {

    private Long id;
    @NotNull
    private Long masterId;
    @NotBlank
    private String name;
    @NotNull
    private Charge charge;
}
