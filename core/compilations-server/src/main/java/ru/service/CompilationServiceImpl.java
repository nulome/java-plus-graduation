package ru.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dto.compilation.CompilationDto;
import ru.dto.compilation.NewCompilationDto;
import ru.dto.compilation.UpdateCompilationRequest;
import ru.dto.event.EventShortDto;
import ru.feign.EventClient;
import ru.mapper.CompilationMapper;
import ru.model.Compilation;
import ru.repository.CompilationRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventClient eventClient;

    @Override
    @Transactional
    public CompilationDto changeCompilation(NewCompilationDto compilationDto) {
        Set<EventShortDto> events = compilationDto.getEvents() != null ?
                checkEventInDB(compilationDto.getEvents()) : new HashSet<>();

        Compilation compilation = compilationMapper.toCompilation(compilationDto);
        compilation = compilationRepository.save(compilation);

        return compilationMapper.toResponseDto(compilation, events);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        checkCompilationInDB(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilation(Long compId, UpdateCompilationRequest compilationDto) {
        Compilation compilation = checkCompilationInDB(compId);
        Set<EventShortDto> events = compilationDto.getEvents() != null ?
                checkEventInDB(compilationDto.getEvents()) : checkEventInDB(compilation.getEvents());
        patchCompilationToDto(compilation, compilationDto);
        compilation = compilationRepository.save(compilation);

        return compilationMapper.toResponseDto(compilation, events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        List<Compilation> compilations =
                compilationRepository.findAllByPinned(pinned, PageRequest.of(from, size)).getContent();

        return compilations.stream()
                .map(compilation -> compilationMapper.toResponseDto(compilation, checkEventInDB(compilation.getEvents())))
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationsById(Long compId) {
        Compilation compilation = checkCompilationInDB(compId);
        Set<EventShortDto> events = checkEventInDB(compilation.getEvents());
        return compilationMapper.toResponseDto(compilation, events);
    }

    private Set<EventShortDto> checkEventInDB(Set<Long> ids) {
        Set<EventShortDto> events = eventClient.getEventsByIds(ids);
        if (events.size() != ids.size()) {
            throw new IllegalArgumentException("Количество событий не соответствует переданному значению: " + ids);
        }
        return events;
    }

    private Compilation patchCompilationToDto(Compilation compilation, UpdateCompilationRequest patchDto) {
        if (patchDto.getPinned() != null && patchDto.getPinned() != compilation.getPinned()) {
            compilation.setPinned(patchDto.getPinned());
        }
        if (patchDto.getTitle() != null && !patchDto.getTitle().equals(compilation.getTitle())) {
            compilation.setTitle(patchDto.getTitle());
        }
        if (patchDto.getEvents() != null && !patchDto.getEvents().isEmpty()) {
            compilation.setEvents(patchDto.getEvents());
        }

        return compilation;
    }

    private Compilation checkCompilationInDB(long compId) {
        return compilationRepository.getCompilationById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе не найдено для Compilation: " + compId));
    }

}
