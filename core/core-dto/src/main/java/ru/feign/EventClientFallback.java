package ru.feign;

import ru.dto.event.EventFullDto;
import ru.dto.event.EventShortDto;
import ru.exception.FeignException;

import java.util.Optional;
import java.util.Set;

public class EventClientFallback implements EventClient {
    @Override
    public Optional<EventFullDto> getEvent(Long eventId) {
        throw new FeignException("Ошибка запроса через Feign EventClient");
    }

    @Override
    public Set<EventShortDto> getEventsByIds(Set<Long> events) {
        throw new FeignException("Ошибка запроса через Feign EventClient");
    }

    @Override
    public boolean existsByCategoryFromEvent(Long catId) {
        throw new FeignException("Ошибка запроса через Feign EventClient");
    }
}
