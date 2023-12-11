package ru.practicum.service.location;

import ru.practicum.service.user.User;
import ru.practicum.service.user.UserShortDto;

public class LocationMapper {

    public static LocationDto toLocationDto(Location location){
        return LocationDto.builder()
                .build();
    }
}
