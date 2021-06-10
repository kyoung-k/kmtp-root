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

import com.kmtp.common.http.WebClientHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 마스터 타입 enum class 입니다.<br>
 *
 * <p>
 * CONSECUTIVE :
 * 1일 이상 예약이 가능한 타입 입니다.<br>
 * 예약기간으로 예약확인 처리를 {@link FunctionalInterface}로 정의합니다.
 * </p>
 *
 * <p>
 * NONCONSECUTIVE :
 * 1일 이상 예약이 불가능한 타입 입니다.<br>
 * 예약날짜로 예약확인 처리를 {@link FunctionalInterface}로 정의합니다.
 * </p>
 */
@Getter
@AllArgsConstructor
public enum MasterType {

    CONSECUTIVE(
            param -> {

                MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

                multiValueMap.put("masterId", Collections.singletonList(String.valueOf(param.getMasterId())));
                multiValueMap.put("itemId", Collections.singletonList(String.valueOf(param.getItemId())));
                multiValueMap.put("startDate", Collections.singletonList(param.getStartDate()));
                multiValueMap.put("endDate", Collections.singletonList(param.getEndDate()));

                return WebClientHandler.build(ApiInfo.RESERVATION_PERIOD_CHECK)
                        .queryParam(multiValueMap)
                        .monoList(ReservationDetail.ScheduleCheck.class);
            }
    ),
    NONCONSECUTIVE(
            param -> {

                MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

                multiValueMap.put("masterId", Collections.singletonList(String.valueOf(param.getMasterId())));
                multiValueMap.put("itemId", Collections.singletonList(String.valueOf(param.getItemId())));
                multiValueMap.put("startDate", Collections.singletonList(param.getStartDate()));

                return WebClientHandler.build(ApiInfo.RESERVATION_DATE_CHECK)
                        .queryParam(multiValueMap)
                        .monoList(ReservationDetail.ScheduleCheck.class);
            }
    )
    ;

    // 예약확인 처리 function
    private Function<ReservationDetail.ScheduleCheck, Mono<List<ReservationDetail.ScheduleCheck>>> checkFunction;
}
