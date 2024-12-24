package ru.feign;

import ru.dto.user.UserDto;
import ru.dto.user.UserFollowingDto;
import ru.exception.FeignException;

import java.util.Optional;

public class UserClientFallback implements UserClient {
    @Override
    public Optional<UserDto> getUser(Long userId) {
        throw new FeignException("Ошибка запроса через Feign UserClient");
    }

    @Override
    public Optional<UserFollowingDto> getUserFollowing(Long userId) {
        throw new FeignException("Ошибка запроса через Feign UserClient");
    }
}
