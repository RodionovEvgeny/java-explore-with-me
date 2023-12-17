package ru.practicum.service.compilation;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.event.EventRepository;
import ru.practicum.service.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        List<Long> events = newCompilationDto.getEvents();
        if (events != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilation(Long compId) {
        validateCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto newCompilationDto) {
        Compilation compilationToUpdate = validateCompilationById(compId);

        if (newCompilationDto.getPinned() != null) compilationToUpdate.setPinned(newCompilationDto.getPinned());
        if (newCompilationDto.getTitle() != null) compilationToUpdate.setTitle(newCompilationDto.getTitle());
        if (newCompilationDto.getEvents() != null) {
            compilationToUpdate.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilationToUpdate));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned == null) {
            return compilationRepository.findAll(pageable).stream()
                    .map(CompilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        } else {
            return compilationRepository.findAllByPinned(pinned, pageable).stream()
                    .map(CompilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = validateCompilationById(compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    private Compilation validateCompilationById(long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Подборка с id = %s не найдена!", compId),
                Compilation.class.getName()));
    }
}
