package ru.practicum;

public class StatsMapper {
    public static Stats toStats(StatsDto statsDto) {
        return Stats.builder()
                .app(statsDto.getApp())
                .uri(statsDto.getUri())
                .hits(statsDto.getHits())
                .build();
    }

    public static StatsDto toStatsDto(Stats stats) {
        return StatsDto.builder()
                .app(stats.getApp())
                .uri(stats.getUri())
                .hits(stats.getHits())
                .build();
    }
}
