package ru.practicum.service.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserDto {
    private long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
}
