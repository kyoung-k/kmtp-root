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
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Calendar.DAY_OF_WEEK;

/**
 * 달력 목록 class 입니다.
 * @author KYoung
 */
public class CalendarList {

    /**
     * 달력 목록 Http Response 생성을 위한 class 입니다.
     */
    @Data
    @Builder
    public static class Response {

        private LocalDate date;
        private List<Item> itemList;
    }

    /**
     * 달력 정보 class 입니다.
     */
    @Data
    @Builder
    public static class Calendar {
        private LocalDate date;
        private boolean isCalendar;
    }

    /**
     * 달력 정보 목록을 생성하기 위한 builder class 입니다.
     */
    public static class CalendarBuilder {

        // 해당월의 시작일(1일)
        private LocalDate startDate;
        // 해당월의 종료일
        private LocalDate endDate;
        // 해당월의 달력 시작 일요일
        private LocalDate calendarStartDate;
        // 해당월의 달력 종료 토요일
        private LocalDate calendarEndDate;
        // 해당월의 총 일수
        private int monthDay;
        // 달력 정보 목록
        private Flux<CalendarList.Calendar> calendarFlux;

        /**
         * 달력 정보 목록 생성을 처리하는 생성자 입니다.
         * <p></p>
         * (1) 해당월의 시작일(1일)을 설정합니다.<br>
         * (2) 해당월의 종료일을 설정합니다.<br>
         * (3) 해당월의 달력 시작 일요일의 일자를 설정합니다.<br>
         * (4) 해당월의 달력 종료 토요일의 일자를 설정합니다.<br>
         * (5) 해당월의 총 일수를 설정합니다.<br>
         * (6) 달력 정보 목록을 설정합니다.<br>
         * <p></p>
         * @param yearMonth {@link YearMonth}
         */
        public CalendarBuilder(YearMonth yearMonth) {

            this.startDate = yearMonth.atDay(1);
            this.endDate = yearMonth.atEndOfMonth();

            this.calendarStartDate = startDate.plusDays((startDate.getDayOfWeek().getValue() % DAY_OF_WEEK) * -1);
            this.calendarEndDate = endDate.plusDays(DAY_OF_WEEK - (endDate.getDayOfWeek().getValue() % DAY_OF_WEEK) - 1);

            this.monthDay = (int) calendarStartDate.until(calendarEndDate.plusDays(1), ChronoUnit.DAYS);

            this.calendarFlux =
                    Flux.fromStream(Stream.iterate(calendarStartDate, localDate -> localDate.plusDays(1))
                            .map(localDate -> CalendarList.Calendar.builder()
                                    .date(localDate)
                                    .isCalendar(!localDate.isBefore(startDate) && localDate.isBefore(endDate.plusDays(1)))
                                    .build())
                            .limit(monthDay)
                    );
        }

        /**
         * 달력 정보 목록을 생성하고 리턴 합니다.
         * @param yearMonth {@link YearMonth}
         * @return {@link Flux}<{@link CalendarList.Calendar}></{@link>
         */
        public static Flux<CalendarList.Calendar> of(YearMonth yearMonth) {
            CalendarBuilder calendarBuilder = new CalendarBuilder(yearMonth);
            return calendarBuilder.calendarFlux;
        }
    }
}
