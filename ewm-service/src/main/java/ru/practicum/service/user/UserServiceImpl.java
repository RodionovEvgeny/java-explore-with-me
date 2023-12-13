package ru.practicum.service.user;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.service.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public List<UserDto> getUsers(List<Long> users, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return userRepository.getAllUsers(users, pageable).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
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
}
