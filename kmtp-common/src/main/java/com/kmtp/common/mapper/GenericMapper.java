package com.kmtp.common.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

public interface GenericMapper <D, E> {

    D entityToApi(E entity);

    @Mappings({
        @Mapping(target = "version", ignore = true)
    })
    E apiToEntity(D api);

    List<D> entityListToApiList(List<E> entityList);

    List<E> apiListToEntityList(List<D> apiList);
}
