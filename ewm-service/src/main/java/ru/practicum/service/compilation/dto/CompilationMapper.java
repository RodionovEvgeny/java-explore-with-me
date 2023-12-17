package ru.practicum.service.compilation.dto;

import ru.practicum.service.compilation.model.Compilation;
import ru.practicum.service.event.dto.EventMapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .events(new ArrayList<>())
                .pinned(newCompilationDto.getPinned() == null ? Boolean.FALSE : newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
