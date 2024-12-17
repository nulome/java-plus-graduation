package ru.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.related.RequestStatus;

import java.util.Set;

@Data
public class EventRequestStatusUpdateRequest {
    @NotEmpty
    private Set<Long> requestIds;
    @NotNull
    private RequestStatus status;
}
