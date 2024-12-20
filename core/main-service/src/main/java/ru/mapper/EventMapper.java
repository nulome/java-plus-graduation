package ru.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import ru.dto.event.EventFullDto;
import ru.dto.event.EventShortDto;
import ru.dto.event.NewEventDto;
import ru.dto.event.UpdateEventAdminRequest;
import ru.dto.event.UpdateUserEventRequest;
import ru.model.Category;
import ru.model.Event;
import ru.model.Location;
import ru.model.User;
import ru.related.EventState;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class EventMapper {

    @Autowired
    UserMapper userMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Mapping(target = "category", expression = "java(categoryMapper.toCategoryDto(event.getCategory()))")
    @Mapping(target = "initiator", expression = "java(userMapper.toUserShortDto(event.getInitiator()))")
    public abstract EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category", expression = "java(categoryMapper.toCategoryDto(event.getCategory()))")
    @Mapping(target = "initiator", expression = "java(userMapper.toUserShortDto(event.getInitiator()))")
    public abstract EventShortDto toEventShortDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", source = "user")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "createdOn", expression = "java(setCreatedOnNow())")
    @Mapping(target = "state", expression = "java(setCreateState())")
    public abstract Event toEvent(NewEventDto newEventDto, User user, Category category, Location location);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "state", ignore = true)
    public abstract void updateEventFromEventDto(@MappingTarget Event event, UpdateUserEventRequest updateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "state", ignore = true)
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
