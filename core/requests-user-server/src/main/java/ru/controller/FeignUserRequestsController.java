package ru.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.related.RequestStatus;
import ru.service.UserRequestsService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feign/requests")
public class FeignUserRequestsController {

    private final UserRequestsService userRequestsService;

    @GetMapping("/count")
    public Long countByStatusAndEventId(@RequestParam RequestStatus status, @RequestParam Long eventId) {
        log.info("Запрос на получение count Requests к eventId {}", eventId);
        return userRequestsService.countByStatusAndEventId(status, eventId);
    }
}
