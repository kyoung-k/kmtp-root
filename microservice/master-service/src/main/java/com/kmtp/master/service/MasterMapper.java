package com.kmtp.master.service;

import com.kmtp.common.mapper.GenericMapper;
import com.kmtp.master.persistence.MasterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MasterMapper extends GenericMapper<Master, MasterEntity> {

    MasterMapper INSTANCE = Mappers.getMapper( MasterMapper.class );
}
