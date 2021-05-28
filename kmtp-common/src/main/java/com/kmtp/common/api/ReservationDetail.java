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

import lombok.Data;

public class ReservationDetail {

    @Data
    public static class Response {

        private Long masterId;
        private Long itemId;
        private Long goodsId;

        private String masterName;
        private String itemName;
        private String goodsName;

        private Long charge;
        private Long discountCharge;
        private Schedule schedule;
    }
}
