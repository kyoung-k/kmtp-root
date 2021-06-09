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
package com.kmtp.master.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ScheduleRepository extends ReactiveCrudRepository<ScheduleEntity, Long> {

    /**
     * 마스터의 스케쥴 목록을 조회합니다.
     * @param masterId 마스터 ID
     * @return {@link Flux}<{@link ScheduleEntity}></{@link>
     */
    Flux<ScheduleEntity> findByMasterId(Long masterId);

    /**
     * 마스터의 스케쥴 목록을 삭제합니다.
     * @param masterId 마스터 ID
     * @return {@link Mono}<{@link Void}></{@link>
     */
    Mono<Void> deleteByMasterId(Long masterId);
}
