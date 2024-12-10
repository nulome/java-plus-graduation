package ru.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.dto.user.NewUserRequest;
import ru.dto.user.UserDto;
import ru.dto.user.UserShortDto;
import ru.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequest newUserRequest);

    UserShortDto toUserShortDto(User user);
}
