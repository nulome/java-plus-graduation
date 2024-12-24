package ru.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.dto.category.CategoryDto;
import ru.dto.event.EventFullDto;
import ru.dto.event.EventShortDto;
import ru.dto.event.LocationDto;
import ru.dto.event.NewEventDto;
import ru.dto.event.UpdateEventAdminRequest;
import ru.dto.event.UpdateUserEventRequest;
import ru.dto.user.UserDto;
import ru.model.Event;
import ru.model.Location;
import ru.related.EventState;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class EventMapper {

    @Mapping(target = "id", expression = "java(event.getId())")
    @Mapping(target = "category", source = "categoryDto")
    @Mapping(target = "initiator", source = "userDto")
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    public abstract EventFullDto toEventFullDto(Event event, UserDto userDto, CategoryDto categoryDto);

    @Mapping(target = "id", expression = "java(event.getId())")
    @Mapping(target = "category", source = "categoryDto")
    @Mapping(target = "initiator", source = "userDto")
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    public abstract EventShortDto toEventShortDto(Event event, UserDto userDto, CategoryDto categoryDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", expression = "java(setCreatedOnNow())")
    @Mapping(target = "state", expression = "java(setCreateState())")
    @Mapping(target = "publishedOn", ignore = true)
    public abstract Event toEvent(NewEventDto newEventDto, Long initiator);

    public abstract Location toLocation(LocationDto locationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    public abstract void updateEventFromEventDto(@MappingTarget Event event, UpdateUserEventRequest updateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    public abstract void updEventForAdminEventDto(@MappingTarget Event event, UpdateEventAdminRequest updateDto);


    @Named("setCreatedOnNow")
    LocalDateTime setCreatedOnNow() {
        return LocalDateTime.now().withNano(0);
    }

    @Named("setCreateState")
    EventState setCreateState() {
        return EventState.PENDING;
    }
}
