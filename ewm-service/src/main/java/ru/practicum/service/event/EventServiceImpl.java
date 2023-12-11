package ru.practicum.service.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.service.SortState;
import ru.practicum.service.exceptions.EntityNotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    @Override
    public List<EventDto> getAllEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                       String rangeEnd, Boolean onlyAvailable, SortState sort, Integer from,
                                       Integer size) {
        return null;
    }

    @Override
    public EventFullDto getEventById(Integer eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Событие с id = %s не найдено!", eventId),
                Event.class.getName()));;


        return null;
    }
}
