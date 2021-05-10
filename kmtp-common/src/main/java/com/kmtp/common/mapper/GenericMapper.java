package com.kmtp.common.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import reactor.core.publisher.Flux;
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

    default Mono<D> entityMonoToApiMono(Mono<E> entityListMono) {
        return entityListMono
                .map(this::entityToApi);
    }

    default Mono<E> apiMonoToEntityMono(Mono<D> apiListMono) {
        return apiListMono
                .map(this::apiToEntity);
    }

    default Flux<D> entityFluxToApiFlux(Flux<E> entityFlux) {
        return entityFlux
                .map(this::entityToApi);
    }

    default Flux<E> apiFluxToEntityFlux(Flux<D> entityFlux) {
        return entityFlux
                .map(this::apiToEntity);
    }
}
