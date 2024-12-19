package ru.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.dto.compilation.CompilationDto;
import ru.dto.compilation.NewCompilationDto;
import ru.dto.event.EventShortDto;
import ru.model.Compilation;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pinned", expression = "java(setPinnedForNewDto(compilationDto))")
    public abstract Compilation toCompilation(NewCompilationDto compilationDto);

    @Mapping(target = "events", source = "shortEvents")
    public abstract CompilationDto toResponseDto(Compilation compilation, Set<EventShortDto> shortEvents);

    @Named("setPinnedForNewDto")
    boolean setPinnedForNewDto(NewCompilationDto compilationDto) {
        if (compilationDto.getPinned() != null) {
            return compilationDto.getPinned();
        }
        return false;
    }
}
