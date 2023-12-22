package ru.practicum.service.comment.dto;

import ru.practicum.service.comment.model.Comment;
import ru.practicum.service.event.dto.EventMapper;
import ru.practicum.service.user.dto.UserMapper;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .commentator(UserMapper.toUserShortDto(comment.getCommentator()))
                .build();
    }

    public static CommentFullDto toCommentFullDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .commentator(UserMapper.toUserShortDto(comment.getCommentator()))
                .state(comment.getState())
                .event(EventMapper.toEventShortDto(comment.getEvent()))
                .build();
    }
}
