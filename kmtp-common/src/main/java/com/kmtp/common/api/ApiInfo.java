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
import lombok.Getter;
import org.springframework.http.HttpMethod;

@Getter
@AllArgsConstructor
public enum ApiInfo {

    MASTER_GET(HttpMethod.GET, 8881, "/master/{id}"),
    MASTER_POST(HttpMethod.POST, 8881, "/master"),
    MASTER_PUT(HttpMethod.PUT, 8881, "/master/{id}"),
    MASTER_DELETE(HttpMethod.DELETE, 8881, "/master/{id}"),

    MEMBER_GET(HttpMethod.GET, 8881, "/member/{id}"),
    MEMBER_POST(HttpMethod.POST, 8881, "/member"),
    MEMBER_PUT(HttpMethod.PUT, 8881, "/member/{id}"),
    MEMBER_DELETE(HttpMethod.DELETE, 8881, "/member/{id}"),

    SCHEDULE_GET(HttpMethod.GET, 8881, "/schedule/{masterId}"),
    SCHEDULE_POST(HttpMethod.POST, 8881, "/schedule/{masterId}"),
    SCHEDULE_DELETE(HttpMethod.DELETE, 8881, "/schedule/{masterId}"),

    ITEM_LIST(HttpMethod.GET, 8882, "/item"),
    ITEM_GET(HttpMethod.GET, 8882, "/item/{id}"),
    ITEM_POST(HttpMethod.POST, 8882, "/item"),
    ITEM_PUT(HttpMethod.PUT, 8882, "/item/{id}"),
    ITEM_DELETE(HttpMethod.DELETE, 8882, "/item/{id}"),

    GOODS_LIST(HttpMethod.GET, 8882, "/goods"),
    GOODS_GET(HttpMethod.GET, 8882, "/goods/{id}"),
    GOODS_POST(HttpMethod.POST, 8882, "/goods"),
    GOODS_PUT(HttpMethod.PUT, 8882, "/goods/{id}"),
    GOODS_DELETE(HttpMethod.DELETE, 8882, "/goods/{id}"),

    ITEMGOODS_ITEMLIST(HttpMethod.GET, 8882, "/itemGoods/item/{itemId}"),
    ITEMGOODS_GOODSLIST(HttpMethod.GET, 8882, "/itemGoods/goods/{goodsId}"),
    ITEMGOODS_POST(HttpMethod.GET, 8882, "/itemGoods/{goodsId}"),
    ITEMGOODS_DELETE(HttpMethod.GET, 8882, "/itemGoods/{goodsId}"),

    RESERVATION_CHECK(HttpMethod.GET, 8882, "/reservation/check"),
    RESERVATION_POST(HttpMethod.POST, 8882, "/reservation"),
    ;

    private final HttpMethod httpMethod;
    private final int port;
    private final String path;
}
