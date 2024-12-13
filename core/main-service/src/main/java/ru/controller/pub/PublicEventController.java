package ru.controller.pub;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dto.StatsSaveRequestDto;
import ru.dto.event.EventFullDto;
import ru.dto.event.EventShortDto;
import ru.related.EventParam;
import ru.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Tag(name = "Public: События", description = "Публичный API для работы с событиями")
public class PublicEventController {

    private final EventService service;

    @GetMapping
    @Operation(summary = "Получение событий с возможностью фильтрации")
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("Получение событий");
        StatsSaveRequestDto statsSaveRequestDto = new StatsSaveRequestDto("emv-main-service",
                request.getServletPath(), request.getRemoteAddr(),
                LocalDateTime.now().withNano(0));
        EventParam param = new EventParam(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size);
        return service.getEvents(param, statsSaveRequestDto);
    }

    @GetMapping("/{id}")
    @Transactional
    @Operation(summary = "Получение подробной информации о событии по идентификатору")
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Получение события id = {}", id);
        StatsSaveRequestDto statsSaveRequestDto = new StatsSaveRequestDto("emv-main-service",
                request.getServletPath(), request.getRemoteAddr(),
                LocalDateTime.now().withNano(0));
        return service.getEventById(id, statsSaveRequestDto);
    }
}
