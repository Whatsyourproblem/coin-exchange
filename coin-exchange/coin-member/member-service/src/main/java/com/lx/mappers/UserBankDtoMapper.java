package com.lx.mappers;

import com.lx.domain.UserBank;
import com.lx.dto.UserBankDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserBankDtoMapper {

    UserBankDtoMapper INSTANCE = Mappers.getMapper(UserBankDtoMapper.class);

    /*
    *  userBankDto -> UserBank
    * */
    UserBank toConvertEntity(UserBankDto source);

    /*
    *  UserBank -> UserBankDto
    * */
    UserBankDto toConvertDto(UserBank source);

    /*
    *  集合转换
    * */
    List<UserBank> toConvertEntity(List<UserBankDto> source);

    List<UserBankDto> toConvertDto(List<UserBank> source);


}
