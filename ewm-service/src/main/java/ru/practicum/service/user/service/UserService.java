package ru.practicum.service.user.service;

import ru.practicum.service.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> users, Integer from, Integer size);

    UserDto addUser(UserDto userDto);

    void deleteUser(Long userId);
}
