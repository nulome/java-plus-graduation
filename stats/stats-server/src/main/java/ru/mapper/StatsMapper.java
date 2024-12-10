package ru.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.dto.StatCountHitsDto;
import ru.dto.StatCountHitsResponseDto;
import ru.dto.StatDto;
import ru.dto.StatsResponseHitDto;
import ru.dto.StatsSaveRequestDto;
import ru.model.Stat;

@Mapper(componentModel = "spring")
public abstract class StatsMapper {

    @Mapping(target = "id", ignore = true)
    public abstract StatDto toStatDto(StatsSaveRequestDto saveRequestDto);

    public abstract Stat toStat(StatDto statDto);

    public abstract StatsResponseHitDto toResponseDto(Stat stat);

    public abstract StatCountHitsResponseDto toCountHitsResponseDto(StatCountHitsDto countHitsDto);


}
