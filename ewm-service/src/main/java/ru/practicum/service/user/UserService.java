package ru.practicum.service.user;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> users, Integer from, Integer size);

    UserDto addUser(UserDto userDto);

    void deleteUser(Long userId);
}
