package ru.practicum.service.user;

public class UserMapper {

    public static UserShortDto toUserShortDto(User user){
        return UserShortDto.builder()
                .build();
    }
}
