package ru.practicum;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        return null;
    }
}
