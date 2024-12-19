package ru.dto.compilation;

import lombok.Data;
import ru.dto.event.EventShortDto;

import java.util.Set;

@Data
public class CompilationDto {
    private Long id;
    private Set<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
