package com.lx.mappers;

import com.lx.domain.User;
import com.lx.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/*
*  该mappers目的是为了 entity(对象实体)和Dto(数据传输对象)的高效转换，并不是和数据库对象的转换的通常的mapper
*   用来做对象的映射转换
*   entity -> Dto or Dto -> entity
* */
@Mapper(componentModel = "spring") // org.mapstruct.Mapper 注意别弄错了
public interface UserDtoMapper {

    // 获取该对象的实例
    UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

    /*
    *  将entity转化为Dto
    * */
    UserDto convert2Dto(User source);

    /*
    *  将Dto对象转化为entity对象
    * */
    User convert2Entity(UserDto source);

    /*
     *  将entity集合转化为Dto集合
     * */
    List<UserDto> convert2Dto(List<User> source);

    /*
     *  将Dto集合转化为entity集合
     * */
    List<User> convert2Entity(List<UserDto> source);
}
