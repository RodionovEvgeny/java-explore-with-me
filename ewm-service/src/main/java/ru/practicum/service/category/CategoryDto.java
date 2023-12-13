package ru.practicum.service.category;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Builder
@Data
public class CategoryDto {
    private Long id;
    @NotBlank
    private String name;
}
