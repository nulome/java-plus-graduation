package ru.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.client.StatsClient;
import ru.dto.StatCountHitsResponseDto;
import ru.dto.StatsSaveRequestDto;
import ru.dto.category.CategoryDto;
import ru.dto.event.EventFullDto;
import ru.dto.event.EventShortDto;
import ru.dto.event.NewEventDto;
import ru.dto.event.UpdateEventAdminRequest;
import ru.dto.event.UpdateUserEventRequest;
import ru.dto.user.UserDto;
import ru.dto.user.UserFollowingDto;
import ru.exception.ConflictException;
import ru.exception.NotFoundException;
import ru.feign.CategoryClient;
import ru.feign.UserClient;
import ru.feign.UserRequestsClient;
import ru.mapper.EventMapper;
import ru.model.Event;
import ru.related.EventParam;
import ru.related.EventSearchParam;
import ru.related.EventState;
import ru.related.RequestStatus;
import ru.repository.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.model.QEvent.event;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final StatsClient statsClient;
    private final CategoryClient categoryClient;
    private final UserClient userClient;
    private final UserRequestsClient userRequestsClient;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsForUser(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.findByInitiator(userId, PageRequest.of(from, size));

        return events.stream()
                .map((e) -> eventMapper.toEventShortDto(e, checkUserInDB(e.getInitiator()),
                        checkCategoryInDB(e.getCategory())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        UserDto initiator = checkUserInDB(userId);
        CategoryDto category = checkCategoryInDB(eventDto.getCategory());
        Event event = eventMapper.toEvent(eventDto, userId);

        event = eventRepository.save(event);

        return eventMapper.toEventFullDto(event, initiator, category);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByIdForUser(Long userId, Long eventId) {
        Event event = checkEventForUserInDB(userId, eventId);
        UserDto initiator = checkUserInDB(userId);
        CategoryDto category = checkCategoryInDB(event.getCategory());
        return eventMapper.toEventFullDto(event, initiator, category);
    }

    @Override
    @Transactional
    public EventFullDto changeEvent(Long userId, Long eventId, UpdateUserEventRequest eventDto) {
        Event event = checkEventForUserInDB(userId, eventId);
        checkEventPublished(event);
        updEventForUserEventDto(event, eventDto);
        eventMapper.updateEventFromEventDto(event, eventDto);

        UserDto initiator = checkUserInDB(userId);
        CategoryDto category = checkCategoryInDB(event.getCategory());

        event = eventRepository.save(event);
        return eventMapper.toEventFullDto(event, initiator, category);
    }

    @Override
    @Transactional
    public EventFullDto changeEvent(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = checkEventInDB(eventId);
        updEventForAdminEventDto(eventDto, event);
        eventMapper.updEventForAdminEventDto(event, eventDto);

        UserDto initiator = checkUserInDB(event.getInitiator());
        CategoryDto category = checkCategoryInDB(event.getCategory());

        event = eventRepository.save(event);
        return eventMapper.toEventFullDto(event, initiator, category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchEvents(EventSearchParam param) {
        BooleanExpression predicate = event.isNotNull();
        PageRequest page = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());

        if (param.isUsers()) {
            predicate = predicate.and(event.initiator.in(param.getUsers()));
        }
        if (param.isStates()) {
            predicate = predicate.and(event.state.in(param.getStates()));
        }
        if (param.isCategories()) {
            predicate = predicate.and(event.category.in(param.getCategories()));
        }

        if (param.isStart() && param.isEnd()) {
            predicate = predicate.and(event.createdOn.between(param.getRangeStart(), param.getRangeEnd()));
        } else if (param.isStart()) {
            predicate = predicate.and(event.createdOn.after(param.getRangeStart()));
        } else if (param.isEnd()) {
            predicate = predicate.and(event.createdOn.before(param.getRangeEnd()));
        }

        if (!param.isStart()) {
            param.setRangeStart(LocalDateTime.now().minusDays(5));
        }
        if (!param.isEnd()) {
            param.setRangeEnd(LocalDateTime.now().plusDays(5));
        }

        List<EventFullDto> events = eventRepository.findAll(predicate, page).toList().stream()
                .map((e) -> eventMapper.toEventFullDto(e, checkUserInDB(e.getInitiator()),
                        checkCategoryInDB(e.getCategory())))
                .toList();
        for (EventFullDto fullDto : events) {
            updDtoStatsViews(fullDto, param);
        }

        return events;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(EventParam param, StatsSaveRequestDto statsSaveRequestDto) {
        BooleanExpression predicate = event.isNotNull();
        PageRequest page = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());

        if (param.isText()) {
            checkText(param.getText());
            predicate = predicate.and(event.annotation.likeIgnoreCase(param.getText()));
        }

        if (param.isCategories()) {
            predicate = predicate.and(event.category.in(param.getCategories()));
        }

        if (param.isPaid()) {
            predicate = predicate.and(event.paid.eq(param.getPaid()));
        }

        if (param.isStart() && param.isEnd()) {
            predicate = predicate.and(event.createdOn.between(param.getRangeStart(), param.getRangeEnd()));
        } else if (param.isStart()) {
            predicate = predicate.and(event.createdOn.after(param.getRangeStart()));
        } else if (param.isEnd()) {
            predicate = predicate.and(event.createdOn.before(param.getRangeEnd()));
        }

        if (!param.isStart()) {
            param.setRangeStart(LocalDateTime.now().minusDays(5));
        }
        if (!param.isEnd()) {
            param.setRangeEnd(LocalDateTime.now().plusDays(5));
        }

        saveStatGetEvent(statsSaveRequestDto);
        List<EventShortDto> eventsDto = eventRepository.findAll(predicate, page).toList().stream()
                .map((e) -> eventMapper.toEventShortDto(e, checkUserInDB(e.getInitiator()),
                        checkCategoryInDB(e.getCategory())))
                .toList();

        for (EventShortDto fullDto : eventsDto) {
            updDtoStatsViews(fullDto, param);
        }
        return eventsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long id, StatsSaveRequestDto statsSaveRequestDto) {
        Event event = checkEventInDB(id);
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие с id = " + id + " недоступно");
        }

        saveStatGetEvent(statsSaveRequestDto);

        UserDto initiator = checkUserInDB(event.getInitiator());
        CategoryDto category = checkCategoryInDB(event.getCategory());

        EventFullDto fullDto = eventMapper.toEventFullDto(event, initiator, category);
        updDtoStatsViews(fullDto, new EventSearchParam(LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(3)));
        return fullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getFollowEvent(Long userId) {
        UserFollowingDto user = checkUserInDataBase(userId);
        return eventRepository.findByStateIsAndInitiatorIn(EventState.PUBLISHED, user.getFollowing())
                .stream()
                .map((e) -> eventMapper.toEventShortDto(e, checkUserInDB(e.getInitiator()),
                        checkCategoryInDB(e.getCategory())))
                .toList();
    }

    @Override
    public Optional<EventFullDto> getEvent(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            return Optional.empty();
        }
        UserDto initiator = checkUserInDB(event.get().getInitiator());
        CategoryDto category = checkCategoryInDB(event.get().getCategory());
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event.get(), initiator, category);
        return Optional.of(eventFullDto);
    }

    @Override
    public Set<EventShortDto> getEventsByIds(Set<Long> events) {
        Set<Event> eventSet = eventRepository.findByIdIn(events);
        return eventSet.stream()
                .map((e) -> eventMapper.toEventShortDto(e, checkUserInDB(e.getInitiator()),
                        checkCategoryInDB(e.getCategory())))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean existsByCategoryFromEvent(Long catId) {
        return eventRepository.existsByCategory(catId);
    }

    private void checkText(String str) {
        if (str.equals("0")) {
            throw new IllegalArgumentException("Указать подробное описание события");
        }
    }

    private void saveStatGetEvent(StatsSaveRequestDto saveDto) {
        statsClient.saveRestClientNewStat(saveDto);
    }

    private EventFullDto updDtoStatsViews(EventFullDto event, EventSearchParam param) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<StatCountHitsResponseDto> stats = statsClient.getStats(
                param.getRangeStart().format(dateTimeFormatter),
                param.getRangeEnd().format(dateTimeFormatter),
                List.of("/events/" + event.getId()),
                true);
        long views = 0L;

        for (StatCountHitsResponseDto stat : stats) {
            views += stat.getHits();
        }
        long confirmedRequests = userRequestsClient.countByStatusAndEventId(RequestStatus.CONFIRMED, event.getId());

        event.setViews(views);
        event.setConfirmedRequests((int) confirmedRequests);
        return event;
    }

    private EventShortDto updDtoStatsViews(EventShortDto event, EventParam param) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<StatCountHitsResponseDto> stats = statsClient.getStats(
                param.getRangeStart().format(dateTimeFormatter),
                param.getRangeEnd().format(dateTimeFormatter),
                List.of("events/" + event.getId()),
                false);
        long views = 0L;

        for (StatCountHitsResponseDto stat : stats) {
            views += stat.getHits();
        }
        long confirmedRequests = userRequestsClient.countByStatusAndEventId(RequestStatus.CONFIRMED, event.getId());

        event.setViews(views);
        event.setConfirmedRequests((int) confirmedRequests);
        return event;
    }

    private void updEventForAdminEventDto(UpdateEventAdminRequest eventDto, Event event) {
        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case REJECT_EVENT -> {
                    checkEventStatePublished(event);
                    event.setState(EventState.CANCELED);
                }

                case PUBLISH_EVENT -> {
                    checkDatePublishAndState(event);
                    event.setPublishedOn(LocalDateTime.now());
                    event.setState(EventState.PUBLISHED);
                }
            }
        }

        if (eventDto.getLocation() != null) {
            event.setLocation(eventMapper.toLocation(eventDto.getLocation()));
        }
        if (eventDto.getCategory() != null) {
            event.setCategory(event.getCategory());
        }
    }

    private void checkEventStatePublished(Event event) {
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано");
        }
    }

    private void checkDatePublishAndState(Event event) {
        if (event.getState() != EventState.PENDING) {
            throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
        }
        if (LocalDateTime.now().plusHours(1).withNano(0).isAfter(event.getEventDate())) {
            throw new ConflictException("Дата начала изменяемого события должна быть не ранее чем за час " +
                    "от даты публикации");
        }
    }

    private UserFollowingDto checkUserInDataBase(Long id) {
        return userClient.getUserFollowing(id)
                .orElseThrow(() -> new NotFoundException("Значение в базе users не найдено: " + id));
    }

    private Event checkEventInDB(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    private void checkEventPublished(Event event) {
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Событие с id = " + event.getId() + " опубликовано и не может быть изменено");
        }
    }

    private void updEventForUserEventDto(Event event, UpdateUserEventRequest eventDto) {
        if (eventDto.getStateAction() != null) {
            switch (eventDto.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);

                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);

            }
        }

        if (eventDto.getLocation() != null) {
            event.setLocation(eventMapper.toLocation(eventDto.getLocation()));
        }

        if (eventDto.getCategory() != null) {
            event.setCategory(event.getCategory());
        }
    }

    private Event checkEventForUserInDB(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiator(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    private UserDto checkUserInDB(Long userId) {
        return userClient.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + "не найден"));
    }

    private CategoryDto checkCategoryInDB(Long catId) {
        return categoryClient.getCategory(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + catId + "не найдена"));
    }
}
