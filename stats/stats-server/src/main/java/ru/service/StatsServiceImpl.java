package ru.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dto.StatCountHitsDto;
import ru.dto.StatCountHitsResponseDto;
import ru.dto.StatDto;
import ru.dto.StatsResponseHitDto;
import ru.dto.StatsSaveRequestDto;
import ru.error.CustomValueException;
import ru.mapper.StatsMapper;
import ru.model.Stat;
import ru.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;
    private final StatsMapper statsMapper;

    @Transactional
    @Override
    public StatsResponseHitDto saveInfo(StatsSaveRequestDto saveRequestDto) {
        StatDto statDto = statsMapper.toStatDto(saveRequestDto);
        Stat stat = statsMapper.toStat(statDto);
        stat = repository.save(stat);

        return statsMapper.toResponseDto(stat);
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatCountHitsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        checkTimeStartAndEnd(start, end);
        List<StatCountHitsDto> listStats;

        if (unique) {
            listStats = uris.isEmpty() ?
                    repository.findAllWithUniqueIp(start, end) : repository.findWithUniqueIp(start, end, uris);
        } else {
            listStats = uris.isEmpty() ?
                    repository.findAllWithoutUniqueIp(start, end) : repository.findWithoutUniqueIp(start, end, uris);
        }

        return listStats.stream()
                .map(statsMapper::toCountHitsResponseDto)
                .collect(Collectors.toList());
    }

    private void checkTimeStartAndEnd(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new CustomValueException("Дата start " + start + "не может быть позже end " + end);
        }
    }
}
