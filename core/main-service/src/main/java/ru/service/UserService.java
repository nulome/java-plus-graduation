package ru.service;


import ru.dto.user.NewUserRequest;
import ru.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto create(NewUserRequest newUser);

    void delete(Long id);

    void subscribe(Long followerId, Long followedId);

    void unsubscribe(Long followerId, Long followedId);

    List<UserDto> getFollowers(Long userId);

    List<UserDto> getFollowing(Long userId);
}
