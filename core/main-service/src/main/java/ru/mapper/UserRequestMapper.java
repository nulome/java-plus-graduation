package ru.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.dto.request.ParticipationRequestDto;
import ru.model.Event;
import ru.model.User;
import ru.model.UserRequest;
import ru.related.RequestStatus;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class UserRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", source = "event")
    @Mapping(target = "requester", source = "user")
    @Mapping(target = "status", expression = "java(setStatus(event))")
    @Mapping(target = "created", expression = "java(setCreatedOnNow())")
    public abstract UserRequest toUserRequest(User user, Event event);


    @Mapping(target = "event", expression = "java(userRequest.getEvent().getId())")
    @Mapping(target = "requester", expression = "java(userRequest.getRequester().getId())")
    public abstract ParticipationRequestDto toPartRequestDto(UserRequest userRequest);


    @Named("setCreatedOnNow")
    LocalDateTime setCreatedOnNow() {
        return LocalDateTime.now().withNano(0);
    }

    @Named("setStatus")
    RequestStatus setStatus(Event event) {
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return RequestStatus.CONFIRMED;
        }
        return RequestStatus.PENDING;
    }
}
