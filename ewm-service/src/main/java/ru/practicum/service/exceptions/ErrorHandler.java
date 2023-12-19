package ru.practicum.service.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO handleEntityNotFoundException(final EntityNotFoundException e) {
        log.error(String.format("Не найден объект класса %s.", e.getNotFoundClassName()));
        log.error(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler({BadRequestException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDTO handleBadRequestException(final Exception e) {
        log.error(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDTO handleConflictException(final Exception e) {
        log.error(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDTO handleInternalServerErrorException(final Exception e) {
        log.error(e.getMessage());
        return new ExceptionDTO(e.getMessage());
    }
}
