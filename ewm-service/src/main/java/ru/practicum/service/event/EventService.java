package ru.practicum.service.event;

import ru.practicum.service.SortState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getAllEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, SortState sort, Integer from, Integer size);

    EventFullDto getEventById(Integer eventId);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventStatus> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent);
}
