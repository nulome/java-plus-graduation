package ru.service;

import ru.dto.StatsSaveRequestDto;
import ru.dto.event.EventFullDto;
import ru.dto.event.EventShortDto;
import ru.dto.event.NewEventDto;
import ru.dto.event.UpdateEventAdminRequest;
import ru.dto.event.UpdateUserEventRequest;
import ru.related.EventParam;
import ru.related.EventSearchParam;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventService {
    List<EventShortDto> getEventsForUser(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto eventDto);

    EventFullDto getEventByIdForUser(Long userId, Long eventId);

    EventFullDto changeEvent(Long userId, Long eventId, UpdateUserEventRequest eventDto);

    EventFullDto changeEvent(Long eventId, UpdateEventAdminRequest eventDto);

    List<EventFullDto> searchEvents(EventSearchParam param);

    List<EventShortDto> getEvents(EventParam param, StatsSaveRequestDto statsSaveRequestDto);

    EventFullDto getEventById(Long id, StatsSaveRequestDto statsSaveRequestDto);

    List<EventShortDto> getFollowEvent(Long userId);

    Optional<EventFullDto> getEvent(Long eventId);

    Set<EventShortDto> getEventsByIds(Set<Long> events);

    boolean existsByCategoryFromEvent(Long catId);

}
