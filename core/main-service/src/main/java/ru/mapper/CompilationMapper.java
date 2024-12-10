package ru.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.dto.compilation.CompilationDto;
import ru.dto.compilation.NewCompilationDto;
import ru.dto.event.EventShortDto;
import ru.model.Compilation;
import ru.model.Event;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CompilationMapper {

    @Autowired
    EventMapper eventMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    @Mapping(target = "pinned", expression = "java(setPinnedForNewDto(compilationDto))")
    public abstract Compilation toCompilation(NewCompilationDto compilationDto, Set<Event> events);

    @Mapping(target = "events", expression = "java(mapEventToShortDto(compilation.getEvents()))")
    public abstract CompilationDto toResponseDto(Compilation compilation);

    @Named("mapEventToShortDto")
    Set<EventShortDto> mapEventToShortDto(Set<Event> events) {
        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toSet());
    }

    @Named("setPinnedForNewDto")
    boolean setPinnedForNewDto(NewCompilationDto compilationDto) {
        if (compilationDto.getPinned() != null) {
            return compilationDto.getPinned();
        }
        return false;
    }
}
