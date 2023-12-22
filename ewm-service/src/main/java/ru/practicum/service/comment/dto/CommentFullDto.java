package ru.practicum.service.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.service.comment.model.CommentState;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentFullDto {

    private long id;
    private String text;
    private LocalDateTime createdOn;
    private CommentState state;
    private EventShortDto event;
    private UserShortDto commentator;
}
