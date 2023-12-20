package ru.practicum.service.comment.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.comment.dto.CommentFullDto;
import ru.practicum.service.comment.dto.NewCommentDto;
import ru.practicum.service.comment.model.CommentState;
import ru.practicum.service.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@AllArgsConstructor
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto addComment(@PathVariable long userId,
                                     @PathVariable long eventId,
                                     @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentFullDto updateComment(@PathVariable long userId,
                                        @PathVariable long commentId,
                                        @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.updateComment(userId, commentId, newCommentDto);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long userId,
                              @PathVariable long commentId) {
        commentService.deleteComment(userId, commentId);
    }

    @GetMapping("/comments")
    public List<CommentFullDto> getUsersComments(@PathVariable long userId,
                                                 @RequestParam(required = false) Long eventId,
                                                 @RequestParam(required = false) CommentState commentState,
                                                 @RequestParam(required = false) String rangeStart,
                                                 @RequestParam(required = false) String rangeEnd,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getComments(userId, eventId, commentState, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/comments/{commentId}")
    public CommentFullDto getUsersCommentById(@PathVariable long userId,
                                              @PathVariable long commentId) {
        return commentService.getUsersCommentById(userId, commentId);
    }
}
