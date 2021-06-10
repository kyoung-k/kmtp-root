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

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 예약하기 상세 API POJO class 입니다.
 * @author KYoung
 */
public class ReservationDetail {

    /**
     * 예약확인 POJO class 입니다.
     */
    @Data
    @Builder
    public static class ScheduleCheck {
        private Long masterId;
        private Long itemId;
        private Long scheduleId;
        private String startDate;
        private String endDate;
    }

    /**
     * 예약하기 상세 API Response POJO class 입니다.
     */
    @Data
    public static class Response {

        private Long masterId;
        private Long itemId;
        private Long goodsId;

        private MasterType masterType;
        private String masterName;
        private String itemName;
        private String goodsName;

        private Long charge;
        private Long discountCharge;
        private List<Schedule> schedule;

        /**
         * {@link Master}, {@link Goods}, {@link Item} 정보를 설정합니다.
         *
         * @param master {@link Master}
         * @param goods {@link Goods}
         * @param item {@link Item}
         * @return {@link ReservationDetail.Response}
         */
        public static ReservationDetail.Response setDetail(Master master, Goods goods, Item item) {

            ReservationDetail.Response response = new ReservationDetail.Response();

            response.setMasterId(master.getId());
            response.setItemId(item.getId());
            response.setGoodsId(goods.getId());

            response.setMasterType(master.getMasterType());
            response.setMasterName(master.getName());
            response.setItemName(item.getName());
            response.setGoodsName(goods.getName());

            response.setCharge(item.getCharge().getCharge());
            response.setDiscountCharge((long) (item.getCharge().getCharge() - (item.getCharge().getCharge() * (goods.getDiscount() == null ? 0 : goods.getDiscount().getDiscount()))));
            response.setSchedule(master.getScheduleList());

            return response;
        }
    }
}
