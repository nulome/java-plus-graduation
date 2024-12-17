package ru.client;

import org.springframework.http.ResponseEntity;
import ru.dto.StatCountHitsResponseDto;
import ru.dto.StatsSaveRequestDto;

import java.util.List;

public interface StatsClient {
    List<StatCountHitsResponseDto> getStats(final String startTime, final String endTime, final List<String> uris, final Boolean unique);

    ResponseEntity<Void> saveRestClientNewStat(final StatsSaveRequestDto request);
}
