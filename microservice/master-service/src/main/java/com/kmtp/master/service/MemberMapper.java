package com.kmtp.master.service;

import com.kmtp.common.mapper.GenericMapper;
import com.kmtp.master.persistence.MemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberMapper extends GenericMapper<Member, MemberEntity> {

    MemberMapper INSTANCE = Mappers.getMapper( MemberMapper.class );
}
