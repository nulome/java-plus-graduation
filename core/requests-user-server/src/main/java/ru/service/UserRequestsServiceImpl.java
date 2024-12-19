package ru.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dto.event.EventFullDto;
import ru.dto.request.EventRequestStatusUpdateRequest;
import ru.dto.request.EventRequestStatusUpdateResult;
import ru.dto.request.ParticipationRequestDto;
import ru.dto.user.UserDto;
import ru.exception.ConflictException;
import ru.exception.NotFoundException;
import ru.feign.EventClient;
import ru.feign.UserClient;
import ru.mapper.UserRequestMapper;
import ru.model.UserRequest;
import ru.related.RequestStatus;
import ru.repository.UserRequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserRequestsServiceImpl implements UserRequestsService {

    private final UserRequestRepository requestRepository;
    private final UserRequestMapper userRequestMapper;
    private final UserClient userClient;
    private final EventClient eventClient;

    @Override
    public List<ParticipationRequestDto> getRequestByUser(Long userId) {
        checkUserInDataBase(userId);
        List<UserRequest> listRequests = requestRepository.findAllByRequester(userId);
        return listRequests.stream()
                .map(userRequestMapper::toPartRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequestByUser(Long userId, Long eventId) {
        UserDto user = checkUserInDataBase(userId);
        EventFullDto event = checkEventInDB(eventId);

        verificationRequestToEvent(user, event);
        UserRequest userRequest = userRequestMapper.toUserRequest(user, event);
        userRequest = requestRepository.save(userRequest);

        return userRequestMapper.toPartRequestDto(userRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequestByUser(Long userId, Long requestId) {
        checkUserInDataBase(userId);
        UserRequest userRequest = checkUserRequestInDataBase(requestId);
        userRequest.setStatus(RequestStatus.CANCELED);
        userRequest = requestRepository.save(userRequest);

        return userRequestMapper.toPartRequestDto(userRequest);
    }

    @Override
    public List<ParticipationRequestDto> getRequestForUserAndEvent(Long userId, Long eventId) {
        List<UserRequest> userRequest = requestRepository.findAllByEvent(eventId);
        return userRequest.stream()
                .map(userRequestMapper::toPartRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult requestUpdateStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest statusUpdateRequest) {
        List<UserRequest> listRequest = requestRepository.findByIdInAndEvent(
                statusUpdateRequest.getRequestIds(), eventId);
        return verificationUserRequestAndUpdate(eventId, listRequest, statusUpdateRequest.getStatus());
    }

    @Override
    public Long countByStatusAndEventId(RequestStatus status, Long eventId) {
        return requestRepository.countByStatusAndEvent(status, eventId);
    }

    private EventRequestStatusUpdateResult verificationUserRequestAndUpdate(Long eventId, List<UserRequest> listRequest, RequestStatus status) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());
        Set<Long> setIds = listRequest.stream()
                .mapToLong(UserRequest::getEvent)
                .boxed()
                .collect(Collectors.toSet());

        List<Map<Long, Long>> rawMapByIds = requestRepository.findRawMapByIds(RequestStatus.CONFIRMED.toString(), setIds);
        Map<Long, Long> mapCountByEvent = rawMapByIds.stream()
                .flatMap((m) -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (UserRequest request : listRequest) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
            }
            long countRequest = mapCountByEvent.getOrDefault(request.getEvent(), 0L);

            EventFullDto event = checkEventInDB(request.getEvent());
            if (countRequest >= event.getParticipantLimit()) {
                throw new ConflictException("Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие");
            }
            if (event.getParticipantLimit() != 0 && event.getRequestModeration()) {
                requestRepository.updateUserRequestStatus(status.toString(), request.getId());
                addResultRequest(result, request, status);
                if (countRequest + 1L == event.getParticipantLimit()) {
                    requestRepository.cancelStatusAllRequestPending(eventId);
                }
            }
        }
        return result;
    }

    private void addResultRequest(EventRequestStatusUpdateResult result, UserRequest request, RequestStatus status) {
        ParticipationRequestDto addPart = userRequestMapper.toPartRequestDto(request);
        addPart.setStatus(status);
        if (status == RequestStatus.CONFIRMED) {
            result.getConfirmedRequests().add(addPart);
        }
        if (status == RequestStatus.REJECTED) {
            result.getRejectedRequests().add(addPart);
        }
    }

    private UserRequest checkUserRequestInDataBase(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Значение в базе для UserRequest не найдено: " + id));
    }

    private UserDto checkUserInDataBase(Long id) {
        return userClient.getUser(id)
                .orElseThrow(() -> new NotFoundException("Значение в базе users не найдено: " + id));
    }

    private EventFullDto checkEventInDB(Long eventId) {
        return eventClient.getEvent(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
    }

    private void verificationRequestToEvent(UserDto user, EventFullDto event) {
        long userId = user.getId();
        if (userId == event.getInitiator().getId()) {
            throw new ConflictException("Инициатор события userId " + userId + " не может добавить запрос на участие " +
                    "в своём событии.");
        }
        if (event.getPublishedOn() == null) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии id - " + event.getId());
        }
        if (event.getParticipantLimit() != 0) {
            long countRequest = requestRepository.countByStatusAndEvent(RequestStatus.CONFIRMED, event.getId());
            if (countRequest >= event.getParticipantLimit()) {
                throw new ConflictException("У события достигнут лимит запросов на участие, id - " + event.getId());
            }
        }
    }

}
