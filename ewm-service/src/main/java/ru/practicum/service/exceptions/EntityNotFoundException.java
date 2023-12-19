package ru.practicum.service.exceptions;

public class EntityNotFoundException extends RuntimeException {
    private final String notFoundClassName;

    public EntityNotFoundException(String message, String notFoundClassName) {
        super(message);
        this.notFoundClassName = notFoundClassName;
    }

    public String getNotFoundClassName() {
        return notFoundClassName;
    }
}

