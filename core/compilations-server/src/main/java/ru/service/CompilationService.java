package ru.service;

import ru.dto.compilation.CompilationDto;
import ru.dto.compilation.NewCompilationDto;
import ru.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto changeCompilation(NewCompilationDto compilationDto);

    void deleteCompilation(Long compId);

    CompilationDto patchCompilation(Long compId, UpdateCompilationRequest compilationDto);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationsById(Long compId);

}
