package ru.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.dto.event.EventFullDto;
import ru.dto.event.EventShortDto;
import ru.dto.event.NewEventDto;
import ru.dto.event.UpdateUserEventRequest;
import ru.service.EventService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Tag(name = "Private: События", description = "Закрытый API для работы с событиями")
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    @Operation(summary = "Получение событий пользователем")
    public List<EventShortDto> getEventsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get events userId = {}, from = {}, size = {}", userId, from, size);
        return eventService.getEventsForUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание события",
            description = "Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента")
    public EventFullDto createEvent(
            @PathVariable Long userId,
            @RequestBody @Valid NewEventDto eventDto) {
        log.info("Create event {} userId = {}", eventDto, userId);
        return eventService.createEvent(userId, eventDto);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Получение события, добавленного пользователем")
    public EventFullDto getEventByIdForUser(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("Get event userId = {}, eventId = {}", userId, eventId);
        return eventService.getEventByIdForUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @Operation(summary = "Изменение события, добавленного пользователем")
    public EventFullDto userChangeEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateUserEventRequest eventDto) {
        log.info("Patch User change event {} userId = {}, eventId = {}", eventDto, userId, eventId);
        return eventService.changeEvent(userId, eventId, eventDto);
    }

    @GetMapping("/follow")
    @Operation(summary = "Получение событий пользователем, созданных пользователями, на которых он подписан")
    public List<EventShortDto> getFollowEvents(@PathVariable Long userId) {
        return eventService.getFollowEvent(userId);
    }
}
