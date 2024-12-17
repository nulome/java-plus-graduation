package ru.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.dto.user.NewUserRequest;
import ru.dto.user.UserDto;
import ru.dto.user.UserFollowingDto;
import ru.model.User;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    public abstract UserDto toUserDto(User user);

    @Mapping(target = "following", expression = "java(setUserIdsFollowing(user))")
    public abstract UserFollowingDto toUserFollowingDto(User user);

    @Mapping(target = "id", ignore = true)
    public abstract User toUser(NewUserRequest newUserRequest);

    @Named("setUserIdsFollowing")
    Set<Long> setUserIdsFollowing(User user) {
        return user.getFollowing().stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }

}
