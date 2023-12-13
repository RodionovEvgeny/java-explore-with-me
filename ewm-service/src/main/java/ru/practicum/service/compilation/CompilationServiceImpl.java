package ru.practicum.service.compilation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.service.event.EventRepository;
import ru.practicum.service.exceptions.EntityNotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
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
    public CompilationDto updateCompilation(Long compId, NewCompilationDto newCompilationDto) {
        Compilation compilationToUpdate = validateCompilationById(compId);

        if (newCompilationDto.getPinned() != null) compilationToUpdate.setPinned(newCompilationDto.getPinned());
        if (newCompilationDto.getTitle() != null) compilationToUpdate.setTitle(newCompilationDto.getTitle());
        if (newCompilationDto.getEvents() != null) {
            compilationToUpdate.setEvents(eventRepository.findAllByIdIn(newCompilationDto.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilationToUpdate));
    }

    private Compilation validateCompilationById(long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Подборка с id = %s не найдена!", compId),
                Compilation.class.getName()));
    }


}
