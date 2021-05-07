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
package com.kmtp.common.entity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericEntity <T> {

    public T change(Consumer<T> consumer) {
        consumer.accept((T) this);
        return (T) this;
    }

    /**
     * Functional Methods for Supporting {@link Mono} Permanence in Entity Objects
     * @param supplier
     * @return
     */
    public Mono<?> persistenceMono(Supplier<? extends Mono> supplier) {
        return supplier.get();
    }

    /**
     * Functional Methods for Supporting {@link Flux} Permanence in Entity Objects
     * @param supplier
     * @return
     */
    public Flux<?> persistenceFlux(Supplier<? extends Flux> supplier) {
        return supplier.get();
    }
}
