package ru.practicum.service.event;

import ru.practicum.service.SortState;

import java.util.List;

public interface EventService {
    List<EventDto> getAllEvents(String text, List<Integer> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, SortState sort, Integer from, Integer size);

    EventFullDto getEventById(Integer eventId);
}
