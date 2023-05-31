package com.lx.mappers;

import com.lx.domain.Market;
import com.lx.dto.MarketDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MarketDtoMappers {

    MarketDtoMappers INSTANCE = Mappers.getMapper(MarketDtoMappers.class);

    Market toConvertEntity(MarketDto source);

    MarketDto toConvertDto(Market source);

    List<Market> toConvertEntity(List<MarketDto> source);

    List<MarketDto> toConvertDto(List<Market> source);

}
