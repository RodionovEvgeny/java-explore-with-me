package ru.practicum.service.user;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
@Builder
public class User {
    private long id;
    private String email;
    private String name;
}
