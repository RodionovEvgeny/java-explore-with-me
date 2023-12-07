package ru.practicum.srevice;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.EndpointHitMapper;
import ru.practicum.model.Stats;
import ru.practicum.model.StatsMapper;
import ru.practicum.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatServiceImpl implements StatService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    public EndpointHitDto addHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
        return EndpointHitMapper.toEndpointHitDto(endpointHit);
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<Stats> stats;
        if (uris == null || uris.isEmpty()) {
            stats = unique ? endpointHitRepository.getStatsNoUrisUniqueIp(start, end)
                    : endpointHitRepository.getStatsNoUrisNotUniqueIp(start, end);
        } else {
            stats = unique ? endpointHitRepository.getStatsWithUrisUniqueIp(start, end, uris)
                    : endpointHitRepository.getStatsWithUrisNotUniqueIp(start, end, uris);
        }
        return stats.stream()
                .map(StatsMapper::toStatsDto)
                .collect(Collectors.toList());
    }
}
