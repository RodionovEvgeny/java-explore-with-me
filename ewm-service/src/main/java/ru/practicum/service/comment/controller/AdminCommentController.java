package ru.practicum.service.comment.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.comment.dto.CommentFullDto;
import ru.practicum.service.comment.service.CommentService;
import ru.practicum.service.comment.model.CommentState;
import ru.practicum.service.comment.model.CommentStateAction;

import java.util.List;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/comments")
    public List<CommentFullDto> getAllComments(@RequestParam(required = false) Long userId,
                                               @RequestParam(required = false) Long eventId,
                                               @RequestParam(required = false) CommentState commentState,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getComments(userId, eventId, commentState, from, size);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentFullDto moderateComment(@PathVariable long commentId,
                                          @RequestParam CommentStateAction commentStateAction) {
        return commentService.moderateComment(commentId, commentStateAction);
    }


}
