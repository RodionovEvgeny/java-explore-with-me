package ru.practicum.service.category;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
@Builder
public class Category {
    private Long id;
    private String name;
}
