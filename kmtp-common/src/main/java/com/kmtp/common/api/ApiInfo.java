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

import com.kmtp.common.api.ServiceInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

/**
 * API 정보를 저장하는 enum class 입니다.
 * @author KYoung
 */
@Getter
@AllArgsConstructor
public enum ApiInfo {

    MASTER_GET(HttpMethod.GET, ServiceInfo.masterHost, ServiceInfo.masterPort, "/master/{id}"),
    MASTER_POST(HttpMethod.POST, ServiceInfo.masterHost, ServiceInfo.masterPort, "/master"),
    MASTER_PUT(HttpMethod.PUT, ServiceInfo.masterHost, ServiceInfo.masterPort, "/master/{id}"),
    MASTER_DELETE(HttpMethod.DELETE, ServiceInfo.masterHost, ServiceInfo.masterPort, "/master/{id}"),

    MEMBER_GET(HttpMethod.GET, ServiceInfo.masterHost, ServiceInfo.masterPort, "/member/{id}"),
    MEMBER_POST(HttpMethod.POST, ServiceInfo.masterHost, ServiceInfo.masterPort, "/member"),
    MEMBER_PUT(HttpMethod.PUT, ServiceInfo.masterHost, ServiceInfo.masterPort, "/member/{id}"),
    MEMBER_DELETE(HttpMethod.DELETE, ServiceInfo.masterHost, ServiceInfo.masterPort, "/member/{id}"),

    SCHEDULE_GET(HttpMethod.GET, ServiceInfo.masterHost, ServiceInfo.masterPort, "/schedule/{masterId}"),
    SCHEDULE_POST(HttpMethod.POST, ServiceInfo.masterHost, ServiceInfo.masterPort, "/schedule/{masterId}"),
    SCHEDULE_DELETE(HttpMethod.DELETE, ServiceInfo.masterHost, ServiceInfo.masterPort, "/schedule/{masterId}"),

    ITEM_LIST(HttpMethod.GET, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/item"),
    ITEM_GET(HttpMethod.GET, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/item/{id}"),
    ITEM_POST(HttpMethod.POST, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/item"),
    ITEM_PUT(HttpMethod.PUT, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/item/{id}"),
    ITEM_DELETE(HttpMethod.DELETE, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/item/{id}"),

    GOODS_LIST(HttpMethod.GET, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/goods"),
    GOODS_GET(HttpMethod.GET, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/goods/{id}"),
    GOODS_POST(HttpMethod.POST, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/goods"),
    GOODS_PUT(HttpMethod.PUT, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/goods/{id}"),
    GOODS_DELETE(HttpMethod.DELETE, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/goods/{id}"),

    ITEMGOODS_ITEMLIST(HttpMethod.GET, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/itemGoods/item/{itemId}"),
    ITEMGOODS_GOODSLIST(HttpMethod.GET, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/itemGoods/goods/{goodsId}"),
    ITEMGOODS_POST(HttpMethod.GET, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/itemGoods/{goodsId}"),
    ITEMGOODS_DELETE(HttpMethod.GET, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/itemGoods/{goodsId}"),

    RESERVATION_PERIOD_CHECK(HttpMethod.GET, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/reservation/period-check"),
    RESERVATION_DATE_CHECK(HttpMethod.GET, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/reservation/date-check"),
    RESERVATION_POST(HttpMethod.POST, ServiceInfo.reservationHost, ServiceInfo.reservationPort, "/reservation"),
    ;

    private final HttpMethod httpMethod;
    private final String host; 
    private final int port;
    private final String path;
}
