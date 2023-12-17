package ru.practicum.service.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.exceptions.ConflictException;
import ru.practicum.service.exceptions.EntityNotFoundException;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.dto.UserMapper;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

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
}
