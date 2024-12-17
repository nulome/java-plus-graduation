package ru.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dto.event.EventFullDto;
import ru.dto.event.EventShortDto;

import java.util.Optional;
import java.util.Set;


@FeignClient(name = "events-server")
public interface EventClient {

    @GetMapping("/feign/events/{eventId}")
    Optional<EventFullDto> getEvent(@PathVariable Long eventId);

    @GetMapping("/feign/events/short")
    Set<EventShortDto> getEventsByIds(@RequestParam Set<Long> events);

    @GetMapping("/feign/events/exists/{catId}")
    boolean existsByCategoryFromEvent(@PathVariable Long catId);

}
