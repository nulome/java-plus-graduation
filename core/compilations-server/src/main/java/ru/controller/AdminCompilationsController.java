package ru.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.dto.compilation.CompilationDto;
import ru.dto.compilation.NewCompilationDto;
import ru.dto.compilation.UpdateCompilationRequest;
import ru.service.CompilationService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Tag(name = "Admin: Подборки событий", description = "API для работы с подборками")
public class AdminCompilationsController {

    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание подборки")
    public CompilationDto adminChangeCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("Admin change compilation {}", compilationDto);
        return service.changeCompilation(compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление подборки")
    public void adminDeleteCompilation(@PathVariable Long compId) {
        log.info("Admin delete compilation id {}", compId);
        service.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @Operation(summary = "Изменение подборки")
    public CompilationDto adminPatchCompilation(@PathVariable Long compId,
                                                @RequestBody @Valid UpdateCompilationRequest compilationDto) {
        log.info("Admin delete compilation id {}", compId);
        return service.patchCompilation(compId, compilationDto);
    }

}
