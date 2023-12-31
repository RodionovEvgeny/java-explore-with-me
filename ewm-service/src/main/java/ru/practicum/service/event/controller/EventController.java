package ru.practicum.service.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.model.SortState;
import ru.practicum.service.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                            @RequestParam(required = false) SortState sort,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            HttpServletRequest request) {
        return eventService.getAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable(name = "id") Long eventId,
                                     HttpServletRequest request) {
        return eventService.getEventById(eventId, request);
    }
}
