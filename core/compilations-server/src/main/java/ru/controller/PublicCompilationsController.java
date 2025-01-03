package ru.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dto.compilation.CompilationDto;
import ru.service.CompilationService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Tag(name = "Public: Подборки событий", description = "Публичный API для работы с подборками")
public class PublicCompilationsController {

    private final CompilationService service;

    @GetMapping
    @Operation(summary = "Получение подборок событий")
    public List<CompilationDto> getCompilations(@RequestParam(value = "pinned", defaultValue = "false") Boolean pinned,
                                                @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                                @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        log.info("Get compilations with pinned={}, page-from={}, page-size={}", pinned, from, size);
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @Operation(summary = "Получение подборки по идентификатору")
    public CompilationDto getCompilationsById(@PathVariable(value = "compId") Long compId) {
        log.info("Get compilation By compId={}", compId);
        return service.getCompilationsById(compId);
    }
}
