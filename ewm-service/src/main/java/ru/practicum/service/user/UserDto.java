package ru.practicum.service.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
}
