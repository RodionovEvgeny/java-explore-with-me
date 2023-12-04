package ru.practicum;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    public EndpointHitDto addHit(@RequestBody EndpointHitDto endpointHitDto) {
        return statService.addHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam LocalDateTime start,
                                   @RequestParam LocalDateTime end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false") boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }
}
