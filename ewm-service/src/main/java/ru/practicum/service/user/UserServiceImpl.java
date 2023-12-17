package ru.practicum.service.user;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.StatsDto;
import ru.practicum.service.category.Category;
import ru.practicum.service.category.CategoryRepository;
import ru.practicum.service.event.Event;
import ru.practicum.service.event.EventFullDto;
import ru.practicum.service.event.EventMapper;
import ru.practicum.service.event.EventRepository;
import ru.practicum.service.event.EventShortDto;
import ru.practicum.service.event.EventStatus;
import ru.practicum.service.event.NewEventDto;
import ru.practicum.service.event.StateAction;
import ru.practicum.service.event.UpdateEventAdminRequest;
import ru.practicum.service.exceptions.BadRequestException;
import ru.practicum.service.exceptions.ConflictException;
import ru.practicum.service.exceptions.EntityNotFoundException;
import ru.practicum.service.location.LocationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> users, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (users == null || users.size() == 0) {
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        return userRepository.findAllByIdIn(users, pageable).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        validateUsersName(userDto);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public void deleteUser(Long userId) {
        validateUserById(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size) {
        validateUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventShortDto> list = eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(this::addViews)
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        return list;
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
    public EventFullDto getUsersEventById(Long userId, Long eventId) {
        validateUserById(userId);
        Event event = validateEventById(eventId);
        return EventMapper.toEventFullDto(addViews(event));
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventAdminRequest updateEvent) {
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
        EventMapper.updateEvent(eventToUpdate, updateEvent);
        if (updateEvent.getStateAction() != null &&
                updateEvent.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            eventToUpdate.setState(EventStatus.CANCELED);
        } else {
            eventToUpdate.setState(EventStatus.PENDING);
        }

        Event updatedEvent = eventRepository.save(eventToUpdate);
        return EventMapper.toEventFullDto(updatedEvent);

    }

    private User validateUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %s не найден!", userId),
                User.class.getName()));
    }

    private UserDto validateUsersName(UserDto userDto) {
        if (userRepository.countByName(userDto.getName()) > 0) {
            throw new ConflictException(
                    String.format("Пользователь с именем %s уже зарегистрирован.", userDto.getName()));
        }
        return userDto;
    }

    private Category validateCategoryById(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Категория с id = %s не найдена!", catId),
                Category.class.getName()));
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
}
