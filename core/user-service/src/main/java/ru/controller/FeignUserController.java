package ru.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dto.user.UserDto;
import ru.dto.user.UserFollowingDto;
import ru.service.UserService;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feign/users")
public class FeignUserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public Optional<UserDto> getUser(@PathVariable Long userId) {
        log.info("Запрос feign на получение пользователя User = {}", userId);
        return userService.getUser(userId);
    }

    @GetMapping("/{userId}/following")
    public Optional<UserFollowingDto> getUserFollowing(@PathVariable Long userId) {
        log.info("Запрос feign на получение пользователя UserFollowing = {}", userId);
        return userService.getUserFollowing(userId);
    }

}
