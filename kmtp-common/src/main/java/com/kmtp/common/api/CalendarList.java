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

public class CalendarList {

    @Data
    @Builder
    public static class Response {

        private LocalDate date;
        private List<Item> itemList;
    }

    @Data
    @Builder
    public static class Calendar {
        private LocalDate date;
        private boolean isCalendar;
    }

    public static class CalendarBuilder {

        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDate calendarStartDate;
        private LocalDate calendarEndDate;
        private int monthDay;
        private Flux<CalendarList.Calendar> calendarFlux;

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

        public static Flux<CalendarList.Calendar> of(YearMonth yearMonth) {
            CalendarBuilder calendarBuilder = new CalendarBuilder(yearMonth);
            return calendarBuilder.calendarFlux;
        }
    }
}
