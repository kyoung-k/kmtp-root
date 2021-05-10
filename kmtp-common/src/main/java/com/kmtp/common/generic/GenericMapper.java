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

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GenericMapper <D, E> {

    D entityToApi(E entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    E apiToEntity(D api);

    List<D> entityListToApiList(List<E> entityList);

    List<E> apiListToEntityList(List<D> apiList);
}
