package com.kmtp.master.service;

import com.kmtp.common.generic.GenericMapper;
import com.kmtp.common.api.Schedule;
import com.kmtp.master.persistence.ScheduleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ScheduleMapper extends GenericMapper<Schedule, ScheduleEntity> {

    ScheduleMapper INSTANCE = Mappers.getMapper( ScheduleMapper.class );
}
