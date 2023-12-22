package ru.practicum.service.comment.service;

import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.CommentFullDto;
import ru.practicum.service.comment.dto.NewCommentDto;
import ru.practicum.service.comment.model.CommentState;
import ru.practicum.service.comment.model.CommentStateAction;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllEventsComments(long eventId, Integer from, Integer size);

    CommentDto getCommentById(long commentId);

    CommentFullDto addComment(long userId, long eventId, NewCommentDto newCommentDto);

    CommentFullDto updateComment(long userId, long commentId, NewCommentDto newCommentDto);

    void deleteComment(long userId, long commentId);

    List<CommentFullDto> getComments(Long userId, Long eventId, CommentState commentState, String rangeStart, String rangeEnd, int from, int size);

    CommentFullDto getUsersCommentById(long userId, long commentId);

    CommentFullDto moderateComment(long commentId, CommentStateAction commentStateAction);
}
