package ru.practicum.service.category;

import ru.practicum.service.location.Location;
import ru.practicum.service.location.LocationDto;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category){
        return CategoryDto.builder()
                .build();
    }
}
