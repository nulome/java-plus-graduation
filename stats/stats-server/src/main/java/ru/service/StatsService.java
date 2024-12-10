package ru.service;


import ru.dto.StatCountHitsResponseDto;
import ru.dto.StatsResponseHitDto;
import ru.dto.StatsSaveRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatsResponseHitDto saveInfo(StatsSaveRequestDto hit);

    List<StatCountHitsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
