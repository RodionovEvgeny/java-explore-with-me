package ru.practicum.service.event;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.StatsDto;
import ru.practicum.service.category.CategoryService;
import ru.practicum.service.exceptions.BadRequestException;
import ru.practicum.service.exceptions.EntityNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final CategoryService categoryService;

    @Override
    public List<EventShortDto> getAllEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Boolean onlyAvailable, SortState sort, Integer from,
                                            Integer size, HttpServletRequest request) {
        if (rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Дата конца выборки не может быть раньше даты начала выборки");
        }
        statsClient.addHit(request, "main_service");
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.getAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        return events.stream()
                .map(this::addViews)
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = validateEventById(eventId);
        statsClient.addHit(request, "main_service");
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventStatus> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                               Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events;
        if (users == null && states == null && categories == null && rangeStart == null && rangeEnd == null) {
            events = eventRepository.findAll(pageable).toList();
        } else {
            events = eventRepository.getAllEventsByAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
        }
        return events.stream()
                .map(this::addViews)
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event eventToUpdate = validateEventById(eventId);
        if (updateEvent.getCategory() != null) {
            eventToUpdate.setCategory(categoryService.getCategoryById(updateEvent.getCategory()));
        }
        Event updatedEvent = eventRepository.save(EventMapper.updateEvent(eventToUpdate, updateEvent));
        return EventMapper.toEventFullDto(updatedEvent);
    }


    private Event validateEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Событие с id = %s не найдено!", eventId),
                Event.class.getName()));
    }

    private Event addViews(Event event) {
        String uri = "/events/" + event.getId();
        List<StatsDto> stats = statsClient.getStats(LocalDateTime.MIN, LocalDateTime.now(), List.of(uri), Boolean.FALSE);
        event.setViews(stats.get(0).getHits()); // TODO хз хз непонятно опка работает ли это вообще
        return event;
    }
}
