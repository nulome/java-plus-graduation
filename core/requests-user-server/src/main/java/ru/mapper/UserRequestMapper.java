package ru.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.dto.event.EventFullDto;
import ru.dto.request.ParticipationRequestDto;
import ru.dto.user.UserDto;
import ru.model.UserRequest;
import ru.related.RequestStatus;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class UserRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", expression = "java(event.getId())")
    @Mapping(target = "requester", expression = "java(user.getId())")
    @Mapping(target = "status", expression = "java(setStatus(event))")
    @Mapping(target = "created", expression = "java(setCreatedOnNow())")
    public abstract UserRequest toUserRequest(UserDto user, EventFullDto event);

    public abstract ParticipationRequestDto toPartRequestDto(UserRequest userRequest);


    @Named("setCreatedOnNow")
    LocalDateTime setCreatedOnNow() {
        return LocalDateTime.now().withNano(0);
    }

    @Named("setStatus")
    RequestStatus setStatus(EventFullDto event) {
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return RequestStatus.CONFIRMED;
        }
        return RequestStatus.PENDING;
    }
}
