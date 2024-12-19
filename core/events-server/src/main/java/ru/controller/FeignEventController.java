package ru.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.dto.event.EventFullDto;
import ru.dto.event.EventShortDto;
import ru.service.EventService;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feign/events")
public class FeignEventController {

    private final EventService service;

    @GetMapping("/{eventId}")
    public Optional<EventFullDto> getEvent(@PathVariable Long eventId) {
        log.info("Запрос feign на получение EventFullDto. eventId = {}", eventId);
        return service.getEvent(eventId);
    }

    @GetMapping("/short")
    public Set<EventShortDto> getEventsByIds(@RequestParam Set<Long> events) {
        log.info("Запрос feign на получение EventsByIds. events = {}", events);
        return service.getEventsByIds(events);
    }

    @GetMapping("/exists/{catId}")
    public boolean existsByCategoryFromEvent(@PathVariable Long catId) {
        log.info("Запрос feign на проверку привязки категории. catId = {}", catId);
        return service.existsByCategoryFromEvent(catId);
    }

}
