package ru.practicum.srevice;

import ru.practicum.EndpointHitDto;
import ru.practicum.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    EndpointHitDto addHit(EndpointHitDto endpointHitDto);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
