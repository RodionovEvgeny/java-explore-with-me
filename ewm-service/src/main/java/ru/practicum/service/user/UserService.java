package ru.practicum.service.user;

import ru.practicum.service.event.EventFullDto;
import ru.practicum.service.event.EventShortDto;
import ru.practicum.service.event.NewEventDto;
import ru.practicum.service.event.UpdateEventAdminRequest;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> users, Integer from, Integer size);

    UserDto addUser(UserDto userDto);

    void deleteUser(Long userId);

    List<EventShortDto> getUsersEvents(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUsersEventById(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventAdminRequest updateEvent);
}
