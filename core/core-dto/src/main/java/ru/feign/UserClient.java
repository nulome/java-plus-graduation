package ru.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.dto.user.UserDto;
import ru.dto.user.UserFollowingDto;

import java.util.Optional;

@FeignClient(name = "user-service", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/feign/users/{userId}")
    Optional<UserDto> getUser(@PathVariable Long userId);

    @GetMapping("/feign/users/{userId}/following")
    Optional<UserFollowingDto> getUserFollowing(@PathVariable Long userId);

}
