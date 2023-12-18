package ru.practicum.service.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.CommentFullDto;
import ru.practicum.service.comment.dto.NewCommentDto;
import ru.practicum.service.comment.model.CommentState;
import ru.practicum.service.comment.model.CommentStateAction;
import ru.practicum.service.comment.repository.CommentRepository;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CommentDto> getAllEventsComments(long eventId, Integer from, Integer size) {
        return null;
    }

    @Override
    public CommentDto getCommentById(long commentId) {
        return null;
    }

    @Override
    public CommentFullDto addComment(long userId, long eventId, NewCommentDto newCommentDto) {
        return null;
    }

    @Override
    public CommentFullDto updateComment(long userId, long commentId, NewCommentDto newCommentDto) {
        return null;
    }

    @Override
    public void deleteComment(long userId, long commentId) {

    }

    @Override
    public List<CommentFullDto> getComments(Long userId, Long eventId, CommentState commentState, Integer from, Integer size) {
        return null;
    }

    @Override
    public CommentFullDto getUsersCommentById(long userId, long commentId) {
        return null;
    }

    @Override
    public CommentFullDto moderateComment(long commentId, CommentStateAction commentStateAction) {
        return null;
    }
}
