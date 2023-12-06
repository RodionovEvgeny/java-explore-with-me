package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.model.Stats(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit AS e " +
            "WHERE e.timestamp >= ?1 " +
            "AND e.timestamp <= ?2 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<Stats> getStatsNoUrisNotUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.model.Stats(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit AS e " +
            "WHERE e.timestamp >= ?1 " +
            "AND e.timestamp <= ?2 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<Stats> getStatsNoUrisUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.model.Stats(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit AS e " +
            "WHERE e.timestamp >= ?1 " +
            "AND e.timestamp <= ?2 " +
            "AND e.uri in ?3 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<Stats> getStatsWithUrisNotUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.model.Stats(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit AS e " +
            "WHERE e.timestamp >= ?1 " +
            "AND e.timestamp <= ?2 " +
            "AND e.uri in ?3 " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<Stats> getStatsWithUrisUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

}
