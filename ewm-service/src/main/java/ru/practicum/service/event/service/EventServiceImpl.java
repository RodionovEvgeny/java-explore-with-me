package ru.practicum.service.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.StatsDto;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.repository.CategoryRepository;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventMapper;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.dto.NewEventDto;
import ru.practicum.service.event.dto.UpdateEventRequest;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.EventStatus;
import ru.practicum.service.event.model.SortState;
import ru.practicum.service.event.model.StateAction;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exceptions.BadRequestException;
import ru.practicum.service.exceptions.ConflictException;
import ru.practicum.service.exceptions.EntityNotFoundException;
import ru.practicum.service.location.repository.LocationRepository;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllEvents(String text, List<Long> categories, Boolean paid, String rangeStart,
                                            String rangeEnd, boolean onlyAvailable, SortState sort, Integer from,
                                            Integer size, HttpServletRequest request) {
        statsClient.addHit(request, "main_service");

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events;
        LocalDateTime start = getStartDateTime(rangeStart);
        LocalDateTime end = getEndDateTime(rangeEnd);
        validateStartEndDates(start, end);

        events = eventRepository.getAllEvents(text, categories, paid, start, end, onlyAvailable, pageable);
        return events.stream()
                .map(this::addViews)
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = validateEventById(eventId);
        if (event.getState() != EventStatus.PUBLISHED) {
            throw new EntityNotFoundException("Событие еще не опубликовано", Event.class.getName());
        }
        statsClient.addHit(request, "main_service");

        return EventMapper.toEventFullDto(addViews(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventStatus> states, List<Long> categories,
                                               String rangeStart, String rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events;
        LocalDateTime start = getStartDateTime(rangeStart);
        LocalDateTime end = getEndDateTime(rangeEnd);
        validateStartEndDates(start, end);

        events = eventRepository.getAllEventsByAdmin(users, states, categories, start, end, pageable);
        return events.stream()
                .map(this::addViews)
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest updateEvent) {
        Event eventToUpdate = validateEventById(eventId);
        if (updateEvent.getStateAction() != null &&
                updateEvent.getStateAction().equals(StateAction.PUBLISH_EVENT) &&
                !eventToUpdate.getState().equals(EventStatus.PENDING)) {
            throw new ConflictException("Публиковать можно только ожидающие события!");
        }
        if (updateEvent.getStateAction() != null &&
                updateEvent.getStateAction().equals(StateAction.REJECT_EVENT) &&
                !eventToUpdate.getState().equals(EventStatus.PENDING)) {
            throw new ConflictException("Отменить можно только ожидающее событие!");
        }

        if (updateEvent.getCategory() != null) {
            Category category = validateCategoryById(updateEvent.getCategory());
            eventToUpdate.setCategory(category);
        }
        if (updateEvent.getLocation() != null) {
            locationRepository.save(updateEvent.getLocation());
        }
        if (updateEvent.getEventDate() != null && updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Время начала события не может быть раньше чем через 2 часа.");
        }

        updateEvent(eventToUpdate, updateEvent);
        Event updatedEvent = eventRepository.save(eventToUpdate);
        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size) {
        validateUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(this::addViews)
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        User user = validateUserById(userId);
        Category category = validateCategoryById(newEventDto.getCategory());
        LocalDateTime createdOn = LocalDateTime.now();
        if (createdOn.isAfter(newEventDto.getEventDate().minusHours(2))) {
            throw new BadRequestException("Публикация события невозможно позднее чем за два часа до него.");
        }
        if (newEventDto.getRequestModeration() == null) newEventDto.setRequestModeration(Boolean.TRUE);
        locationRepository.save(newEventDto.getLocation());
        Event event = eventRepository.save(EventMapper.toEventFromNewEventDto(newEventDto, user, category, createdOn));
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getUsersEventById(Long userId, Long eventId) {
        validateUserById(userId);
        Event event = validateEventById(eventId);
        return EventMapper.toEventFullDto(addViews(event));
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventRequest updateEvent) {
        validateUserById(userId);
        Event eventToUpdate = validateEventById(eventId);
        if (eventToUpdate.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Редактировать можно только отмененные или ожидающие проверки события.");
        }
        if (updateEvent.getCategory() != null) {
            Category category = validateCategoryById(updateEvent.getCategory());
            eventToUpdate.setCategory(category);
        }
        if (updateEvent.getLocation() != null) {
            locationRepository.save(updateEvent.getLocation());
        }

        updateEvent(eventToUpdate, updateEvent);

        if (updateEvent.getStateAction() != null &&
                updateEvent.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            eventToUpdate.setState(EventStatus.CANCELED);
        } else {
            eventToUpdate.setState(EventStatus.PENDING);
        }

        Event updatedEvent = eventRepository.save(eventToUpdate);
        return EventMapper.toEventFullDto(updatedEvent);
    }

    private Event updateEvent(Event eventToUpdate, UpdateEventRequest updateEvent) {
        if (updateEvent.getAnnotation() != null) {
            eventToUpdate.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null) {
            eventToUpdate.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            eventToUpdate.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getLocation() != null) {
            eventToUpdate.setLocation(updateEvent.getLocation());
        }
        if (updateEvent.getPaid() != null) {
            eventToUpdate.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            eventToUpdate.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            eventToUpdate.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getStateAction() != null) {
            switch (updateEvent.getStateAction()) {
                case REJECT_EVENT:
                    eventToUpdate.setState(EventStatus.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    eventToUpdate.setState(EventStatus.PUBLISHED);
                    break;
            }
        }
        if (updateEvent.getTitle() != null) {
            eventToUpdate.setTitle(updateEvent.getTitle());
        }
        return eventToUpdate;
    }

    private User validateUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %s не найден!", userId),
                User.class.getName()));
    }

    private Event validateEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Событие с id = %s не найдено!", eventId),
                Event.class.getName()));
    }

    private Event addViews(Event event) {
        String uri = "/events/" + event.getId();
        List<StatsDto> stats = statsClient.getStats(LocalDateTime.now().minusYears(1000), LocalDateTime.now().plusMinutes(5), new String[]{uri}, Boolean.FALSE);
        if (!stats.isEmpty()) {
            event.setViews(stats.size());
        }
        return event;
    }

    private Category validateCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Категория с id = %s не найдена!", catId),
                Category.class.getName()));
    }

    private LocalDateTime getStartDateTime(String rangeStart) {
        if (rangeStart == null) {
            return LocalDateTime.now();
        } else {
            return LocalDateTime.parse(rangeStart, FORMATTER);
        }
    }

    private LocalDateTime getEndDateTime(String rangeEnd) {
        if (rangeEnd == null) {
            return LocalDateTime.now().plusYears(10);
        } else {
            return LocalDateTime.parse(rangeEnd, FORMATTER);
        }
    }

    private void validateStartEndDates(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new BadRequestException("Дата конца выборки не может быть раньше даты начала выборки");
        }
    }
}
