package ru.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dto.compilation.CompilationDto;
import ru.dto.compilation.NewCompilationDto;
import ru.dto.compilation.UpdateCompilationRequest;
import ru.mapper.CompilationMapper;
import ru.model.Compilation;
import ru.model.Event;
import ru.repository.CompilationRepository;
import ru.repository.EventRepository;
import ru.service.CompilationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto changeCompilation(NewCompilationDto compilationDto) {
        Set<Event> events = compilationDto.getEvents() != null ?
                checkEventInDB(compilationDto.getEvents()) : new HashSet<>();

        Compilation compilation = compilationMapper.toCompilation(compilationDto, events);
        compilation = compilationRepository.save(compilation);

        return compilationMapper.toResponseDto(compilation);
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
        patchCompilationToDto(compilation, compilationDto);
        compilation = compilationRepository.save(compilation);

        return compilationMapper.toResponseDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        List<Compilation> compilations =
                compilationRepository.findAllByPinned(pinned, PageRequest.of(from, size)).getContent();

        return compilations.stream()
                .map(compilationMapper::toResponseDto)
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationsById(Long compId) {
        Compilation compilation = checkCompilationInDB(compId);

        return compilationMapper.toResponseDto(compilation);
    }

    private Set<Event> checkEventInDB(Set<Long> ids) {
        Set<Event> events = eventRepository.findByIdIn(ids);
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
            Set<Event> events = checkEventInDB(patchDto.getEvents());
            compilation.setEvents(events);
        }

        return compilation;
    }

    private Compilation checkCompilationInDB(long compId) {
        return compilationRepository.getCompilationById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе не найдено для Compilation: " + compId));
    }
}
