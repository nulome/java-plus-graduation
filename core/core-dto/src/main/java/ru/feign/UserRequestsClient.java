package ru.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.related.RequestStatus;

@FeignClient(name = "requests-user-server", fallback = UserRequestsClientFallback.class)
public interface UserRequestsClient {

    @GetMapping("/feign/requests/count")
    Long countByStatusAndEventId(@RequestParam RequestStatus status, @RequestParam Long eventId);

}
