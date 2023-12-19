package ru.practicum;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {

    EndpointHitDto addHit(HttpServletRequest request, String app);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);
}
