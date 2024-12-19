package ru.feign;

import ru.exception.FeignException;
import ru.related.RequestStatus;

public class UserRequestsClientFallback implements UserRequestsClient {
    @Override
    public Long countByStatusAndEventId(RequestStatus status, Long eventId) {
        throw new FeignException("Ошибка запроса через Feign UserRequestsClient");
    }
}
