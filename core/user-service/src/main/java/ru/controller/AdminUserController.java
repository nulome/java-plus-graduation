package ru.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.dto.user.NewUserRequest;
import ru.dto.user.UserDto;
import ru.service.UserService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Tag(name = "Admin: Пользователи", description = "API для работы с пользователями")
public class AdminUserController {
    private final UserService service;

    @GetMapping
    @Operation(summary = "Получение информации о пользователях",
            description = "Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки)")
    public List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение пользователей ids = {}, from = {}, size = {}", ids, from, size);
        return service.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление нового пользователя")
    public UserDto createUser(@RequestBody @Valid NewUserRequest newUser) {
        log.info("Создание пользователя {}", newUser);
        return service.create(newUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление пользователя")
    public void deleteUser(@PathVariable Long id) {
        log.info("Удаление пользователя id = {}", id);
        service.delete(id);
    }
}