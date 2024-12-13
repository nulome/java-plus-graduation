package ru.controller.adm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dto.event.EventFullDto;
import ru.dto.event.UpdateEventAdminRequest;
import ru.related.EventSearchParam;
import ru.related.EventState;
import ru.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Tag(name = "Admin: События", description = "API для работы с событиями")
public class AdminEventController {
    private final EventService service;

    @GetMapping
    @Operation(summary = "Поиск событий",
            description = "Возвращает полную информацию обо всех событиях подходящих под переданные условия")
    public List<EventFullDto> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("Admin search events");
        EventSearchParam param = new EventSearchParam(users, states, categories, rangeStart, rangeEnd, from, size);
        return service.searchEvents(param);
    }

    @PatchMapping("/{eventId}")
    @Transactional
    @Operation(summary = "Редактирование события", description = "Редактирование данных любого события администратором")
    public EventFullDto adminChangeEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventAdminRequest eventDto) {
        log.info("Admin change event {} eventId = {}", eventDto, eventId);
        return service.changeEvent(eventId, eventDto);
    }
}
