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
package com.kmtp.common.generic;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class GenericEntity <T> {

    /**
     * Functions used in PUT API
     * @param consumer consumer
     * @return generic T
     */
    public T change(Consumer<T> consumer) {
        consumer.accept((T) this);
        return (T) this;
    }

    /**
     * Functional Methods for Supporting {@link Mono} Permanence in Entity Objects
     * @param supplier r2dbc를 실행하는 supplier
     * @return repository result {@link Mono}
     */
    public <S extends T> Mono<S> persistenceMono(Supplier<? extends Mono<S>> supplier) {

        return supplier.get();
    }

    /**
     * Functional Methods for Supporting {@link Flux} Permanence in Entity Objects
     * @param supplier r2dbc를 실행하는 supplier
     * @return repository result {@link Flux}
     */
    public <S extends T> Flux<S> persistenceFlux(Supplier<? extends Flux<S>> supplier) {
        return supplier.get();
    }
}
