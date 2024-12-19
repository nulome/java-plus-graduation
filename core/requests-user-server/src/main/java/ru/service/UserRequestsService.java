package ru.service;

import ru.dto.request.EventRequestStatusUpdateRequest;
import ru.dto.request.EventRequestStatusUpdateResult;
import ru.dto.request.ParticipationRequestDto;
import ru.related.RequestStatus;

import java.util.List;

public interface UserRequestsService {

    List<ParticipationRequestDto> getRequestByUser(Long userId);

    ParticipationRequestDto createRequestByUser(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestByUser(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestForUserAndEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult requestUpdateStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest statusUpdateRequest);

    Long countByStatusAndEventId(RequestStatus status, Long eventId);

}
