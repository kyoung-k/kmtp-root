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

import java.util.List;

/**
 * 상품 기준 목록의 API POJO class 입니다.
 * @author KYoung
 */
public class GoodsList {

    @Data
    public static class Response {

        private Long id;
        private Long masterId;
        private String name;
        private Discount discount;
        private List<Item> itemList;

        public void setGoods(Goods goods) {
            this.id = goods.getId();
            this.masterId = goods.getMasterId();
            this.name = goods.getName();
            this.discount = goods.getDiscount();
        }
    }
}