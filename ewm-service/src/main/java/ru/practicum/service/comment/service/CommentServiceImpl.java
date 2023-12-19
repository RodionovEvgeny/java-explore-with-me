package ru.practicum.service.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.CommentFullDto;
import ru.practicum.service.comment.dto.CommentMapper;
import ru.practicum.service.comment.dto.NewCommentDto;
import ru.practicum.service.comment.model.Comment;
import ru.practicum.service.comment.model.CommentState;
import ru.practicum.service.comment.model.CommentStateAction;
import ru.practicum.service.comment.repository.CommentRepository;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exceptions.ConflictException;
import ru.practicum.service.exceptions.EntityNotFoundException;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CommentDto> getAllEventsComments(long eventId, Integer from, Integer size) {
        validateEventById(eventId);
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findAllByEventIdAndState(eventId, CommentState.PUBLISHED, pageable).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(long commentId) {
        return CommentMapper.toCommentDto(validateCommentById(commentId));
    }

    @Override
    public CommentFullDto addComment(long userId, long eventId, NewCommentDto newCommentDto) {
        User user = validateUserById(userId);
        Event event = validateEventById(eventId);

        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .createdOn(LocalDateTime.now())
                .state(CommentState.PENDING)
                .event(event)
                .commentator(user)
                .build();
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    public CommentFullDto updateComment(long userId, long commentId, NewCommentDto newCommentDto) {
        validateUserById(userId);
        Comment comment = validateCommentById(commentId);
        validateUserAsCommentAuthor(userId, comment);
        comment.setText(newCommentDto.getText());
        comment.setState(CommentState.PENDING);
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(long userId, long commentId) {
        validateUserById(userId);
        Comment comment = validateCommentById(commentId);
        validateUserAsCommentAuthor(userId, comment);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentFullDto> getComments(Long userId, Long eventId, CommentState commentState, Integer from, Integer size) {
        return null;
    }

    @Override
    public CommentFullDto getUsersCommentById(long userId, long commentId) {
        validateUserById(userId);
        Comment comment = validateCommentById(commentId);
        validateUserAsCommentAuthor(userId, comment);
        return CommentMapper.toCommentFullDto(comment);
    }

    @Override
    public CommentFullDto moderateComment(long commentId, CommentStateAction commentStateAction) {
        Comment comment = validateCommentById(commentId);
        if (commentStateAction.equals(CommentStateAction.CONFIRM)) {
            comment.setState(CommentState.PUBLISHED);
        } else {
            comment.setState(CommentState.REJECTED);
        }
        return CommentMapper.toCommentFullDto(commentRepository.save(comment));
    }

    private void validateUserAsCommentAuthor(long userId, Comment comment) {
        if (comment.getCommentator().getId() != userId) {
            throw new ConflictException(
                    String.format("Пользователь с id = %s не является автором комментария сid = %s.",
                            userId, comment.getId()));
        }
    }

    private Comment validateCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Комментарий с id = %s не найден!", commentId),
                Comment.class.getName()));
    }

    private User validateUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Пользователь с id = %s не найден!", userId),
                User.class.getName()));
    }

    private Event validateEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Событие с id = %s не найдено!", eventId),
                Event.class.getName()));
    }
}
