package ru.practicum;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Service
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